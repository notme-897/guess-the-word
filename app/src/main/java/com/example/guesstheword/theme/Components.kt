package com.example.guesstheword.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bouncy_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.85f else 1f,
        label = "bouncy_alpha"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }.clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .scale(scale)
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Text(text = icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
    containerColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(
            elevation = 0.dp,
            shape = RoundedCornerShape(24.dp),
            clip = false
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(borderWidth, borderColor),
        content = content
    )
}

@Composable
fun GameIconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 20.sp, color = contentColor)
    }
}

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🪙", fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = "%,d".format(coins),
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔥", fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$streak Days",
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
