package com.gitlab.drzepka.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gitlab.drzepka.components.BackgroundActor

class GameScreen : BaseScreen() {

    private val stage = Stage(ScreenViewport())

    override fun create() {
        val bgActor = BackgroundActor()
        bgActor.setSize(1f, 1f)
        bgActor.prepare()
        stage.addActor(bgActor)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }
}