package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gitlab.drzepka.stwrbird.components.BackgroundActor
import com.gitlab.drzepka.stwrbird.components.BirdActor
import com.gitlab.drzepka.stwrbird.components.GameOverActor
import com.gitlab.drzepka.stwrbird.components.PlayGameOverlay

class GameScreen : BaseScreen() {

    private val stage = Stage(ScreenViewport())
    private var mode = Mode.TAP_TO_PLAY

    private val tapToPlayOverlay = PlayGameOverlay()
    private val gameOverActor = GameOverActor()
    private val backgroundActor = BackgroundActor()
    private val birdActor = BirdActor()

    private var bestScore = 0

    override fun create() {
        Gdx.input.inputProcessor = stage

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
        if (Gdx.input.justTouched() && mode == Mode.TAP_TO_PLAY)
            setMode(Mode.GAME)

        if (Gdx.input.justTouched()) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (mode) {
                Mode.TAP_TO_PLAY -> setMode(Mode.GAME)
                Mode.GAME_OVER -> setMode(Mode.TAP_TO_PLAY)
            }
        }

        stage.act(delta)

        // SPRAWDZENIE KOLIZJI
        if (mode == Mode.GAME && backgroundActor.checkForCollision(birdActor)) {
            // kolizja - koniec gry
            setMode(Mode.GAME_OVER)
        }

        stage.draw()
    }

    private fun setMode(mode: Mode) {
        when (mode) {
            GameScreen.Mode.FIRST -> TODO()
            GameScreen.Mode.TAP_TO_PLAY -> {
                birdActor.reset()
                backgroundActor.reset()
                gameOverActor.remove()
                tapToPlayOverlay.isVisible = true
            }
            GameScreen.Mode.GAME -> {
                tapToPlayOverlay.isVisible = false
                backgroundActor.generatePipes = true
                birdActor.started = true
                gameOverActor.remove()
            }
            GameScreen.Mode.GAME_OVER -> {
                backgroundActor.started = false
                backgroundActor.generatePipes = false
                birdActor.started = false

                // obliczenie wyniku i przyznanie medalu
                val score = backgroundActor.score
                val medal = when {
                    score > 100 -> GameOverActor.Medal.PLATINIUM
                    score > 70 -> GameOverActor.Medal.GOLD
                    score > 30 -> GameOverActor.Medal.SILVER
                    score > 15 -> GameOverActor.Medal.BRONZE
                    else -> GameOverActor.Medal.NONE
                }
                gameOverActor.newBest = if (score > bestScore) {
                    val preferences = Gdx.app.getPreferences("stwr-bird")
                    preferences.putInteger("best_score", score)
                    preferences.flush()
                    bestScore = score
                    true
                } else
                    false
                gameOverActor.score = score
                gameOverActor.bestScore = bestScore
                gameOverActor.medal = medal
                stage.addActor(gameOverActor)
            }
        }

        this.mode = mode
    }

    private enum class Mode {
        FIRST,
        TAP_TO_PLAY,
        GAME,
        GAME_OVER
    }
}