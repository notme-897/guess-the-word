package com.example.guesstheword.ui.login

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.safeDrawingPadding
import com.example.guesstheword.R
import com.example.guesstheword.data.PreferencesManager
import com.example.guesstheword.theme.GameButton
import com.example.guesstheword.theme.bouncyClickable

@Composable
fun LoginScreen(
  onNavigateToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val prefsManager = remember { PreferencesManager(context) }
  
  var startAnimations by rememberSaveable { mutableStateOf(false) }
  
  val headerAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 500),
    label = "header_alpha"
  )
  val headerTranslationY by animateFloatAsState(
    targetValue = if (startAnimations) 0f else 30f,
    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
    label = "header_translation"
  )
  
  val googleBtnAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 500, delayMillis = 150),
    label = "google_alpha"
  )
  val googleBtnTranslationY by animateFloatAsState(
    targetValue = if (startAnimations) 0f else 30f,
    animationSpec = tween(durationMillis = 500, delayMillis = 150, easing = FastOutSlowInEasing),
    label = "google_translation"
  )

  val guestBtnAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 500, delayMillis = 300),
    label = "guest_alpha"
  )
  val guestBtnTranslationY by animateFloatAsState(
    targetValue = if (startAnimations) 0f else 30f,
    animationSpec = tween(durationMillis = 500, delayMillis = 300, easing = FastOutSlowInEasing),
    label = "guest_translation"
  )

  val footerAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 500, delayMillis = 450),
    label = "footer_alpha"
  )

  LaunchedEffect(Unit) {
    startAnimations = true
  }

  val primaryColor = MaterialTheme.colorScheme.primary
  val textPrimary = MaterialTheme.colorScheme.onBackground
  val textSecondary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
  val outlineColor = MaterialTheme.colorScheme.outline

  BoxWithConstraints(
    modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .safeDrawingPadding()
  ) {
    val screenHeight = maxHeight
    val isTablet = maxWidth > 600.dp
    val buttonWidthFraction = if (isTablet) 0.6f else 1f
    val verticalPadding = if (screenHeight < 650.dp) 16.dp else 32.dp

    Column(
      modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = verticalPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      // TOP BRANDING SECTION
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(headerAlpha)
            .graphicsLayer { translationY = headerTranslationY }
      ) {
        Image(
          painter = painterResource(id = R.drawable.logo),
          contentDescription = "Mini Logo",
          modifier = Modifier.size(if (screenHeight < 650.dp) 44.dp else 56.dp),
          contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
          text = "GUESS THE WORD",
          style = MaterialTheme.typography.labelLarge.copy(
              color = primaryColor,
              letterSpacing = 1.sp
          )
        )
        
        Spacer(modifier = Modifier.height(if (screenHeight < 650.dp) 16.dp else 24.dp))
        
        Text(
          text = "Welcome Back!",
          style = MaterialTheme.typography.displayMedium.copy(
              color = textPrimary,
              fontWeight = FontWeight.ExtraBold
          )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
          text = "Solve thousands of fun puzzles.",
          style = MaterialTheme.typography.bodyLarge.copy(
              color = textSecondary
          ),
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(if (screenHeight < 650.dp) 32.dp else 48.dp))

      // BUTTONS CONTAINER
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Google Button
        Row(
          modifier = Modifier
              .fillMaxWidth(buttonWidthFraction)
              .height(56.dp)
              .alpha(googleBtnAlpha)
              .graphicsLayer { translationY = googleBtnTranslationY }
              .shadow(2.dp, RoundedCornerShape(20.dp), clip = false)
              .clip(RoundedCornerShape(20.dp))
              .border(1.dp, outlineColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
              .background(MaterialTheme.colorScheme.surface)
              .bouncyClickable {
                  Toast
                      .makeText(context, "Signing in with Google...", Toast.LENGTH_SHORT)
                      .show()
                  prefsManager.setPlayedBefore(true)
                  prefsManager.setGoogleUser(true)
                  prefsManager.setUsername("Google Player")
                  onNavigateToHome()
              }
              .padding(horizontal = 24.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          GoogleIcon()
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
          )
        }

        // Guest Button
        Box(
          modifier = Modifier
              .fillMaxWidth(buttonWidthFraction)
              .alpha(guestBtnAlpha)
              .graphicsLayer { translationY = guestBtnTranslationY }
        ) {
          GameButton(
            text = "Continue as Guest",
            onClick = {
              prefsManager.setPlayedBefore(true)
              prefsManager.setGoogleUser(false)
              prefsManager.setUsername("Guest Explorer")
              onNavigateToHome()
            },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(48.dp))

      // FOOTER SECTION
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(footerAlpha).padding(top = 24.dp)
      ) {
        Text(
          text = "Progress sync is available\nwhen you sign in with Google.",
          style = MaterialTheme.typography.bodyMedium.copy(
              color = textSecondary
          ),
          textAlign = TextAlign.Center,
          lineHeight = 16.sp
        )
        
        Spacer(modifier = Modifier.height(18.dp))
        
        Text(
          text = "Privacy Policy",
          style = MaterialTheme.typography.titleMedium.copy(
              color = primaryColor,
              fontWeight = FontWeight.Bold,
              textDecoration = TextDecoration.Underline
          ),
          modifier = Modifier.clickable {
            Toast.makeText(context, "Opening Privacy Policy...", Toast.LENGTH_SHORT).show()
          }
        )
      }
    }
  }
}

@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(18.dp)) {
    val sizePx = size.minDimension
    val strokeWidth = sizePx * 0.15f
    
    // Draw 4 segments of the Google ring
    drawArc(
      color = Color(0xFFEA4335), // Red
      startAngle = 180f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth)
    )
    drawArc(
      color = Color(0xFFFBBC05), // Yellow
      startAngle = 90f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth)
    )
    drawArc(
      color = Color(0xFF34A853), // Green
      startAngle = 0f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth)
    )
    drawArc(
      color = Color(0xFF4285F4), // Blue
      startAngle = 270f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth)
    )
  }
}
