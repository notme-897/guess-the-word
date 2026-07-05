package com.example.guesstheword.ui.gameplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.data.WordRepository
import com.example.guesstheword.theme.CoinBadge
import com.example.guesstheword.theme.GameCard
import com.example.guesstheword.theme.GameIconButton
import com.example.guesstheword.theme.bouncyClickable
import kotlinx.coroutines.launch

private const val HINT_COST = 20

@Composable
fun GameplayScreen(
    modifier: Modifier = Modifier,
    onLevelComplete: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val level = remember { prefs.getCurrentLevel() }
    val puzzle = remember(level) { WordRepository.getPuzzleForLevel(context, level) }
    val scope = rememberCoroutineScope()

    // ── Savers for List types not natively supported by Bundle ─────────────
    val bankLettersSaver = Saver<List<Char>, String>(
        save    = { list -> list.joinToString(",") { it.code.toString() } },
        restore = { str  -> str.split(",").map { it.trim().toInt().toChar() } }
    )
    val boolListSaver = Saver<List<Boolean>, String>(
        save    = { list -> list.joinToString(",") { if (it) "1" else "0" } },
        restore = { str  -> str.split(",").map { it.trim() == "1" } }
    )
    val charNullableSaver = Saver<List<Char?>, String>(
        save    = { list -> list.joinToString(",") { (it?.code ?: -1).toString() } },
        restore = { str  -> str.split(",").map { val i = it.trim().toInt(); if (i == -1) null else i.toChar() } }
    )
    val intListSaver = Saver<List<Int>, String>(
        save    = { list -> list.joinToString(",") },
        restore = { str  -> str.split(",").map { it.trim().toInt() } }
    )

    // ── Mutable game state (rememberSaveable for configuration change safety) ─
    var bankLetters  by rememberSaveable(level, stateSaver = bankLettersSaver)  { mutableStateOf(puzzle.letterBank) }
    var bankUsed     by rememberSaveable(level, stateSaver = boolListSaver)     { mutableStateOf(List(puzzle.letterBank.size) { false }) }
    var answerSlots  by rememberSaveable(level, stateSaver = charNullableSaver) { mutableStateOf(List<Char?>(puzzle.word.length) { null }) }
    var answerSource by rememberSaveable(level, stateSaver = intListSaver)      { mutableStateOf(List(puzzle.word.length) { -1 }) }

    var coins        by rememberSaveable(level)        { mutableIntStateOf(prefs.getCoins()) }
    var earnedCoins  by rememberSaveable(level)        { mutableIntStateOf(0) }
    var isCorrect    by rememberSaveable(level)        { mutableStateOf(false) }
    var isWrong      by rememberSaveable(level)        { mutableStateOf(false) }
    var showGetReady by rememberSaveable(level)        { mutableStateOf(true) }
    var showExitConfirmDialog by rememberSaveable(level) { mutableStateOf(false) }

    // Shake animation
    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(level) {
        showGetReady = true
        kotlinx.coroutines.delay(1200)
        showGetReady = false
    }

    LaunchedEffect(isCorrect) {
        if (isCorrect) {
            android.util.Log.d("GuessWordNav", "GameplayScreen: isCorrect is true. Delaying 900ms before calling onLevelComplete...")
            kotlinx.coroutines.delay(900)
            android.util.Log.d("GuessWordNav", "GameplayScreen: Delay finished. Invoking onLevelComplete(word=${puzzle.word}, coins=$earnedCoins)")
            onLevelComplete(puzzle.word, earnedCoins)
        }
    }

    LaunchedEffect(isWrong) {
        if (isWrong) {
            kotlinx.coroutines.delay(700)
            isWrong = false
        }
    }

    fun doShake() = scope.launch {
        shakeAnim.snapTo(0f)
        shakeAnim.animateTo(0f, animationSpec = keyframes {
            durationMillis = 500
            -20f at 60
             20f at 120
            -16f at 190
             16f at 260
             -8f at 340
              0f at 420
        })
    }

    fun checkAnswer() {
        if (isCorrect) return  // guard against double invocation (e.g. rapid tap)
        val attempt = answerSlots.joinToString("") { it?.uppercase() ?: "" }
        android.util.Log.d("GuessWordNav", "checkAnswer: attempt=$attempt, puzzle.word=${puzzle.word}")
        if (attempt == puzzle.word.uppercase()) {
            val baseReward = puzzle.reward
            var bonus = 0
            if (level % 50 == 0) bonus += 500
            else if (level % 10 == 0) bonus += 100
            earnedCoins = baseReward + bonus
            coins += earnedCoins
            prefs.setCoins(coins)
            prefs.setCurrentLevel(level + 1)
            prefs.incrementWordsSolved()
            val newStreak = prefs.getStreak() + 1
            prefs.setStreak(newStreak)
            if (newStreak > prefs.getBestStreak()) {
                prefs.setBestStreak(newStreak)
            }
            android.util.Log.d("GuessWordNav", "checkAnswer: Answer is CORRECT! Setting isCorrect=true, earnedCoins=$earnedCoins")
            isCorrect = true  // set last so LaunchedEffect sees final earnedCoins
        } else {
            android.util.Log.d("GuessWordNav", "checkAnswer: Answer is WRONG. Resetting streak.")
            isWrong = true
            prefs.setStreak(0)
            doShake()
        }
    }

    fun removeLetterAt(slotIndex: Int) {
        if (isCorrect) return
        val sourceIndex = answerSource[slotIndex]
        if (sourceIndex >= 0) {
            bankUsed = bankUsed.toMutableList().apply { this[sourceIndex] = false }
        }
        answerSlots = answerSlots.toMutableList().apply { this[slotIndex] = null }
        answerSource = answerSource.toMutableList().apply { this[slotIndex] = -1 }
    }

    fun placeLetter(bankIndex: Int) {
        if (bankUsed[bankIndex] || isCorrect) return
        val slot = answerSlots.indexOfFirst { it == null }
        if (slot < 0) return

        answerSlots = answerSlots.toMutableList().apply { this[slot] = bankLetters[bankIndex] }
        answerSource = answerSource.toMutableList().apply { this[slot] = bankIndex }
        bankUsed = bankUsed.toMutableList().apply { this[bankIndex] = true }

        if (answerSlots.all { it != null }) {
            checkAnswer()
        }
    }

    fun useHint() {
        if (coins < HINT_COST || isCorrect) return

        // 1. Find the first slot index that is incorrect (empty OR has a wrong character)
        var targetSlotIdx = -1
        for (i in 0 until puzzle.word.length) {
            val placedChar = answerSlots[i]
            val correctChar = puzzle.word[i].uppercaseChar()
            if (placedChar == null || placedChar.uppercaseChar() != correctChar) {
                targetSlotIdx = i
                break
            }
        }
        if (targetSlotIdx < 0) return // No incorrect slots

        val targetCorrectChar = puzzle.word[targetSlotIdx].uppercaseChar()

        // 2. If the slot has a wrong character, clear it and return it to the bank first
        if (answerSlots[targetSlotIdx] != null) {
            removeLetterAt(targetSlotIdx)
        }

        // 3. Check if the target correct character is placed in some other WRONG slot.
        // If so, clear that wrong slot first to free the character back to the bank.
        for (s in 0 until puzzle.word.length) {
            if (s != targetSlotIdx && answerSlots[s]?.uppercaseChar() == targetCorrectChar) {
                if (puzzle.word[s].uppercaseChar() != targetCorrectChar) {
                    removeLetterAt(s)
                }
            }
        }

        // 4. Find the correct character in the bank that is currently unused
        val bankIndex = bankLetters.indices.firstOrNull { idx ->
            bankLetters[idx].uppercaseChar() == targetCorrectChar && !bankUsed[idx]
        }

        if (bankIndex != null) {
            coins -= HINT_COST
            prefs.setCoins(coins)
            placeLetter(bankIndex)
        }
    }

    fun deleteLast() {
        if (isCorrect) return
        val lastFilledIdx = answerSlots.indexOfLast { it != null }
        if (lastFilledIdx >= 0) {
            removeLetterAt(lastFilledIdx)
        }
    }

    fun shuffleBank() {
        if (isCorrect) return
        val unusedIndices = bankLetters.indices.filter { !bankUsed[it] }
        val unusedLetters = unusedIndices.map { bankLetters[it] }.shuffled()
        val newBankLetters = bankLetters.toMutableList()
        unusedIndices.forEachIndexed { i, originalIdx ->
            newBankLetters[originalIdx] = unusedLetters[i]
        }
        bankLetters = newBankLetters
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val cardBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            val isTablet = maxWidth > 600.dp
            val contentWidthFraction = if (isTablet) 0.65f else 1f

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Top Bar ───────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(contentWidthFraction),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameIconButton(
                        icon = "←",
                        onClick = {
                            if (answerSlots.any { it != null } && !isCorrect) {
                                showExitConfirmDialog = true
                            } else {
                                onBack()
                            }
                        }
                    )

                    Text(
                        text = "Level $level",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = textPrimary)
                    )

                    CoinBadge(coins = coins)
                }

                Spacer(Modifier.height(28.dp))

                // ── Category Pill & Clue Card ──────────────────────────────────────
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(primaryColor.copy(alpha = 0.1f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = puzzle.categoryLabel.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Clue Card (Emoji + Hint)
                GameCard(
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .defaultMinSize(minHeight = 160.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = puzzle.emoji,
                            fontSize = 72.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = puzzle.hint,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Answer Slots ──────────────────────────────────────────────────
                val wordLen = puzzle.word.length
                val slotBoxSize = if (wordLen > 12) 28.dp else if (wordLen > 8) 36.dp else if (wordLen > 5) 46.dp else 56.dp
                val slotFontSize = if (wordLen > 12) 13.sp else if (wordLen > 8) 18.sp else if (wordLen > 5) 21.sp else 24.sp
                val slotCornerRadius = if (wordLen > 12) 6.dp else if (wordLen > 8) 9.dp else if (wordLen > 5) 12.dp else 16.dp

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        if (wordLen > 12) 4.dp else if (wordLen > 8) 6.dp else 8.dp,
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .graphicsLayer { translationX = shakeAnim.value }
                ) {
                    answerSlots.forEachIndexed { idx, char ->
                        AnswerSlotBox(
                            char = char,
                            isCorrect = isCorrect,
                            isWrong = isWrong && char != null,
                            boxSize = slotBoxSize,
                            fontSize = slotFontSize,
                            cornerRadius = slotCornerRadius,
                            onClick = { removeLetterAt(idx) }
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                // ── Letter bank (dynamically sizing and wrapping) ─────────────────
                val rowChunkSize = if (bankLetters.size > 12) 6 else if (bankLetters.size > 8) 5 else 4
                val tileSize = if (bankLetters.size > 12) 46.dp else if (bankLetters.size > 8) 52.dp else 58.dp
                val letterFontSize = if (bankLetters.size > 12) 18.sp else if (bankLetters.size > 8) 21.sp else 24.sp

                Column(
                    modifier = Modifier.fillMaxWidth(contentWidthFraction),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bankLetters.indices.chunked(rowChunkSize).forEach { rowIndices ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowIndices.forEach { idx ->
                                LetterTile(
                                    letter = bankLetters[idx],
                                    isUsed = bankUsed[idx],
                                    tileSize = tileSize,
                                    fontSize = letterFontSize,
                                    onClick = { placeLetter(idx) }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                // ── Action buttons ────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(contentWidthFraction),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        text = "💡 Hint (-20)",
                        enabled = coins >= HINT_COST && !isCorrect,
                        modifier = Modifier.weight(1f),
                        onClick = { useHint() }
                    )
                    ActionButton(
                        text = "⌫ Delete",
                        enabled = answerSlots.any { it != null } && !isCorrect,
                        modifier = Modifier.weight(1f),
                        onClick = { deleteLast() }
                    )
                    ActionButton(
                        text = "🔀 Shuffle",
                        enabled = !isCorrect,
                        modifier = Modifier.weight(1f),
                        onClick = { shuffleBank() }
                    )
                }
            }
        }

        // ── Get Ready overlay ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showGetReady,
            enter = fadeIn(tween(300)),
            exit  = fadeOut(tween(400, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(primaryColor, primaryColor.copy(alpha = 0.8f)))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LEVEL $level",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = Color.White,
                            letterSpacing = 3.sp
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Get Ready!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // ── Exit Confirmation Dialog ──────────────────────────────────────────
        val inProgress = answerSlots.any { it != null } && !isCorrect
        BackHandler(enabled = inProgress) {
            showExitConfirmDialog = true
        }

        if (showExitConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showExitConfirmDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        "Exit Game?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    )
                },
                text = {
                    Text(
                        "You have a level in progress. Are you sure you want to exit to the Main Menu? Your progress for this level will not be saved.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = textSecondary)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitConfirmDialog = false
                            onBack()
                        }
                    ) {
                        Text("Exit", style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitConfirmDialog = false }) {
                        Text("Keep Playing", style = MaterialTheme.typography.labelLarge.copy(color = textSecondary))
                    }
                }
            )
        }
    }
}

@Composable
private fun AnswerSlotBox(
    char: Char?,
    isCorrect: Boolean,
    isWrong: Boolean,
    boxSize: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(char) {
        if (char != null) {
            scaleAnim.animateTo(1.15f, animationSpec = tween(70, easing = FastOutSlowInEasing))
            scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val successColor = MaterialTheme.colorScheme.tertiary

    val tileBrush = when {
        isCorrect && char != null -> Brush.verticalGradient(listOf(successColor.copy(alpha = 0.9f), successColor))
        isWrong && char != null   -> Brush.verticalGradient(listOf(errorColor.copy(alpha = 0.9f), errorColor))
        char != null              -> Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.85f), primaryColor))
        else                      -> null
    }

    val borderModifier = if (char == null) {
        Modifier.border(1.5.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f), RoundedCornerShape(cornerRadius))
    } else Modifier

    val textColor = Color.White

    val boxModifier = if (char != null) {
        Modifier
            .size(boxSize)
            .scale(scaleAnim.value)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(cornerRadius), clip = false)
            .clip(RoundedCornerShape(cornerRadius))
            .background(tileBrush ?: Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
            .bouncyClickable(enabled = !isCorrect) { onClick() }
    } else {
        Modifier
            .size(boxSize)
            .scale(scaleAnim.value)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
            .then(borderModifier)
    }

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(
                text = char.toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

@Composable
private fun LetterTile(
    letter: Char,
    isUsed: Boolean,
    tileSize: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (isUsed) 0f else 1f,
        animationSpec = tween(150),
        label = "tile_alpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(tileSize)
            .alpha(alpha)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.85f), primaryColor))
            )
            .bouncyClickable(enabled = !isUsed) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "act_btn_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
            disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        ),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
