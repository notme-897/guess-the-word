package com.example.guesstheword

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Login : NavKey
@Serializable data object Home : NavKey
@Serializable data object Gameplay : NavKey
@Serializable data object Profile : NavKey
@Serializable data object Settings : NavKey
@Serializable data object Onboarding : NavKey
@Serializable data class LevelComplete(val word: String, val coinsEarned: Int) : NavKey
