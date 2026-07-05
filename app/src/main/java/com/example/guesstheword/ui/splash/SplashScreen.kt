package com.example.guesstheword.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guesstheword.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import com.example.guesstheword.data.PreferencesManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onNavigateToOnboarding: () -> Unit,
  onNavigateToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val prefsManager = remember { PreferencesManager(context) }
  
  // Animation States
  var startAnimations by remember { mutableStateOf(false) }
  var isExiting by remember { mutableStateOf(false) }
  
  // Entrance Animations
  val logoScale by animateFloatAsState(
    targetValue = if (isExiting) 1.15f else if (startAnimations) 1f else 0.7f,
    animationSpec = tween(
      durationMillis = 800,
      easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    ),
    label = "logo_scale"
  )
  val logoAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 800),
    label = "logo_alpha"
  )
  
  val textAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 1000, delayMillis = 400),
    label = "text_alpha"
  )
  val textTranslationY by animateFloatAsState(
    targetValue = if (startAnimations) 0f else 40f,
    animationSpec = tween(durationMillis = 1000, delayMillis = 400, easing = FastOutSlowInEasing),
    label = "text_translation"
  )
  
  val loaderAlpha by animateFloatAsState(
    targetValue = if (startAnimations) 1f else 0f,
    animationSpec = tween(durationMillis = 800, delayMillis = 800),
    label = "loader_alpha"
  )

  LaunchedEffect(Unit) {
    startAnimations = true
    
    // Simulate Loading workflow:
    // At 2.0 seconds: Scale the logo slightly to prepare for exit transition (lasts 0.5s)
    delay(2000)
    isExiting = true
    
    // At 2.5 seconds: Check played before flag and navigate
    delay(500)
    val hasPlayedBefore = prefsManager.isPlayedBefore()
    if (hasPlayedBefore) {
      onNavigateToHome()
    } else {
      onNavigateToOnboarding()
    }
  }

  val isDark = isSystemInDarkTheme()
  val bgGradient = if (isDark) {
    Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
  } else {
    Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), Color(0xFFEEF2FF)))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(bgGradient),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      // Dynamic top spacing to Logo
      Spacer(modifier = Modifier.weight(1f))
      
      // Logo
      Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Guess The Word Logo",
        modifier = Modifier
          .size(120.dp)
          .graphicsLayer {
            scaleX = logoScale
            scaleY = logoScale
            alpha = logoAlpha
          }
      )
      
      // 32dp spacing from Logo to Game Name
      Spacer(modifier = Modifier.height(32.dp))
      
      // Game Name & Tagline Group
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .alpha(textAlpha)
          .graphicsLayer {
            translationY = textTranslationY
          }
      ) {
        Text(
          text = "GUESS THE WORD",
          fontSize = 32.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.SansSerif,
          color = MaterialTheme.colorScheme.onBackground,
          letterSpacing = 0.5.sp
        )
        
        // 12dp spacing from Title to Tagline
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
          text = "Guess • Learn • Win",
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.SansSerif,
          color = MaterialTheme.colorScheme.primary
        )
      }
      
      // Dynamic spacing from Tagline to Loading Animation
      Spacer(modifier = Modifier.weight(1f))
      
      // Loading Bouncing Dots
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(loaderAlpha)
      ) {
        BouncingDots()
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "LOADING...",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.SansSerif,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
          letterSpacing = 0.5.sp
        )
      }
      
      Spacer(modifier = Modifier.weight(1f))
      
      // Version text at bottom
      Text(
        text = "Version 1.0.0",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 16.dp)
      )
    }
  }
}

@Composable
fun BouncingDots(modifier: Modifier = Modifier) {
  val dots = listOf(
    remember { Animatable(0f) },
    remember { Animatable(0f) },
    remember { Animatable(0f) }
  )

  dots.forEachIndexed { index, animatable ->
    LaunchedEffect(animatable) {
      delay(index * 150L)
      animatable.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(450, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    }
  }

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val dotColor = MaterialTheme.colorScheme.primary
    dots.forEach { animatable ->
      // Interpolate scale and opacity based on animation value
      val scale = 0.8f + (1.3f - 0.8f) * animatable.value
      val alpha = 0.2f + (1f - 0.2f) * animatable.value
      Box(
        modifier = Modifier
          .size(10.dp)
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
          }
          .background(color = dotColor, shape = CircleShape)
      )
    }
  }
}
