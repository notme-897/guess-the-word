package com.example.guesstheword.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

private val languageOptions = listOf(
    "English", "Hindi", "Gujarati", "Spanish", "French"
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    var soundEnabled by remember { mutableStateOf(prefs.getSoundEnabled()) }
    var musicEnabled by remember { mutableStateOf(prefs.getMusicEnabled()) }
    var vibrationEnabled by remember { mutableStateOf(prefs.getVibrationEnabled()) }
    var language by remember { mutableStateOf(prefs.getLanguage()) }

    var showLanguageDialog by remember { mutableStateOf(false) }

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val listAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(400),
        label = "list_alpha"
    )
    val listOffset by animateFloatAsState(
        targetValue = if (started) 0f else 20f,
        animationSpec = tween(400),
        label = "list_offset"
    )

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSupportEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@guesstheword.com")
                putExtra(Intent.EXTRA_SUBJECT, "Guess The Word Support")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
        }
    }

    fun rateApp() {
        openUrl("market://details?id=${context.packageName}")
    }

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
                // ── Top Bar ───────────────────────────────────────────────────────────
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
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(46.dp)) // balance
                }

                Spacer(Modifier.height(32.dp))

                // ── Settings Container ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth(contentWidthFraction)
                        .alpha(listAlpha)
                        .graphicsLayer { translationY = listOffset }
                ) {
                    // Card 1: Audio / Vibration / Language
                    GameCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            ToggleRow(
                                icon = "🔊",
                                label = "Sound Effects",
                                checked = soundEnabled,
                                onCheckedChange = {
                                    soundEnabled = it
                                    prefs.setSoundEnabled(it)
                                }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            ToggleRow(
                                icon = "🎵",
                                label = "Music",
                                checked = musicEnabled,
                                onCheckedChange = {
                                    musicEnabled = it
                                    prefs.setMusicEnabled(it)
                                }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            ToggleRow(
                                icon = "📳",
                                label = "Haptic Vibration",
                                checked = vibrationEnabled,
                                onCheckedChange = {
                                    vibrationEnabled = it
                                    prefs.setVibrationEnabled(it)
                                }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            LanguageRow(
                                icon = "🌐",
                                label = "Language Selection",
                                value = language
                            ) {
                                showLanguageDialog = true
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Card 2: Legal and Support
                    GameCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            ActionItemRow(icon = "⭐", label = "Rate App") {
                                rateApp()
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            ActionItemRow(icon = "📧", label = "Contact Support") {
                                sendSupportEmail()
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            ActionItemRow(icon = "📄", label = "Privacy Policy") {
                                openUrl("https://www.example.com/privacy")
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), thickness = 1.dp)

                            ActionItemRow(icon = "📜", label = "Terms of Service") {
                                openUrl("https://www.example.com/terms")
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    // Version number
                    Text(
                        text = "Version 1.0.0 (Premium UX)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    // ── Language Dialog ───────────────────────────────────────────────────────
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Select Language",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    languageOptions.forEach { lang ->
                        val isSelected = lang == language
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .bouncyClickable {
                                    language = lang
                                    prefs.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) primaryColor else textPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Text("✓", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(color = textSecondary))
                }
            }
        )
    }
}

@Composable
private fun ToggleRow(
    icon: String,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
            )
        )
    }
}

@Composable
private fun LanguageRow(
    icon: String,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

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
                color = textPrimary
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$value ›",
            style = MaterialTheme.typography.titleMedium.copy(
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun ActionItemRow(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    val textPrimary = MaterialTheme.colorScheme.onBackground

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
                color = textPrimary
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
