package com.gitlab.drzepka.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gitlab.drzepka.components.BackgroundActor
import com.gitlab.drzepka.components.PlayGameOverlay

class GameScreen : BaseScreen() {

    private val stage = Stage(ScreenViewport())
    private var mode = Mode.PLAY_GAME_OVERLAY

    override fun create() {
        val bgActor = BackgroundActor()
        bgActor.setSize(1f, 1f)
        bgActor.prepare()
        stage.addActor(bgActor)

        val playGameOverlay = PlayGameOverlay()
        playGameOverlay.setSize(1f, 1f)
        stage.addActor(playGameOverlay)
    }

    override fun render(delta: Float) {
        stage.act(delta)
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