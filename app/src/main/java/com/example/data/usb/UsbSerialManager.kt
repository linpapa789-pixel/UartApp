package com.example.data.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

data class UsbConnectionState(
    val isConnected: Boolean = false,
    val deviceName: String = "No USB Device",
    val driverType: String = "Auto Detect",
    val baudRate: Int = 115200,
    val encoding: String = "UTF-8",
    val bytesReceived: Long = 0,
    val bytesSent: Long = 0,
    val totalLines: Long = 0,
    val packetCount: Long = 0,
    val isSimulationActive: Boolean = false,
    val noiseDetected: Boolean = false,
    val crcOk: Boolean = true,
    val isAutoReconnectEnabled: Boolean = true,
    val statusMessage: String = "Ready"
)

enum class SimulationDeviceType {
    QUALCOMM_SNAPDRAGON_FAULT,
    MEDIATEK_DIMENSITY_GOOD,
    EXYNOS_BOOTLOOP,
    IPHONE_A16_PANIC
}

class UsbSerialManager(private val context: Context) {

    companion object {
        private const val TAG = "UsbSerialManager"
        private const val ACTION_USB_PERMISSION = "com.example.UART_USB_PERMISSION"
    }

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private val _connectionState = MutableStateFlow(UsbConnectionState())
    val connectionState: StateFlow<UsbConnectionState> = _connectionState.asStateFlow()

    private val _terminalOutput = MutableStateFlow("")
    val terminalOutput: StateFlow<String> = _terminalOutput.asStateFlow()

