package com.example.data.usb

import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread-safe high-speed Circular RingBuffer for high baud-rate (e.g., 921600)
 * byte streaming without GC allocation churn or data dropping.
 */
class RingBuffer(val capacity: Int = 65536) {
    private val buffer = ByteArray(capacity)
    private var writeIndex = 0
    private var readIndex = 0
    private val availableBytes = AtomicInteger(0)

    @Synchronized
    fun write(data: ByteArray, length: Int): Int {
        val bytesToWrite = minOf(length, capacity - availableBytes.get())
        if (bytesToWrite <= 0) return 0 // Buffer overflow protection

        for (i in 0 until bytesToWrite) {
            buffer[writeIndex] = data[i]
            writeIndex = (writeIndex + 1) % capacity
        }
        availableBytes.addAndGet(bytesToWrite)
        return bytesToWrite
    }

    @Synchronized
    fun read(destination: ByteArray, length: Int): Int {
        val bytesToRead = minOf(length, availableBytes.get())
        if (bytesToRead <= 0) return 0

        for (i in 0 until bytesToRead) {
            destination[i] = buffer[readIndex]
            readIndex = (readIndex + 1) % capacity
        }
        availableBytes.addAndGet(-bytesToRead)
        return bytesToRead
    }

    @Synchronized
    fun readLine(): String? {
        if (availableBytes.get() == 0) return null

        val tempIndex = readIndex
        var foundNewline = false
        var lineLength = 0

        for (i in 0 until availableBytes.get()) {
            val idx = (tempIndex + i) % capacity
            val b = buffer[idx]
            lineLength++
            if (b == '\n'.code.toByte() || b == '\r'.code.toByte()) {
                foundNewline = true
                break
            }
        }

        if (!foundNewline && lineLength < availableBytes.get()) {
            return null // Incomplete line, wait for newline
        }

        val lineBytes = ByteArray(lineLength)
        read(lineBytes, lineLength)
        return String(lineBytes).trimEnd('\r', '\n')
    }

    fun size(): Int = availableBytes.get()

    @Synchronized
    fun clear() {
        writeIndex = 0
        readIndex = 0
        availableBytes.set(0)
    }
}
