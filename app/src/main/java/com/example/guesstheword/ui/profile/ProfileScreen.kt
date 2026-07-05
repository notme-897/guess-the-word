package com.example.guesstheword.ui.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.safeDrawingPadding
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.theme.GameCard
import com.example.guesstheword.theme.GameIconButton
import com.example.guesstheword.theme.bouncyClickable

private val avatarOptions = listOf(
    "😊", "🦊", "🤖", "🧩",
    "🐱", "🦁", "🐸", "🦄",
    "🐧", "🎭", "👾", "🧙"
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    var avatar by remember { mutableStateOf(prefs.getAvatar()) }
    var name by remember { mutableStateOf(prefs.getUsername() ?: "Player") }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showNameEditor by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    val isGoogle = remember { prefs.isGoogleUser() }

    val highestLevel = remember { prefs.getCurrentLevel() }
    val wordsSolved  = remember { prefs.getWordsSolved() }
    val coins        = remember { prefs.getCoins() }
    val bestStreak   = remember { prefs.getBestStreak() }

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val avatarScale by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "avatar_scale"
    )
    val nameAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(300, delayMillis = 150),
        label = "name_alpha"
    )
    val statsAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(300, delayMillis = 250),
        label = "stats_alpha"
    )
    val statsOffset by animateFloatAsState(
        targetValue = if (started) 0f else 30f,
        animationSpec = tween(350, delayMillis = 250),
        label = "stats_offset"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(300, delayMillis = 400),
        label = "buttons_alpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val cardBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

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
                // ── Top bar ───────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(contentWidthFraction),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameIconButton(
                        icon = "←",
                        onClick = onBack
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(46.dp)) // balance
                }

                Spacer(Modifier.height(32.dp))

                // ── Avatar ────────────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .scale(avatarScale)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.1f))
                        .border(3.dp, primaryColor, CircleShape)
                        .bouncyClickable { showAvatarPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(avatar, fontSize = 52.sp, textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(10.dp))

                // Tap to change label
                Text(
                    text = "Tap to change avatar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .alpha(avatarScale)
                        .bouncyClickable { showAvatarPicker = true }
                )

                Spacer(Modifier.height(16.dp))

                // ── Player name ───────────────────────────────────────────────────────
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = textPrimary,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.alpha(nameAlpha)
                )

                Spacer(Modifier.height(32.dp))

                // ── Stats grid cards ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(statsAlpha)
                        .graphicsLayer { translationY = statsOffset },
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(icon = "🏆", label = "Highest Level", value = highestLevel.toString(), modifier = Modifier.weight(1f))
                        StatCard(icon = "📝", label = "Words Solved", value = wordsSolved.toString(), modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(icon = "🪙", label = "Coins", value = "%,d".format(coins), modifier = Modifier.weight(1f))
                        StatCard(icon = "🔥", label = "Best Streak", value = "$bestStreak days", modifier = Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── Action rows card ──────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(buttonsAlpha)
                        .clip(RoundedCornerShape(24.dp))
                        .background(cardBackground)
                        .padding(horizontal = 20.dp)
                ) {
                    ActionRow(icon = "🖼️", label = "Change Avatar", tint = textPrimary) {
                        showAvatarPicker = true
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)
                    ActionRow(icon = "✏️", label = "Edit Name", tint = textPrimary) {
                        showNameEditor = true
                    }
                    if (isGoogle) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)
                        ActionRow(icon = "🚪", label = "Sign Out", tint = MaterialTheme.colorScheme.error) {
                            showSignOutDialog = true
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // ── Avatar Picker Dialog ──────────────────────────────────────────────────
    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Choose Avatar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    avatarOptions.chunked(4).forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { emoji ->
                                val isSelected = emoji == avatar
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(58.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else cardBackground)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) primaryColor else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            avatar = emoji
                                            prefs.setAvatar(emoji)
                                            showAvatarPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(color = textSecondary))
                }
            }
        )
    }

    // ── Name Editor Dialog ────────────────────────────────────────────────────
    if (showNameEditor) {
        var draftName by remember { mutableStateOf(name) }
        AlertDialog(
            onDismissRequest = { showNameEditor = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Edit Username",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { if (it.length <= 20) draftName = it },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (draftName.isNotBlank()) {
                            name = draftName.trim()
                            prefs.setUsername(name)
                        }
                        showNameEditor = false
                    }
                ) {
                    Text("Save", style = MaterialTheme.typography.labelLarge.copy(color = primaryColor, fontWeight = FontWeight.Bold))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameEditor = false }) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(color = textSecondary))
                }
            }
        )
    }

    // ── Sign Out Confirmation Dialog ──────────────────────────────────────────
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Sign Out",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                )
            },
            text = {
                Text(
                    "Are you sure you want to sign out? Your current stats will be cleared.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        prefs.signOut()
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sign Out", style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(color = textSecondary))
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    GameCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary
                )
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: String,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = tint
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            fontWeight = FontWeight.Light
        )
    }
}
