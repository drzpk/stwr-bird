package com.gitlab.drzepka.stwrbird.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.gitlab.drzepka.stwrbird.model.Score
import java.io.StringWriter

object ScoreData {

    /**
     * Maksymalna ilość przechowywanych najwyższych wyników.
     */
    private const val MAX_SCORE_ITEMS = 10
    private const val PREFERENCES_NAME = "highest_scores"
    private const val SCORE_PREFERENCE_NAME = "data"

    private var highestScores: MutableList<Score>? = null

    fun addScore(new: Score) {
        val scores = getHighestScores()

        var newPos = -1
        for (i in scores.indices) {
            if (scores[i].score < new.score) {
                newPos = i
                break
            }
        }

        val isFull = scores.size == MAX_SCORE_ITEMS
        if (newPos == -1 && isFull) return

        when {
            isFull -> scores[newPos] = new
            newPos > -1 && newPos < scores.size -> scores.add(newPos, new)
            else -> scores.add(new)
        }


        val prefs = Gdx.app.getPreferences(PREFERENCES_NAME)
        prefs.putString(SCORE_PREFERENCE_NAME, serializeScores(scores))
        prefs.flush()
    }

    fun getHighestScores(): MutableList<Score> {
        if (highestScores != null) return highestScores!!

        val raw = Gdx.app.getPreferences(PREFERENCES_NAME).getString(SCORE_PREFERENCE_NAME)
        highestScores = if (raw != null)
            deserializeScores(raw)
        else
            ArrayList()

        return highestScores!!
    }

    private fun serializeScores(scores: List<Score>): String {
        val json = Json(JsonWriter.OutputType.json)
        val writer = StringWriter()
        json.setWriter(writer)

        json.writeValue(scores)

        return writer.toString()

    }

    @Suppress("UNCHECKED_CAST")
    private fun deserializeScores(data: String): MutableList<Score> {
        if (data.isBlank()) return ArrayList()

        val json = Json()

        return json.fromJson(List::class.java, data) as MutableList<Score>
    }
}