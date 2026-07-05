package com.example.guesstheword.ui.home

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.theme.CoinBadge
import com.example.guesstheword.theme.GameButton
import com.example.guesstheword.theme.GameCard
import com.example.guesstheword.theme.GameIconButton
import com.example.guesstheword.theme.bouncyClickable

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToGameplay: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    val level = prefs.getCurrentLevel()
    val coins = prefs.getCoins()
    val streak = prefs.getStreak()
    
    val milestoneProgress = ((level - 1) % 10) / 10f
    val levelProgress = if (milestoneProgress <= 0f) 0.05f else milestoneProgress

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val topAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "top_alpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logo_scale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = 200),
        label = "card_alpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (started) 0f else 40f,
        animationSpec = tween(durationMillis = 350, delayMillis = 200),
        label = "card_offset_y"
    )
    val playScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "play_scale"
    )
    val playAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 200, delayMillis = 350),
        label = "play_alpha"
    )
    val bottomAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 200, delayMillis = 500),
        label = "bottom_alpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                modifier = Modifier
                    .fillMaxWidth(contentWidthFraction)
                    .alpha(topAlpha)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile button
                GameIconButton(
                    icon = "👤",
                    onClick = onNavigateToProfile
                )

                // Coin balance in the centre of the top bar
                CoinBadge(coins = coins)

                // Settings button
                GameIconButton(
                    icon = "⚙️",
                    onClick = onNavigateToSettings
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Game Logo ─────────────────────────────────────────────────────
            Text(
                text = "GUESS THE WORD",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = topAlpha
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Continue Card ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(contentWidthFraction)
                    .alpha(cardAlpha)
                    .graphicsLayer { translationY = cardOffsetY }
            ) {
                GameCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Milestone Progress",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = textSecondary,
                            letterSpacing = 0.3.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Level $level",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = textPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(levelProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(milestoneProgress * 100).toInt()}% to next Bonus",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── PLAY Button ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(contentWidthFraction)
                    .alpha(playAlpha)
                    .graphicsLayer {
                        scaleX = playScale
                        scaleY = playScale
                    }
            ) {
                GameButton(
                    text = "▶  PLAY",
                    onClick = onNavigateToGameplay,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Coins + Streak Cards ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth(contentWidthFraction)
                    .alpha(bottomAlpha),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Coins Item Card
                GameCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🪙", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "%,d".format(coins),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Text(
                            text = "Coins",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textSecondary
                            )
                        )
                    }
                }

                // Streak Item Card
                GameCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔥", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$streak Days",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Text(
                            text = "Active Streak",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
