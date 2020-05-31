package com.gitlab.drzepka.stwrbird.model

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import java.util.*

class Score() : Json.Serializable {

    var score = 0
    var playTime = 0
    var medal: Medal? = null
    var date = Date()

    constructor(score: Int, playTime: Int, medal: Medal?) : this() {
        this.score = score
        this.playTime = playTime
        this.medal = medal
    }

    override fun write(json: Json) {
        json.writeValue("score", score)
        json.writeValue("playTime", playTime)
        json.writeValue("medal", medal?.name)
        json.writeValue("date", date.time)
    }

    override fun read(json: Json, jsonData: JsonValue) {
        score = jsonData.getInt("score", score)
        playTime = jsonData.getInt("playTime", playTime)
        date = Date(jsonData.getLong("date", date.time))

        jsonData.getString("medal", null)?.apply {
            medal = Medal.valueOf(this)
        }
    }
}