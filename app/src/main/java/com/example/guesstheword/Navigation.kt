package com.example.guesstheword

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.guesstheword.ui.gameplay.GameplayScreen
import com.example.guesstheword.ui.home.HomeScreen
import com.example.guesstheword.ui.levelcomplete.LevelCompleteScreen
import com.example.guesstheword.ui.login.LoginScreen
import com.example.guesstheword.ui.onboarding.OnboardingScreen
import com.example.guesstheword.ui.profile.ProfileScreen
import com.example.guesstheword.ui.settings.SettingsScreen
import com.example.guesstheword.ui.splash.SplashScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Splash)

    NavDisplay(
        backStack = backStack,
        onBack = {
            val last = backStack.lastOrNull()
            if (last is LevelComplete) {
                backStack.removeLastOrNull() // pop LevelComplete
                backStack.removeLastOrNull() // pop Gameplay
            } else {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {

            entry<Splash> {
                SplashScreen(
                    onNavigateToOnboarding = {
                        backStack.removeLastOrNull()
                        backStack.add(Onboarding)
                    },
                    onNavigateToHome = {
                        backStack.removeLastOrNull()
                        backStack.add(Home)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Onboarding> {
                OnboardingScreen(
                    onFinish = {
                        backStack.removeLastOrNull()
                        backStack.add(Login)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Login> {
                LoginScreen(
                    onNavigateToHome = {
                        backStack.removeLastOrNull()
                        backStack.add(Home)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Home> {
                HomeScreen(
                    onNavigateToGameplay = { backStack.add(Gameplay) },
                    onNavigateToProfile  = { backStack.add(Profile)  },
                    onNavigateToSettings = { backStack.add(Settings) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Gameplay> {
                GameplayScreen(
                    onLevelComplete = { word, coinsEarned ->
                        backStack.add(LevelComplete(word, coinsEarned))
                    },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<LevelComplete> {
                LevelCompleteScreen(
                    word = it.word,
                    coinsEarned = it.coinsEarned,
                    onNextLevel = {
                        backStack.removeLastOrNull() // pop LevelComplete → [Home, Gameplay]
                        backStack.removeLastOrNull() // pop old Gameplay  → [Home]
                        backStack.add(Gameplay)      // fresh Gameplay with next level
                    },
                    onHome = {
                        backStack.removeLastOrNull() // pop LevelComplete → [Home, Gameplay]
                        backStack.removeLastOrNull() // pop Gameplay      → [Home]
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Profile> {
                ProfileScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onSignOut = {
                        while (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(Login)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            entry<Settings> {
                SettingsScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}
