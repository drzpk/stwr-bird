package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gitlab.drzepka.stwrbird.components.BackgroundActor
import com.gitlab.drzepka.stwrbird.components.BirdActor
import com.gitlab.drzepka.stwrbird.components.PlayGameOverlay

class GameScreen : BaseScreen() {

    private val stage = Stage(ScreenViewport())
    private var mode = Mode.PLAY_GAME_OVERLAY

    private val backgroundActor = BackgroundActor()
    private val playGameOverlay = PlayGameOverlay()
    private val birdActor = BirdActor()

    override fun create() {
        Gdx.input.inputProcessor = stage

        backgroundActor.setSize(1f, 1f)
        backgroundActor.prepare()
        stage.addActor(backgroundActor)

        playGameOverlay.setSize(1f, 1f)
        stage.addActor(playGameOverlay)

        stage.addActor(birdActor)
    }

    override fun render(delta: Float) {
        stage.act(delta)

        // SPRAWDZENIE KOLIZJI
        if (backgroundActor.checkForCollision(birdActor)) {
            // kolizja - koniec gry
            birdActor.stop()
        }

        stage.draw()
    }


    private fun setMode(mode: Mode) {

    }


    private enum class Mode {
        MAIN_OVERLAY,
        PLAY_GAME_OVERLAY,
        GAME,
        GAME_OVER_OVERLAY
    }
}