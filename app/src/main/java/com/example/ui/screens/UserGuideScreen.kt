package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

@Composable
fun UserGuideScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    // Map to keep track of expanded accordion cards (all expanded by default for easy reading)
    val expandedStates = remember {
        mutableStateMapOf(
            0 to true,
            1 to true,
            2 to true,
            3 to true,
            4 to true,
            5 to true,
            6 to true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(14.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner Card
        GlassCard(borderColor = Color(0xFF06B6D4)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF06B6D4).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF06B6D4),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "UART PRO AI - အသုံးပြုပုံ အပြည့်အစုံ လမ်းညွှန်",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "မြန်မာဖုန်းပြင်ဆရာများအတွက် အဆင့်ဆင့် အသေအချာ ရှင်းလင်းချက်",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Section 0: Overview & Core Concept
        GuideAccordionItem(
            index = 0,
            title = "နိဒါန်းနှင့် UART PRO AI အလုပ်လုပ်ပုံ",
            icon = Icons.AutoMirrored.Filled.Help,
            iconTint = Color(0xFF06B6D4),
            isExpanded = expandedStates[0] == true,
            onToggle = { expandedStates[0] = !(expandedStates[0] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UART (Universal Asynchronous Receiver-Transmitter) ဆိုသည်မှာ ဖုန်း Motherboard ပေါ်ရှိ BootROM, CPU, PMIC (ပါဝါအိုင်စီ) နှင့် UFS/eMMC (မန်မိုရီ) တို့ စတင်အလုပ်လုပ်ချိန်တွင် ပြင်ပသို့ ထုတ်ပေးသော စက်ပိုင်းဆိုင်ရာ Boot Log သတင်းအချက်အလက်များဖြစ်ပါသည်။",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
                Text(
                    text = "UART PRO AI အက်ပလီကေးရှင်းသည် ဖုန်းစက်မနိုးခြင်း (Dead, Logo Hang, Restart, Bootloop) ပြဿနာကြုံတွေ့ရချိန်တွင် ဖုန်း၏ မားသားဘုတ်မှ တက်လာသော Boot Log ကို တိုက်ရိုက်ဖတ်ရှုပြီး ပျက်စီးနေသော IC နှင့် ပါဝါလိုင်းများကို AI နည်းပညာဖြင့် မြန်မာလို တိကျစွာ ခွဲခြားစစ်ဆေးပေးသည့် စနစ်ဖြစ်ပါသည်။",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }
        }

        // Section 1: Hardware Setup
        GuideAccordionItem(
            index = 1,
            title = "အခန်း (၁) - လိုအပ်သော Hardware များနှင့် ကြိုးဆက်သွယ်နည်း",
            icon = Icons.Default.Hardware,
            iconTint = Color(0xFFF59E0B),
            isExpanded = expandedStates[1] == true,
            onToggle = { expandedStates[1] = !(expandedStates[1] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၁.၁",
                    title = "လိုအပ်သော ပစ္စည်းများ စုဆောင်းပါ",
                    description = "• USB-to-TTL Serial Module (FT232RL, CP2102 သို့မဟုတ် CH340G)\n• OTG Adapter (Type-C / Micro-USB)\n• Soldering Iron & Jumper Wire (Motherboard Pin ပေါ်သို့ သဟိုဒါ ဆက်ရန်)"
                )

                GuideStepBox(
                    stepNumber = "၁.၂",
                    title = "Motherboard ပေါ်ရှိ Pinများ ချိတ်ဆက်နည်း",
                    description = "ဖုန်းမားသားဘုတ်မှ UART Pin (Test Point) များကို ရှာဖွေပြီး အောက်ပါအတိုင်း Cross ဆက်ပေးပါ-\n• Phone Board TXD  --->  USB Module RXD Pin\n• Phone Board RXD  --->  USB Module TXD Pin\n• Phone Board GND  --->  USB Module GND Pin"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚠️ သတိပြုရန် (Voltage Level Alert): စမတ်ဖုန်းအများစု၏ UART Voltage သည် 1.8V သာရှိပါသည်။ 5V သို့မဟုတ် 3.3V တိုက်ရိုက် သုံးမိပါက CPU/PMIC ပျက်စီးနိုင်သဖြင့် 1.8V Level Shifter ပါသော Module ကိုသာ သုံးစွဲပါ။",
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section 2: Terminal & Live Capture
        GuideAccordionItem(
            index = 2,
            title = "အခန်း (၂) - UART Terminal ဖြင့် Boot Log ဖတ်ရှုခြင်း",
            icon = Icons.Default.Terminal,
            iconTint = Color(0xFF10B981),
            isExpanded = expandedStates[2] == true,
            onToggle = { expandedStates[2] = !(expandedStates[2] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၂.၁",
                    title = "USB OTG ချိတ်ဆက်၍ Baud Rate ရွေးချယ်ပါ",
                    description = "၁။ USB Module ကို OTG ဖြင့် ဖုန်းသို့ ထိုးထည့်ပါ။ Pop-up ပေါ်လာပါက 'Allow USB Access' ပေးပါ။\n၂။ Terminal Screen သို့ သွားပြီး Baud Rate ကို ရွေးချယ်ပါ (Qualcomm / MTK ဖုန်းများအတွက် Standard 115200 သို့မဟုတ် 921600 ရွေးပါ)။"
                )

                GuideStepBox(
                    stepNumber = "၂.၂",
                    title = "Connect USB သို့မဟုတ် Simulation Mode စမ်းသပ်ပါ",
                    description = "၁။ 'CONNECT USB' ခလုတ်ကို နှိပ်၍ Device ချိတ်ဆက်ပါ။\n၂။ ပြုပြင်ရန် ဖုန်းအစစ်မရှိသေးပါက 'Start Qualcomm Log' သို့မဟုတ် 'MTK Log' သို့မဟုတ် 'iPhone A16 Boot Fault' ကို နှိပ်၍ Simulation Mode ဖြင့် လေ့ကျင့်နိုင်ပါသည်။"
                )

                GuideStepBox(
                    stepNumber = "၂.၃",
                    title = "Terminal Control များ အသုံးပြုပုံ",
                    description = "• Pause/Resume: တက်လာသော Log ကို ခဏရပ်၍ ဖတ်ချင်ပါက Pause ခလုတ်ကို နှိပ်ပါ။\n• Auto Scroll: စာကြောင်းအသစ်များ အလိုအလျောက် အောက်သို့ ရောက်စေရန် Auto Scroll ဖွင့်ထားပါ။\n• Filter Keyword: ERROR, WARN, FAIL စသော စာလုံးများကို ရိုက်ထည့်၍ သီးသန့် ရှာဖွေနိုင်ပါသည်။"
                )
            }
        }

        // Section 3: Log Compare Engine
        GuideAccordionItem(
            index = 3,
            title = "အခန်း (၃) - Log Compare စနစ်ဖြင့် စက်ကောင်း/စက်ပျက် ယှဉ်ပြိုင်စစ်ဆေးနည်း",
            icon = Icons.Default.Compare,
            iconTint = Color(0xFF8B5CF6),
            isExpanded = expandedStates[3] == true,
            onToggle = { expandedStates[3] = !(expandedStates[3] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၃.၁",
                    title = "Good Log နှင့် Fault Log ကို ရွေးချယ်ပါ",
                    description = "၁။ 'Log Compare' Screen သို့ သွားပါ။\n၂။ ဘယ်ဘက်တွင် 'Good Log Library' မှ စက်ကောင်း Boot Log ကို ရွေးယူပါ (သို့မဟုတ် Past သေချာ ထည့်ပါ)။\n၃။ ညာဘက်တွင် ယခု ဖတ်ရှုရရှိထားသော စက်ပျက် Log ကို ထည့်ပါ။"
                )

                GuideStepBox(
                    stepNumber = "၃.၂",
                    title = "LCS Structural Diff ဖြင့် ယှဉ်ပြိုင်ပါ",
                    description = "၁။ 'Run LCS Structural Log Diff' ခလုတ်ကို နှိပ်ပါ။\n၂။ စနစ်မှ Similarity Score (%) ကို တွက်ချက်ပေးပြီး စက်ကောင်းနှင့် စက်ပျက်ကြား ကွဲပြားသွားသော Boot Stage (ဥပမာ - RAM Init ပြီးနောက် UFS Read Timeout ဖြစ်သွားခြင်း) ကို အနီရောင်ဖြင့် စိစစ်ပြသပေးမည် ဖြစ်ပါသည်။"
                )
            }
        }

        // Section 4: AI Diagnostics & Brain Studio
        GuideAccordionItem(
            index = 4,
            title = "အခန်း (၄) - AI Assistant နှင့် Brain Studio ဖြင့် အမှားရှာခြင်း",
            icon = Icons.Default.AutoAwesome,
            iconTint = Color(0xFF06B6D4),
            isExpanded = expandedStates[4] == true,
            onToggle = { expandedStates[4] = !(expandedStates[4] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၄.၁",
                    title = "AI Auto Diagnostics ရယူခြင်း",
                    description = "၁။ Terminal သို့မဟုတ် Dashboard မှ 'AI REPAIR ASSISTANT' ခလုတ်ကို နှိပ်ပါ။\n၂။ Gemini 3.5 AI Core မှ Boot Log တစ်ခုလုံးကို သုံးသပ်ပြီး ပျက်စီးနေသော IC, ပျက်နေသော ပါဝါလိုင်း (VPH_PWR / VDD) နှင့် ပြုပြင်ရန် အဆင့်များကို မြန်မာဘာသာဖြင့် ထုတ်ပြန်ပေးပါမည်။"
                )

                GuideStepBox(
                    stepNumber = "၄.၂",
                    title = "AI Brain Studio တွင် Custom Rule များ ဖန်တီးခြင်း",
                    description = "၁။ 'AI Brain Studio' သို့ သွားပါ။\n၂။ Custom Rules တွင် မကြာခဏ ပျက်တတ်သော Log Keyword (ဥပမာ - 'pmic_fault', 'ufs_timeout') များကို ထည့်သွင်းထားပါက စနစ်မှ Log တက်လာသည်နှင့် အလိုအလျောက် သတိပေးချက် ထုတ်ပေးမည်ဖြစ်ပါသည်။"
                )
            }
        }

        // Section 5: Good Log Library & Dictionary
        GuideAccordionItem(
            index = 5,
            title = "အခန်း (၅) - Good Log Library နှင့် UART Dictionary အသုံးပြုပုံ",
            icon = Icons.Default.IntegrationInstructions,
            iconTint = Color(0xFFEC4899),
            isExpanded = expandedStates[5] == true,
            onToggle = { expandedStates[5] = !(expandedStates[5] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၅.၁",
                    title = "Good Log Library တွင် စက်ကောင်း Log များ သိမ်းဆည်းပုံ",
                    description = "ဖုန်းတစ်လုံးအား ပြုပြင်ပြီးပါက သို့မဟုတ် စက်ကောင်း ရောက်ရှိလာပါက Boot Log ကို ဖတ်ယူပြီး Brand, Model, Chipset အလိုက် Database တွင် သိမ်းဆည်းထားပါ။ နောက်နောင် စက်ပျက်လာပါက ယှဉ်ပြိုင်ကြည့်ရန် အလွန် အသုံးဝင်ပါသည်။"
                )

                GuideStepBox(
                    stepNumber = "၅.၂",
                    title = "UART Dictionary တွင် Boot Stage များ လေ့လာခြင်း",
                    description = "Boot Log ထဲတွင် ပါဝင်သော PBL, XBL, DDR_INIT, S-BOOT, TZ (TrustZone), Kernel Panic စသည့် နည်းပညာ အခေါ်အဝေါ်များကို Dictionary တွင် ရှာဖွေ၍ မြန်မာလို အသေးစိတ် လေ့လာနိုင်ပါသည်။"
                )
            }
        }

        // Section 6: Troubleshooting & FAQs
        GuideAccordionItem(
            index = 6,
            title = "အခန်း (၆) - မကြာခဏ ကြုံတွေ့ရသော ပြဿနာများနှင့် ဖြေရှင်းနည်းများ",
            icon = Icons.Default.BugReport,
            iconTint = Color(0xFFEF4444),
            isExpanded = expandedStates[6] == true,
            onToggle = { expandedStates[6] = !(expandedStates[6] ?: false) }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideStepBox(
                    stepNumber = "၆.၁",
                    title = "USB OTG မသိဘဲ ပိတ်နေပါက",
                    description = "ဖုန်း၏ Settings > Additional Settings > OTG Connection ကို ON ပေးပါ။ OTG Adapter အဆင်မပြေပါက အခြား OTG ကြိုးအသစ်ဖြင့် လဲလှယ်စမ်းသပ်ပါ။"
                )

                GuideStepBox(
                    stepNumber = "၆.၂",
                    title = "Terminal တွင် ဖတ်မရသော စာလုံးထွေများ (Garbage / Garbage Characters) ပေါ်နေပါက",
                    description = "Baud Rate သတ်မှတ်ချက် လွဲနေခြင်း ဖြစ်ပါသည်။ 115200 သို့မဟုတ် 921600 သို့ Baud Rate ပြောင်းလဲပေးပါ။ ထို့အပြင် Ground (GND) ကြိုး မခိုင်မြဲပါကလည်း စာလုံးထွေ တက်နိုင်ပါသည်။"
                )

                GuideStepBox(
                    stepNumber = "၆.၃",
                    title = "Log ဘာမျှ မတက်လာပါက",
                    description = "၁။ TXD နှင့် RXD ကြိုး လွဲနေခြင်း ဖြစ်နိုင်သဖြင့် ပြောင်းပြန် ပြန်ဆက်ကြည့်ပါ။\n၂။ Phone Motherboard တွင် UART Point အား Enable လုပ်ရန် Resistor ပါမပါ စစ်ဆေးပါ။"
                )
            }
        }
    }
}

@Composable
fun GuideAccordionItem(
    index: Int,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    GlassCard(borderColor = if (isExpanded) iconTint else Color(0x33FFFFFF)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun GuideStepBox(
    stepNumber: String,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B1B1F))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF06B6D4))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stepNumber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )
        }
    }
}
