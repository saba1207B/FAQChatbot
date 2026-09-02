package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FaqDataset
import com.example.model.ChatMessage
import com.example.model.FaqItem
import com.example.model.Sender
import com.example.ui.theme.Amber500
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()
    val threshold by viewModel.threshold.collectAsStateWithLifecycle()
    val showMathDialog by viewModel.showMathDialog.collectAsStateWithLifecycle()
    val showFaqDrawer by viewModel.showFaqDrawer.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var isNlpPanelExpanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll when new messages arrive or typing status changes
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            ChatTopBar(
                threshold = threshold,
                onToggleNlp = { isNlpPanelExpanded = !isNlpPanelExpanded },
                onOpenDataset = { viewModel.toggleFaqDrawer(true) },
                onOpenMath = { viewModel.toggleMathDialog(true) },
                onClearChat = { viewModel.clearChat() }
            )

            // Expandable NLP Configuration Panel
            AnimatedVisibility(
                visible = isNlpPanelExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NlpConfigCard(
                    threshold = threshold,
                    onThresholdChange = { viewModel.updateThreshold(it) },
                    vocabSize = viewModel.vocabularySize,
                    faqCount = viewModel.totalFaqsCount
                )
            }

            // Quick Sample Question Chips Row
            SampleQuestionsBar(
                onSampleSelected = { sample ->
                    viewModel.sendMessage(sample)
                }
            )

            // Message History LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onSuggestionClick = { question ->
                            viewModel.sendMessage(question)
                        }
                    )
                }

                if (isTyping) {
                    item {
                        TypingIndicatorBubble()
                    }
                }
            }

            // Bottom Input Bar
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            )
        }

        // Math & Algorithm Info Dialog
        if (showMathDialog) {
            NlpMathDialog(onDismiss = { viewModel.toggleMathDialog(false) })
        }

        // FAQ Dataset Bottom Sheet
        if (showFaqDrawer) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleFaqDrawer(false) },
                sheetState = sheetState,
                containerColor = Slate900,
                dragHandle = null
            ) {
                FaqDatasetSheetContent(
                    viewModel = viewModel,
                    onQuestionSelected = { q ->
                        viewModel.toggleFaqDrawer(false)
                        viewModel.sendMessage(q)
                    },
                    onClose = { viewModel.toggleFaqDrawer(false) }
                )
            }
        }
    }
}

@Composable
private fun ChatTopBar(
    threshold: Float,
    onToggleNlp: () -> Unit,
    onOpenDataset: () -> Unit,
    onOpenMath: () -> Unit,
    onClearChat: () -> Unit
) {
    Surface(
        color = Slate900,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Indigo500, Cyan500))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Bot Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CodeAlpha FAQ Bot",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Emerald400)
                        )
                    }
                    Text(
                        text = "TF-IDF & Cosine Similarity (θ = ${"%.2f".format(threshold)})",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onToggleNlp,
                    modifier = Modifier.testTag("nlp_config_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "NLP Config",
                        tint = Indigo400
                    )
                }
                IconButton(
                    onClick = onOpenDataset,
                    modifier = Modifier.testTag("dataset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Browse FAQ Dataset",
                        tint = Cyan400
                    )
                }
                IconButton(
                    onClick = onOpenMath,
                    modifier = Modifier.testTag("math_info_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "NLP Math Info",
                        tint = Slate300
                    )
                }
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("clear_chat_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear Conversation",
                        tint = Slate400
                    )
                }
            }
        }
    }
}