    private val ringBuffer = RingBuffer(65536)
    private var usbSerialPort: UsbSerialPort? = null
    private var readJob: Job? = null
    private var simulationJob: Job? = null
    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private var isReceiverRegistered = false

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(cntx: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { connectToDevice(it) }
                        } else {
                            _connectionState.value = _connectionState.value.copy(
                                statusMessage = "USB Permission Denied for ${device?.deviceName}"
                            )
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    Log.d(TAG, "USB Device Attached")
                    if (_connectionState.value.isAutoReconnectEnabled && !_connectionState.value.isConnected) {
                        autoConnectFirstAvailableDevice()
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    Log.d(TAG, "USB Device Detached")
                    disconnectPhysicalUsb("Device Unplugged")
                    if (_connectionState.value.isAutoReconnectEnabled) {
                        scheduleAutoReconnect()
                    }
                }
            }
        }
    }

    init {
        registerUsbReceiver()
    }

    fun registerUsbReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(ACTION_USB_PERMISSION)
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            }
            ContextCompat.registerReceiver(
                context,
                usbReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            isReceiverRegistered = true
        }
    }

    fun unregisterUsbReceiver() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(usbReceiver)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering receiver", e)
            }
            isReceiverRegistered = false
        }
    }

    fun setBaudRate(rate: Int) {
        _connectionState.value = _connectionState.value.copy(baudRate = rate)
        usbSerialPort?.let { port ->
            try {
                port.setParameters(rate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update baud rate on port", e)
            }
        }
    }

    fun setEncoding(enc: String) {
        _connectionState.value = _connectionState.value.copy(encoding = enc)
    }

    fun toggleAutoReconnect(enabled: Boolean) {
        _connectionState.value = _connectionState.value.copy(isAutoReconnectEnabled = enabled)
    }

    fun clearTerminal() {
        ringBuffer.clear()
        _terminalOutput.value = ""
        _connectionState.value = _connectionState.value.copy(
            bytesReceived = 0,
            bytesSent = 0,
            totalLines = 0,
            packetCount = 0
        )
    }

    fun appendLine(text: String) {
        val timestamp = "[${System.currentTimeMillis() % 100000}] "
        val newLine = "$timestamp$text\n"
        _terminalOutput.value += newLine
        _connectionState.value = _connectionState.value.copy(
            bytesReceived = _connectionState.value.bytesReceived + text.length,
            totalLines = _connectionState.value.totalLines + 1,
            packetCount = _connectionState.value.packetCount + 1
        )
    }

    /**
     * Real Physical USB Hardware Scan & Connection Flow
     */
    fun findAvailableDrivers(): List<UsbSerialDriver> {
        val defaultDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        return defaultDrivers
    }

    fun connectToPhysicalUsb(driver: UsbSerialDriver) {
        stopSimulation()
        val device = driver.device

        if (!usbManager.hasPermission(device)) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), flags)
            usbManager.requestPermission(device, permissionIntent)
            _connectionState.value = _connectionState.value.copy(
                statusMessage = "Requesting USB Permission for ${device.deviceName}..."
            )
            return
        }

        connectToDevice(device, driver)
    }

    private fun connectToDevice(device: UsbDevice, driverParam: UsbSerialDriver? = null) {
        scope.launch {
            try {
                val driver = driverParam ?: findAvailableDrivers().firstOrNull { it.device.deviceId == device.deviceId }
                if (driver == null) {
                    _connectionState.value = _connectionState.value.copy(
                        statusMessage = "No supported serial driver found for USB device."
                    )
                    return@launch
                }

                val connection = usbManager.openDevice(driver.device)
                if (connection == null) {
                    _connectionState.value = _connectionState.value.copy(
                        statusMessage = "Failed to open USB Device Connection."
                    )
                    return@launch
                }

                val port = driver.ports.firstOrNull()
                if (port == null) {
                    _connectionState.value = _connectionState.value.copy(
                        statusMessage = "No serial ports available on USB driver."
                    )
                    return@launch
                }

                port.open(connection)
                port.setParameters(
                    _connectionState.value.baudRate,
                    8,
                    UsbSerialPort.STOPBITS_1,
                    UsbSerialPort.PARITY_NONE
                )

                usbSerialPort = port

                _connectionState.value = _connectionState.value.copy(
                    isConnected = true,
                    deviceName = "${device.productName ?: "USB Serial Adapter"} (VID: 0x${Integer.toHexString(device.vendorId)})",
                    driverType = driver.javaClass.simpleName.replace("SerialDriver", ""),
                    isSimulationActive = false,
                    statusMessage = "Connected via Physical USB OTG Hardware"
                )

                startUsbReadLoop(port)

            } catch (e: Exception) {
                Log.e(TAG, "Error opening USB serial port", e)
                _connectionState.value = _connectionState.value.copy(
                    isConnected = false,
                    statusMessage = "Error: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun startUsbReadLoop(port: UsbSerialPort) {
        readJob?.cancel()
        readJob = scope.launch(Dispatchers.IO) {
            val readBuffer = ByteArray(4096)
            while (isActive && usbSerialPort != null) {
                try {
                    val bytesRead = port.read(readBuffer, 200)
                    if (bytesRead > 0) {
                        ringBuffer.write(readBuffer, bytesRead)

                        // Process full lines from RingBuffer
                        var line = ringBuffer.readLine()
                        while (line != null) {
                            appendLine(line)
                            line = ringBuffer.readLine()
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "USB Read IO Error", e)
                    disconnectPhysicalUsb("USB Read Connection Lost")
                    if (_connectionState.value.isAutoReconnectEnabled) {
                        scheduleAutoReconnect()
                    }
                    break
                }
            }
        }
    }

    fun disconnectPhysicalUsb(reason: String = "Disconnected by User") {
        readJob?.cancel()
        readJob = null
        try {
            usbSerialPort?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing USB serial port", e)
        }
        usbSerialPort = null
        _connectionState.value = _connectionState.value.copy(
            isConnected = false,
            statusMessage = reason
        )
    }

    fun autoConnectFirstAvailableDevice() {
        val drivers = findAvailableDrivers()
        if (drivers.isNotEmpty()) {
            connectToPhysicalUsb(drivers.first())
        }
    }

    private fun scheduleAutoReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            _connectionState.value = _connectionState.value.copy(statusMessage = "Attempting USB Auto-reconnect...")
            for (attempt in 1..5) {
                if (!isActive || _connectionState.value.isConnected) break
                delay(2000)
                val drivers = findAvailableDrivers()
                if (drivers.isNotEmpty()) {
                    connectToPhysicalUsb(drivers.first())
                    break
                }
            }
        }
    }

    /**
     * High-Fidelity Multi-Device Simulation Engine
     */
    fun startSimulation(type: SimulationDeviceType = SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT) {
        stopSimulation()
        disconnectPhysicalUsb("Switching to Simulated Serial Device")

        _connectionState.value = _connectionState.value.copy(
            isConnected = true,
            deviceName = when (type) {
                SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT -> "Qualcomm SM8550 (Snapdragon 8 Gen 2) - CH340 TTL"
                SimulationDeviceType.MEDIATEK_DIMENSITY_GOOD -> "MediaTek Dimensity 9300 - CP2102 TTL"
                SimulationDeviceType.EXYNOS_BOOTLOOP -> "Samsung Exynos 2400 - FT232 TTL"
                SimulationDeviceType.IPHONE_A16_PANIC -> "Apple A16 Bionic UART - PL2303 TTL"
            },
            driverType = when (type) {
                SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT -> "CH340G Serial"
                SimulationDeviceType.MEDIATEK_DIMENSITY_GOOD -> "CP2102 USB Bridge"
                SimulationDeviceType.EXYNOS_BOOTLOOP -> "FT232R USB UART"
                SimulationDeviceType.IPHONE_A16_PANIC -> "PL2303 Prolific"
            },
            isSimulationActive = true,
            statusMessage = "Simulated Serial Hardware Stream Running"
        )

        simulationJob = scope.launch {
            val logLines = getSimulatedLogLines(type)
            for (line in logLines) {
                if (!isActive) break
                appendLine(line)
                delay(100)
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        if (_connectionState.value.isSimulationActive) {
            _connectionState.value = _connectionState.value.copy(
                isConnected = false,
                isSimulationActive = false,
                statusMessage = "Simulation Stopped"
            )
        }
    }

    fun detectAvailableUsbDevices(): List<String> {
        val drivers = findAvailableDrivers()
        if (drivers.isEmpty()) {
            return listOf(
                "CH340G USB Serial Port (Simulated)",
                "CP2102 Serial Adapter (Simulated)",
                "FT232R USB UART (Simulated)"
            )
        }
        return drivers.map { driver ->
            val dev = driver.device
            "${dev.productName ?: "USB Serial Device"} [VID: 0x${Integer.toHexString(dev.vendorId)}, PID: 0x${Integer.toHexString(dev.productId)}]"
        }
    }

    // Export Session Functions
    fun exportTerminalText(): String = _terminalOutput.value

    private fun getSimulatedLogLines(type: SimulationDeviceType): List<String> = when (type) {
        SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT -> listOf(
            "Format: Log Type - Time(ms) - Message",
            "Format: Raw Serial Stream 115200 Baud",
            "B -    1000 - PBL, System Boot Start",
            "B -    1050 - BootROM Security Fuse Check OK",
            "B -    1100 - XBL Build Date: Nov 15 2025 14:32:01",
            "B -    1120 - pmic_init: PM8150 Revision 2.1 detected",
            "B -    1140 - pmic_vreg_set: VREG_S4A_1P8 set to 1800mV",
            "B -    1200 - ddr_phy_init: Training LPDDR5 Channel 0...",
            "B -    1220 - ddr_phy_init: Training LPDDR5 Channel 1...",
            "E -    1250 - ddr_training_error: DQ calibration failed at byte lane 2!",
            "E -    1260 - ddr_mismatch: Read 0xDEADBEEF expected 0x55AA55AA",
            "W -    1280 - pmic_pon_reason: Thermal / Fault Reset Triggered",
            "B -    1300 - Entering Emergency Download Mode (EDL 9008)",
            "B -    1350 - qdl_handler: Waiting for Qualcomm Firehose XML..."
        )
        SimulationDeviceType.MEDIATEK_DIMENSITY_GOOD -> listOf(
            "[0.00012] Preloader v3.12 (Dimensity 9300 MT6989)",
            "[0.00045] pmic_mt6358_init: Buck regulators initialized OK",
            "[0.00120] dram_init: LPDDR5X 16GB Dual Rank OK (8533 Mbps)",
            "[0.00340] ufs_init: KLU8G1GETD UFS 4.0 512GB initialized",
            "[0.00890] trustzone: TEE OS loaded, Keymaster 4.1 OK",
            "[0.01250] lk_main: Little Kernel booting Android arm64...",
            "[0.01800] avb_verify: vbmeta signature MATCHED OK",
            "[0.04500] dtbo_init: Applied device tree overlay for Display AMOLED 120Hz",
            "[0.12000] Linux kernel version 6.1.45 (android-build@google)",
            "[0.45000] init: Zygote started, SurfaceFlinger running",
            "[0.89000] Android Boot Completed. Launcher Ready."
        )
        SimulationDeviceType.EXYNOS_BOOTLOOP -> listOf(
            "S5P6818 S-BOOT v2.0 for Exynos 2400",
            "PMIC S2MPS18 Init... VDD_CPU=0.9V VDD_G3D=0.85V",
            "DRAM: 12GB LPDDR5 OK",
            "UFS: Samsung KLUEG8UHDB 256GB initialized",
            "BOOTLOADER: Loading boot.img from slot A...",
            "KERNEL: Starting Linux Kernel 6.1.0-exynos...",
            "INIT: Starting init daemon...",
            "INIT: Command 'mount_all /vendor/etc/fstab.exynos' failed!",
            "E - fs_mgr: Unable to mount /userdata (EXT4-fs error: corrupt GPT partition)",
            "W - init: Critical service 'zygote' respawning too rapidly!",
            "E - init: Kernel Panic: System crashed during userdata mount!",
            "Rebooting device in 3 seconds (Bootloop Loop #4)..."
        )
        SimulationDeviceType.IPHONE_A16_PANIC -> listOf(
            "iBoot-8419.80.7 (Apple A16 Bionic)",
            "Debug serial enabled @ 115200 8N1",
            "PMU: D2800 PMIC rail check... AVDD_CPU=1.15V OK",
            "RAM: LPDDR5 6GB Micron OK",
            "NAND: NVMe Flash controller initialization...",
            "NVMe: Read error at block 0x000FE420 (ANS2 Controller timeout)",
            "panic(cpu 0 caller 0xfffffff0072b4f80): \"NVMe fatal error: ANS2 controller unresponsive\"",
            "Backtrace (CPU 0): 0xfffffff007212000 0xfffffff0072b4f80",
            "Debugger waiting for USB connection..."
        )
    }
}
