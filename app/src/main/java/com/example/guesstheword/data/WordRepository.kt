package com.example.guesstheword.data

import android.content.Context
import org.json.JSONArray
import java.io.InputStream

data class WordPuzzle(
    val level: Int,
    val word: String,
    val letterBank: List<Char>,
    val category: String,
    val categoryLabel: String,
    val emoji: String,
    val hint: String,
    val reward: Int
)

object WordRepository {

    private var cachedPuzzles: List<WordPuzzle>? = null

    // Fallback puzzles if JSON load fails
    private val fallbackPuzzles = listOf(
        WordPuzzle(1, "CAT", listOf('C','A','T','M','R','P'), "Animals", "Guess the Animal", "🐱", "A furry pet that purrs", 20),
        WordPuzzle(2, "DOG", listOf('D','O','G','L','T','C'), "Animals", "Guess the Animal", "🐶", "Man's best friend", 20),
        WordPuzzle(3, "SUN", listOf('S','U','N','K','B','T'), "Nature", "Guess the Nature", "☀️", "It rises every morning", 20),
        WordPuzzle(4, "MAP", listOf('M','A','P','T','R','C'), "Objects", "Guess the Object", "🗺️", "Used to find directions", 20),
        WordPuzzle(5, "CUP", listOf('C','U','P','B','L','T'), "Objects", "Guess the Object", "☕", "You drink from it", 20),
    )

    private fun loadPuzzles(context: Context): List<WordPuzzle> {
        cachedPuzzles?.let { return it }

        return try {
            val inputStream: InputStream = context.assets.open("levels.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<WordPuzzle>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val answer = obj.getString("answer")
                val category = obj.getString("category")
                val emoji = obj.getString("clue") // maps from 'clue' to 'emoji'
                val hint = obj.getString("hint")
                val categoryLabel = obj.getString("categoryLabel")
                val reward = obj.optInt("reward", 20)

                val bankArray = obj.getJSONArray("letterBank")
                val letterBank = mutableListOf<Char>()
                for (j in 0 until bankArray.length()) {
                    val charStr = bankArray.getString(j)
                    if (charStr.isNotEmpty()) {
                        letterBank.add(charStr[0])
                    }
                }

                list.add(
                    WordPuzzle(
                        level = id,
                        word = answer,
                        letterBank = letterBank,
                        category = category,
                        categoryLabel = categoryLabel,
                        emoji = emoji,
                        hint = hint,
                        reward = reward
                    )
                )
            }
            cachedPuzzles = list
            list
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackPuzzles
        }
    }

    fun getPuzzleForLevel(context: Context, level: Int): WordPuzzle {
        val puzzles = loadPuzzles(context)
        if (puzzles.isEmpty()) {
            return fallbackPuzzles[0]
        }
        val safeLevel = maxOf(1, level)
        val index = (safeLevel - 1) % puzzles.size
        // Return a copy with shuffled letter bank so it's fresh for the player
        val p = puzzles[index]
        return p.copy(letterBank = p.letterBank.shuffled())
    }

    fun getPuzzlesCount(context: Context): Int {
        return loadPuzzles(context).size
    }
}