@Composable
private fun NlpConfigCard(
    threshold: Float,
    onThresholdChange: (Float) -> Unit,
    vocabSize: Int,
    faqCount: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Similarity Threshold",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate100
                )
                Text(
                    text = "θ = ${"%.2f".format(threshold)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Indigo400
                )
            }

            Slider(
                value = threshold,
                onValueChange = onThresholdChange,
                valueRange = 0.05f..0.80f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = Indigo500,
                    activeTrackColor = Indigo500,
                    inactiveTrackColor = Slate700
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("More permissive (0.05)", fontSize = 10.sp, color = Slate400)
                Text("Strict match (0.80)", fontSize = 10.sp, color = Slate400)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Slate800, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Vocabulary Size", fontSize = 10.sp, color = Slate400)
                        Text("$vocabSize features", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Slate800, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("FAQ Knowledge Base", fontSize = 10.sp, color = Slate400)
                        Text("$faqCount questions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleQuestionsBar(
    onSampleSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FaqDataset.SAMPLE_QUESTIONS.forEach { sample ->
            Surface(
                color = Slate800,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                modifier = Modifier.clickable { onSampleSelected(sample) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Indigo400,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = sample,
                        fontSize = 11.sp,
                        color = Slate300
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onSuggestionClick: (String) -> Unit
) {
    val isUser = message.sender == Sender.USER
    var isBreakdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Indigo600, Cyan500))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Bot",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                // Confidence & Matching Status Pill for bot messages
                if (!isUser && message.similarityScore > 0f) {
                    val (badgeColor, badgeBg, badgeIcon) = if (message.matched) {
                        if (message.similarityScore >= 0.45f) {
                            Triple(Emerald400, Emerald500.copy(alpha = 0.15f), Icons.Default.CheckCircle)
                        } else {
                            Triple(Amber500, Amber500.copy(alpha = 0.15f), Icons.Default.CheckCircle)
                        }
                    } else {
                        Triple(Rose500, Rose500.copy(alpha = 0.15f), Icons.Default.Warning)
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (message.matched) "${message.confidencePercentage}% Cosine Match" else "Below Threshold (${message.confidencePercentage}%)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                }

                // Bubble Body
                Surface(
                    color = if (isUser) Indigo600 else Slate900,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Slate700) else null,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        // If matched, offer expandable vector inspect accordion
                        if (!isUser && message.matchedFaq != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate800)
                                    .clickable { isBreakdownOpen = !isBreakdownOpen }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NLP Vector Insights",
                                    fontSize = 10.sp,
                                    color = Indigo400,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = if (isBreakdownOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Indigo400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            AnimatedVisibility(visible = isBreakdownOpen) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp)
                                        .background(Slate950, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Matched FAQ: \"${message.matchedFaq.question}\"",
                                        fontSize = 10.sp,
                                        color = Slate300,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Category: ${message.matchedFaq.category} • Score: ${"%.4f".format(message.similarityScore)}",
                                        fontSize = 10.sp,
                                        color = Cyan400
                                    )
                                    if (message.processedTokens.isNotEmpty()) {
                                        Text(
                                            text = "Extracted Tokens: [${message.processedTokens}]",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Slate400
                                        )
                                    }
                                }
                            }
                        }

                        // Top suggestions if fallback triggered
                        if (!isUser && !message.matched && message.topSuggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Did you mean one of these?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate300
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            message.topSuggestions.forEach { suggestion ->
                                Surface(
                                    color = Slate800,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable { onSuggestionClick(suggestion.question) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = suggestion.question,
                                            fontSize = 11.sp,
                                            color = Slate100,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${suggestion.confidencePercentage}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Cyan400
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = message.timestamp,
                    fontSize = 9.sp,
                    color = Slate400,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicatorBubble() {
    val transition = rememberInfiniteTransition(label = "dots")
    val alpha1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), repeatMode = RepeatMode.Reverse),
        label = "d1"
    )
    val alpha2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse),
        label = "d2"
    )
    val alpha3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse),
        label = "d3"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Indigo600),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = Slate900,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Indigo400.copy(alpha = alpha1)))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Indigo400.copy(alpha = alpha2)))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Indigo400.copy(alpha = alpha3)))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Computing TF-IDF vector & cosine similarities...", fontSize = 10.sp, color = Slate400)
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Slate900,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask a question (e.g., 'API rate limits?')...", fontSize = 12.sp, color = Slate400) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedBorderColor = Indigo500,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { onTextChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (text.isNotBlank()) Brush.linearGradient(listOf(Indigo500, Indigo600))
                        else Brush.linearGradient(listOf(Slate800, Slate800))
                    )
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) Color.White else Slate700,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NlpMathDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = Cyan400)
                Spacer(modifier = Modifier.width(8.dp))
                Text("How TF-IDF & Cosine Similarity Works", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate100)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "1. TF (Term Frequency)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Indigo400
                )
                Text(
                    text = "Measures how frequently term t appears in document d:\nTF(t, d) = 1 + ln(count(t, d))",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate300,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "2. IDF (Inverse Document Frequency)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Indigo400
                )
                Text(
                    text = "Penalizes common words and boosts rare informative keywords:\nIDF(t, D) = ln((N + 1) / (df(t) + 1)) + 1",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate300,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "3. Cosine Similarity Formula",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Cyan400
                )
                Text(
                    text = "Computes the cosine angle between query vector q and document vector d:\nCosine(q, d) = (q · d) / (||q||₂ × ||d||₂)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate300,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "4. Threshold Decision Rule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Emerald400
                )
                Text(
                    text = "• If max(Cosine(q, d)) ≥ θ: Return matched FAQ Answer\n• If max(Cosine(q, d)) < θ: Trigger Graceful Fallback & Top Suggestions",
                    fontSize = 11.sp,
                    color = Slate300,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It", color = Indigo400)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FaqDatasetSheetContent(
    viewModel: ChatViewModel,
    onQuestionSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val searchFilter by viewModel.searchFilter.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredFaqs = viewModel.getFilteredFaqs()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 650.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("FAQ Knowledge Base", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate100)
                Text("${filteredFaqs.size} questions available in corpus", fontSize = 11.sp, color = Slate400)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchFilter,
            onValueChange = { viewModel.setSearchFilter(it) },
            placeholder = { Text("Filter FAQs...", fontSize = 12.sp, color = Slate400) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Slate800,
                unfocusedContainerColor = Slate800,
                focusedBorderColor = Indigo500,
                unfocusedBorderColor = Slate700,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            viewModel.categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedCategory(cat) },
                    label = { Text(cat, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Indigo600,
                        selectedLabelColor = Color.White,
                        containerColor = Slate800,
                        labelColor = Slate300
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredFaqs, key = { it.id }) { faq ->
                FaqSheetItemCard(
                    faq = faq,
                    onClick = { onQuestionSelected(faq.question) }
                )
            }
        }
    }
}

@Composable
private fun FaqSheetItemCard(
    faq: FaqItem,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Slate700, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Indigo500.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = faq.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Indigo400,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text("Tap to ask", fontSize = 9.sp, color = Cyan400)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = faq.question,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Slate100
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = faq.answer,
                fontSize = 11.sp,
                color = Slate400,
                lineHeight = 15.sp,
                maxLines = 3
            )
        }
    }
}
