package com.example.guesstheword.ui.levelcomplete

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.graphics.Brush
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.theme.GameButton
import com.example.guesstheword.theme.GameCard
import com.example.guesstheword.theme.bouncyClickable
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Confetti(
    val x: Float,
    val phase: Float,
    val speed: Float,
    val drift: Float,
    val color: Color,
    val widthDp: Float,
    val heightDp: Float,
    val initialRotation: Float,
    val rotationSpeed: Float
)

private val confettiColors = listOf(
    Color(0xFFFFC107), Color(0xFF4F7CFF), Color(0xFF34C759),
    Color(0xFFFF3B30), Color(0xFFAF52DE), Color(0xFFFF9500), Color(0xFF00C7BE)
)

private fun makeConfetti() = List(60) {
    Confetti(
        x = Random.nextFloat(),
        phase = Random.nextFloat(),
        speed = Random.nextFloat() * 0.6f + 0.7f,
        drift = (Random.nextFloat() - 0.5f) * 0.15f,
        color = confettiColors[Random.nextInt(confettiColors.size)],
        widthDp = Random.nextFloat() * 7f + 5f,
        heightDp = Random.nextFloat() * 5f + 3f,
        initialRotation = Random.nextFloat() * 360f,
        rotationSpeed = (Random.nextFloat() - 0.5f) * 4f
    )
}

@Composable
fun LevelCompleteScreen(
    word: String,
    coinsEarned: Int,
    modifier: Modifier = Modifier,
    onNextLevel: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val streak = remember { prefs.getStreak() }
    val totalCoins = remember { prefs.getCoins() }

    val pieces = remember { makeConfetti() }
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val confettiProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000)),
        label = "confetti_progress"
    )

    var star1Visible by remember { mutableStateOf(false) }
    var star2Visible by remember { mutableStateOf(false) }
    var star3Visible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var wordVisible by remember { mutableStateOf(false) }
    var rewardVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }
    var displayCoins by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(100); star1Visible = true
        delay(150); star2Visible = true
        delay(150); star3Visible = true
        delay(100); titleVisible = true
        delay(100); wordVisible = true
        delay(150); rewardVisible = true; displayCoins = coinsEarned
        delay(200); buttonVisible = true
    }

    val star1Scale by animateFloatAsState(
        targetValue = if (star1Visible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium),
        label = "s1"
    )
    val star2Scale by animateFloatAsState(
        targetValue = if (star2Visible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium),
        label = "s2"
    )
    val star3Scale by animateFloatAsState(
        targetValue = if (star3Visible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium),
        label = "s3"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "title"
    )
    val wordAlpha by animateFloatAsState(
        targetValue = if (wordVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "word"
    )
    val rewardAlpha by animateFloatAsState(
        targetValue = if (rewardVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "reward"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "button"
    )
    val animatedCoins by animateIntAsState(
        targetValue = displayCoins,
        animationSpec = tween(700),
        label = "coins_count"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val successColor = MaterialTheme.colorScheme.tertiary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val cardBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Confetti Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalH = size.height * 1.15f
            pieces.forEach { p ->
                val progress = ((confettiProgress * p.speed + p.phase) % 1f)
                val px = (p.x + p.drift * progress).coerceIn(0f, 1f) * size.width
                val py = progress * totalH - totalH * 0.05f
                val rot = p.initialRotation + progress * 360f * p.rotationSpeed
                val alpha = if (progress > 0.82f) (1f - progress) / 0.18f else 1f
                val w = p.widthDp * density
                val h = p.heightDp * density

                withTransform({ rotate(rot, Offset(px, py)) }) {
                    drawRect(
                        color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                        topLeft = Offset(px - w / 2f, py - h / 2f),
                        size = Size(w, h)
                    )
                }
            }
        }

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
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(24.dp))

                // Stars row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 42.sp, modifier = Modifier.scale(star1Scale))
                    Text("⭐", fontSize = 56.sp, modifier = Modifier.scale(star2Scale))
                    Text("⭐", fontSize = 42.sp, modifier = Modifier.scale(star3Scale))
                }

                Spacer(Modifier.height(24.dp))

                // Title
                Text(
                    text = "LEVEL COMPLETE!",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = successColor,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(titleAlpha)
                )

                Spacer(Modifier.height(28.dp))

                // Correct word card
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(wordAlpha)
                ) {
                    Text(
                        text = "Correct Word",
                        style = MaterialTheme.typography.labelLarge.copy(color = textSecondary)
                    )
                    Spacer(Modifier.height(12.dp))
                    
                    val wordLen = word.length
                    val boxSize = if (wordLen > 12) 26.dp else if (wordLen > 8) 34.dp else if (wordLen > 5) 44.dp else 54.dp
                    val fontSize = if (wordLen > 12) 13.sp else if (wordLen > 8) 17.sp else if (wordLen > 5) 20.sp else 24.sp
                    val cornerRadius = if (wordLen > 12) 6.dp else if (wordLen > 8) 9.dp else if (wordLen > 5) 12.dp else 16.dp
                    val spacing = if (wordLen > 12) 3.dp else if (wordLen > 8) 5.dp else 8.dp

                    val goldBrush = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706))) // Premium gold gradient
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        word.uppercase().forEach { char ->
                            Box(
                                modifier = Modifier
                                    .size(boxSize)
                                    .shadow(3.dp, RoundedCornerShape(cornerRadius), clip = false)
                                    .clip(RoundedCornerShape(cornerRadius))
                                    .background(goldBrush),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                // Rewards Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(rewardAlpha)
                ) {
                    GameCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Coins Earned Ticker
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "+$animatedCoins Coins",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFFC107)
                                    )
                                )
                            }

                            // Streak Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "$streak Level Streak",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                )
                            }

                            // Total Coins Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💰", fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Total Balance: %,d Coins".format(totalCoins),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = textSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                // Action Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(buttonAlpha)
                ) {
                    GameButton(
                        text = "▶  NEXT LEVEL",
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .bouncyClickable { onHome() }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏠  Back to Home",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
