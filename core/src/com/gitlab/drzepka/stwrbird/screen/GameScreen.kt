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

    override fun create() {
        Gdx.input.inputProcessor = stage

        backgroundActor.setSize(1f, 1f)
        backgroundActor.prepare()
        stage.addActor(backgroundActor)

        tapToPlayOverlay.setSize(1f, 1f)
        stage.addActor(tapToPlayOverlay)

        gameOverActor.prepare()

        stage.addActor(birdActor)
        setMode(Mode.TAP_TO_PLAY)
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched() && mode == Mode.TAP_TO_PLAY)
            setMode(Mode.GAME)

        stage.act(delta)

        // SPRAWDZENIE KOLIZJI
        if (backgroundActor.checkForCollision(birdActor)) {
            // kolizja - koniec gry
            setMode(Mode.GAME_OVER)
        }

        stage.draw()
    }

    private fun setMode(mode: Mode) {
        when (mode) {
            GameScreen.Mode.FIRST -> TODO()
            GameScreen.Mode.TAP_TO_PLAY -> {

            }
            GameScreen.Mode.GAME -> {
                tapToPlayOverlay.isVisible = false
                backgroundActor.generatePipes = true
                birdActor.started = true
                gameOverActor.remove()
            }
            GameScreen.Mode.GAME_OVER -> {
                backgroundActor.started = false
                birdActor.started = false
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