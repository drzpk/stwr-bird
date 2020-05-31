package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.gitlab.drzepka.stwrbird.Audio
import com.gitlab.drzepka.stwrbird.components.game.BackgroundActor
import com.gitlab.drzepka.stwrbird.components.game.BirdActor
import com.gitlab.drzepka.stwrbird.components.game.GameOverActor
import com.gitlab.drzepka.stwrbird.components.game.PlayGameOverlay
import com.gitlab.drzepka.stwrbird.model.Medal
import kotlin.math.abs

class GameScreen : BaseScreen() {

    /** Czas trwania błysku po przegranej grze w sekundach */
    private val FLASH_DURATION = 0.21f

    private var mode = Mode.TAP_TO_PLAY

    private val tapToPlayOverlay = PlayGameOverlay()
    private val gameOverActor = GameOverActor(this)
    private val backgroundActor = BackgroundActor()
    private val birdActor = BirdActor()
    private val shapeRenderer = ShapeRenderer()

    private var bestScore = 0
    private var flashAnimation = false
    private var flashStatus = 0f

    override fun create() {
        super.create()

        // załadowanie najlepszego wyniku
        bestScore = Gdx.app.getPreferences("stwr-bird").getInteger("best_score", 0)

        backgroundActor.setSize(1f, 1f)
        backgroundActor.prepare()
        stage.addActor(backgroundActor)

        tapToPlayOverlay.setSize(1f, 1f)
        stage.addActor(tapToPlayOverlay)

        gameOverActor.prepare()
        birdActor.prepare()

        stage.addActor(birdActor)
        setMode(Mode.TAP_TO_PLAY)
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (mode) {
                Mode.TAP_TO_PLAY -> setMode(Mode.GAME)
            }
        }

        stage.act(delta)

        // SPRAWDZENIE KOLIZJI
        if (mode == Mode.GAME && backgroundActor.checkForCollision(birdActor)) {
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
                birdActor.reset()
                backgroundActor.reset()
                gameOverActor.remove()
                tapToPlayOverlay.isVisible = true

                // dźwięk machnięcia
                Audio.swoosh.play()
            }
            Mode.GAME -> {
                tapToPlayOverlay.isVisible = false
                backgroundActor.generatePipes = true
                birdActor.started = true
                gameOverActor.remove()
            }
            Mode.GAME_OVER -> {
                backgroundActor.started = false
                backgroundActor.generatePipes = false
                birdActor.started = false

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
                gameOverActor.newBest = if (score > bestScore) {
                    val preferences = Gdx.app.getPreferences("stwr-bird")
                    preferences.putInteger("best_score", score)
                    preferences.flush()
                    bestScore = score
                    true
                }
                else
                    false
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