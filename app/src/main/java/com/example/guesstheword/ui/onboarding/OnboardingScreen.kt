package com.example.guesstheword.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.safeDrawingPadding
import com.example.guesstheword.R
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.theme.GameButton
import com.example.guesstheword.theme.bouncyClickable

private data class OnboardingPage(
    val title: String,
    val description: String,
    val illustration: @Composable () -> Unit
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    var currentPageIndex by rememberSaveable { mutableIntStateOf(0) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    val pages = remember(primaryColor) {
        listOf(
            OnboardingPage(
                title = "Welcome to Guess The Word!",
                description = "Solve thousands of fun word puzzles and test your vocabulary with clear, beautiful clues.",
                illustration = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(110.dp)
                    )
                }
            ),
            OnboardingPage(
                title = "Tap Letters to Solve",
                description = "Tap letters in the bank to fill empty slots in order. Tap a placed letter to return it back to the bank.",
                illustration = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("C", "A", "T").forEach { char ->
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(char, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            ),
            OnboardingPage(
                title = "Use Hints and Shuffles",
                description = "Stuck on a word? Spend 20 coins to reveal a correct letter, or shuffle the remaining letter bank for free!",
                illustration = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DemoBadge("💡 Hint")
                        DemoBadge("🔀 Shuffle")
                        DemoBadge("⌫ Delete")
                    }
                }
            )
        )
    }

    var visible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(currentPageIndex) {
        visible = true
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        val screenHeight = maxHeight
        val isTablet = maxWidth > 600.dp
        val horizontalPadding = if (isTablet) 40.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Skip area
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPageIndex < pages.size - 1) {
                    Text(
                        text = "Skip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .bouncyClickable {
                                prefs.setPlayedBefore(true)
                                onFinish()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            // Main Pager content
            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(250)),
                    exit = fadeOut(tween(250)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val page = pages[currentPageIndex]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Illustration card
                        val cardBg = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                        }
                        Box(
                            modifier = Modifier
                                .size(if (screenHeight < 650.dp) 150.dp else 190.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(cardBg)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                                    RoundedCornerShape(32.dp)
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            page.illustration()
                        }

                        Spacer(Modifier.height(if (screenHeight < 650.dp) 20.dp else 32.dp))

                        // Title
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = if (screenHeight < 650.dp) 20.sp else 24.sp,
                                color = textPrimary
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Description
                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = if (screenHeight < 650.dp) 14.sp else 15.sp,
                                color = textSecondary
                            ),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(0.5f))

            // Bottom Navigation Indicators & Action Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPageIndex
                        val dotWidth by animateFloatAsState(
                            targetValue = if (isSelected) 24f else 8f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) primaryColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))
                        )
                    }
                }

                Spacer(Modifier.height(if (screenHeight < 650.dp) 20.dp else 32.dp))

                // Custom premium elevated button
                GameButton(
                    text = if (currentPageIndex == pages.size - 1) "Start Playing" else "Next",
                    onClick = {
                        if (currentPageIndex < pages.size - 1) {
                            // Fade out first, then advance page so fade-in plays correctly
                            scope.launch {
                                visible = false
                                delay(250L)
                                currentPageIndex++
                            }
                        } else {
                            prefs.setPlayedBefore(true)
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(if (isTablet) 0.6f else 1f)
                )
            }
        }
    }
}

@Composable
private fun DemoBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
