package com.gitlab.drzepka.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.gitlab.drzepka.StwrBird

abstract class BaseScreen : Screen {

    lateinit var stwrBird: StwrBird

    var active = false
    var toBeClosed = false

    protected val width by lazy { Gdx.app.graphics.width }
    protected val height by lazy { Gdx.app.graphics.height }

    open fun create() = Unit
    override fun show() = Unit
    override fun render(delta: Float) = Unit
    override fun resize(width: Int, height: Int) = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit
    override fun dispose() = Unit

    /**
     * Zamyka ekran.
     */
    protected fun close() {
        // jeśli ekran jest nieaktywny, zostanie zamknięty w swoim czasie
        if (active)
            stwrBird.popScreen()
        else
            toBeClosed = true
    }
}