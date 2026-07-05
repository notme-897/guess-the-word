package com.example.guesstheword.data

import android.content.Context

class PreferencesManager(context: Context) {
  private val sharedPreferences = context.getSharedPreferences("guess_the_word_prefs", Context.MODE_PRIVATE)

  fun isPlayedBefore(): Boolean {
    return sharedPreferences.getBoolean("played_before", false)
  }

  fun setPlayedBefore(played: Boolean) {
    sharedPreferences.edit().putBoolean("played_before", played).apply()
  }

  fun getUsername(): String? {
    return sharedPreferences.getString("username", null)
  }

  fun setUsername(username: String?) {
    sharedPreferences.edit().putString("username", username).apply()
  }

  fun clear() {
    sharedPreferences.edit().clear().apply()
  }

  fun signOut() {
    sharedPreferences.edit()
      .remove("username")
      .remove("google_user")
      .remove("coins")
      .remove("current_level")
      .remove("streak")
      .remove("best_streak")
      .remove("words_solved")
      .remove("avatar")
      .apply()
  }

  fun getCurrentLevel(): Int {
    return sharedPreferences.getInt("current_level", 1)
  }

  fun setCurrentLevel(level: Int) {
    sharedPreferences.edit().putInt("current_level", level).apply()
  }

  fun getCoins(): Int {
    return sharedPreferences.getInt("coins", 1250)
  }

  fun setCoins(amount: Int) {
    sharedPreferences.edit().putInt("coins", amount).apply()
  }

  fun getStreak(): Int {
    return sharedPreferences.getInt("streak", 0)
  }

  fun setStreak(days: Int) {
    sharedPreferences.edit().putInt("streak", days).apply()
  }

  // ── Profile ────────────────────────────────────────────────────────────
  fun getAvatar(): String  = sharedPreferences.getString("avatar", "😊") ?: "😊"
  fun setAvatar(v: String) = sharedPreferences.edit().putString("avatar", v).apply()

  fun getWordsSolved(): Int  = sharedPreferences.getInt("words_solved", 0)
  fun setWordsSolved(v: Int) = sharedPreferences.edit().putInt("words_solved", v).apply()
  fun incrementWordsSolved() = setWordsSolved(getWordsSolved() + 1)

  fun getBestStreak(): Int  = sharedPreferences.getInt("best_streak", 0)
  fun setBestStreak(v: Int) = sharedPreferences.edit().putInt("best_streak", v).apply()

  fun isGoogleUser(): Boolean  = sharedPreferences.getBoolean("google_user", false)
  fun setGoogleUser(v: Boolean) = sharedPreferences.edit().putBoolean("google_user", v).apply()

  // ── Settings ───────────────────────────────────────────────────────────
  fun getSoundEnabled(): Boolean = sharedPreferences.getBoolean("settings_sound", true)
  fun setSoundEnabled(v: Boolean) = sharedPreferences.edit().putBoolean("settings_sound", v).apply()

  fun getMusicEnabled(): Boolean = sharedPreferences.getBoolean("settings_music", true)
  fun setMusicEnabled(v: Boolean) = sharedPreferences.edit().putBoolean("settings_music", v).apply()

  fun getVibrationEnabled(): Boolean = sharedPreferences.getBoolean("settings_vibration", true)
  fun setVibrationEnabled(v: Boolean) = sharedPreferences.edit().putBoolean("settings_vibration", v).apply()

  fun getLanguage(): String = sharedPreferences.getString("settings_language", "English") ?: "English"
  fun setLanguage(v: String) = sharedPreferences.edit().putString("settings_language", v).apply()
}
