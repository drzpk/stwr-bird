package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.floor
import com.gitlab.drzepka.stwrbird.Audio
import com.gitlab.drzepka.stwrbird.components.game.BackgroundActor
import com.gitlab.drzepka.stwrbird.components.game.GameOverActor
import com.gitlab.drzepka.stwrbird.components.game.PlayGameOverlay
import com.gitlab.drzepka.stwrbird.components.game.PlayerActor
import com.gitlab.drzepka.stwrbird.data.ScoreData
import com.gitlab.drzepka.stwrbird.model.Medal
import com.gitlab.drzepka.stwrbird.model.Score
import java.util.*
import kotlin.math.abs

class GameScreen : BaseScreen() {

    /** Czas trwania błysku po przegranej grze w sekundach */
    private val FLASH_DURATION = 0.21f

    private var mode = Mode.TAP_TO_PLAY

    private val tapToPlayOverlay = PlayGameOverlay(this)
    private val gameOverActor = GameOverActor(this)
    private val backgroundActor = BackgroundActor()
    private val playerActor = PlayerActor()
    private val shapeRenderer = ShapeRenderer()

    private var bestScore = 0
    private var flashAnimation = false
    private var flashStatus = 0f
    private var gameStartTime: Date? = null

    override fun create() {
        super.create()

        // załadowanie najlepszego wyniku
        bestScore = ScoreData.getHighestScores().firstOrNull()?.score ?: 0

        backgroundActor.setSize(1f, 1f)
        backgroundActor.prepare()
        stage.addActor(backgroundActor)

        tapToPlayOverlay.setSize(1f, 1f)
        stage.addActor(tapToPlayOverlay)
        tapToPlayOverlay.onPlayerTypeSelected = {
            playerActor.playerType = it
        }

        gameOverActor.prepare()
        playerActor.prepare()

        stage.addActor(playerActor)
        setMode(Mode.TAP_TO_PLAY)
    }

    override fun render(delta: Float) {
        stage.act(delta)

        // SPRAWDZENIE KOLIZJI
        if (mode == Mode.GAME && backgroundActor.checkForCollision(playerActor)) {
            // kolizja - koniec gry
            setMode(Mode.GAME_OVER)
        }

        stage.draw()

        // RYSOWANIE BŁYSKU
        if (flashAnimation) {
            val alpha = ((FLASH_DURATION / 2) - abs(flashStatus)) / (FLASH_DURATION / 2)
            Gdx.gl.glEnable(GL20.GL_BLEND)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(1f, 1f, 1f, alpha)
            shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            shapeRenderer.end()
            Gdx.gl.glDisable(GL20.GL_BLEND)

            flashStatus += delta
            if (flashStatus > FLASH_DURATION / 2)
                flashAnimation = false
        }
    }

    fun setMode(mode: Mode) {
        when (mode) {
            Mode.FIRST -> TODO()
            Mode.TAP_TO_PLAY -> {
                playerActor.reset()
                backgroundActor.reset()
                gameOverActor.remove()
                tapToPlayOverlay.isVisible = true

                // dźwięk machnięcia
                Audio.swoosh.play()
            }
            Mode.GAME -> {
                tapToPlayOverlay.isVisible = false
                backgroundActor.generatePipes = true
                playerActor.started = true
                playerActor.gameOver = false
                playerActor.fly()
                gameStartTime = Date()
                gameOverActor.remove()
            }
            Mode.GAME_OVER -> {
                val gameEndTime = Date()

                backgroundActor.started = false
                backgroundActor.generatePipes = false
                playerActor.started = false
                playerActor.gameOver = true

                // włączenie błysku
                flashAnimation = true
                flashStatus = -(FLASH_DURATION / 2)

                // obliczenie wyniku i przyznanie medalu
                val score = backgroundActor.score
                val medal = when {
                    score > 100 -> Medal.PLATINIUM
                    score > 70 -> Medal.GOLD
                    score > 30 -> Medal.SILVER
                    score > 15 -> Medal.BRONZE
                    else -> Medal.NONE
                }

                if (score > 0) {
                    val playTime = floor((gameEndTime.time - gameStartTime!!.time) / 1000f + 0.5f)
                    ScoreData.addScore(Score(score, playTime, medal))
                }

                gameOverActor.newBest = if (score > bestScore) {
                    bestScore = score
                    true
                } else false

                gameOverActor.score = score
                gameOverActor.bestScore = bestScore
                gameOverActor.medal = medal
                stage.addActor(gameOverActor)
            }
        }

        this.mode = mode
    }

    fun showScoreboard() {
        stwrBird.pushScreen(ScoreboardScreen())
    }

    enum class Mode {
        FIRST,
        TAP_TO_PLAY,
        GAME,
        GAME_OVER
    }
}