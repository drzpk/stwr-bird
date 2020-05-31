package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gitlab.drzepka.stwrbird.StwrBird

abstract class BaseScreen : Screen {

    /** Czas przejścia między ekranami, w sekundach */
    private val FADE_DURATION = 0.15f

    var visible = false

    lateinit var stwrBird: StwrBird
    protected val stage = Stage(ScreenViewport())

    protected val width by lazy { Gdx.app.graphics.width }
    protected val height by lazy { Gdx.app.graphics.height }

    open fun create() = Unit
    override fun show() = Unit
    override fun render(delta: Float) = Unit
    override fun resize(width: Int, height: Int) = Unit
    override fun pause() = Unit

    override fun resume() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        val multiplexer = InputMultiplexer(KeyProcessor(), stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun hide() = Unit
    override fun dispose() = Unit

    fun fadeIn(onFinish: () -> Unit) {
        stage.root.color.a = 0f
        val sequence = SequenceAction()
        sequence.addAction(Actions.fadeIn(FADE_DURATION))
        sequence.addAction(Actions.run(onFinish))
        stage.root.addAction(sequence)
    }

    fun fadeOut(onFinish: () -> Unit) {
        val sequence = SequenceAction()
        sequence.addAction(Actions.fadeOut(FADE_DURATION))
        sequence.addAction(Actions.run(onFinish))
        stage.root.addAction(sequence)
    }

    /**
     * Zamyka ekran.
     */
    protected fun close() {
        stwrBird.popScreen()
    }

    private inner class KeyProcessor : InputAdapter() {
        override fun keyDown(keycode: Int): Boolean {
            return if (keycode == Input.Keys.BACK) {
                close()
                true
            } else {
                false
            }
        }
    }
}