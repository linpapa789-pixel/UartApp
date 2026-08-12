package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(viewModel: MainViewModel) {
    val aiResult by viewModel.aiAnalysisResult.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()

    var userMessage by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(14.dp)
    ) {
        // AI Hero Banner Bento Card
        GlassCard(
            borderColor = Color(0xFF06B6D4)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF06B6D4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GEMINI 3.5 FLASH AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "🇲🇲 မိုဘိုင်း ဖုန်းပြင်ဆရာများအတွက် အဆင့်မြင့် AI Hardware Diagnosis",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = { viewModel.analyzeCurrentTerminalWithAi() },
                    enabled = !isLoading && terminalOutput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                    } else {
                        Text("Analyze Log", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Output Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B1B1F))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                if (aiResult != null) {
                    Text(
                        text = "AI Diagnostic Report:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06B6D4)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aiResult ?: "",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFE3E2E6)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "UART Log ကို စစ်ဆေးရန် 'Analyze Log' နှိပ်ပါ သို့မဟုတ် AI ဖြင့် တိုက်ရိုက် မေးမြန်းပါ",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Chat history loop
                chatHistory.forEach { (user, ai) ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2E2E32))
                            .padding(10.dp)
                    ) {
                        Text("Q: $user", fontSize = 12.sp, color = Color(0xFF38BDF8))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A0A0A))
                            .padding(10.dp)
                    ) {
                        Text("AI: $ai", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                placeholder = { Text("Ask repair question (e.g., Qualcomm 9008 သို့ မည်သို့ဝင်မည်နည်း?)", fontSize = 11.sp, color = Color(0xFF64748B)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06B6D4),
                    unfocusedBorderColor = Color(0x1AFFFFFF),
                    focusedContainerColor = Color(0xFF1B1B1F),
                    unfocusedContainerColor = Color(0xFF1B1B1F),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val msg = userMessage
                    if (msg.isNotBlank()) {
                        userMessage = ""
                        scope.launch {
                            val response = viewModel.aiService.chatWithAi(chatHistory, msg)
                            chatHistory = chatHistory + Pair(msg, response)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF06B6D4))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.Black)
            }
        }
    }
}
