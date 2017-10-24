package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.gitlab.drzepka.stwrbird.screen.BaseScreen
import com.gitlab.drzepka.stwrbird.screen.GameScreen
import java.util.*

class StwrBird private constructor() : ApplicationAdapter() {
    //private val FPS = 60f
    //private val frameTime = (1000f / FPS).toLong()

    private val screenStack = Stack<BaseScreen>()
    private var lastFrame = 0L


    constructor(androidIface: AndroidInterface) : this() {
        androidInterface = androidIface
    }

    override fun create() {

        // dodanie ekranu głównego
        pushScreen(GameScreen())

        // ustawienie czasu klatki
        lastFrame = System.currentTimeMillis()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        /*val delta = System.currentTimeMillis() - lastFrame
        if (delta < frameTime)
            Thread.sleep(frameTime - delta)

        screenStack.peek().render((System.currentTimeMillis() - lastFrame) / 1000f)
        lastFrame = System.currentTimeMillis()*/
        screenStack.peek().render(Gdx.graphics.rawDeltaTime)
    }

    /**
     * Dodaje nowy ekran do stosu. Dodany ekran staje się aktywny, podczas gdy poprzednio aktywny ekran jest usypiany.
     */
    fun pushScreen(screen: BaseScreen) {
        // ustawienie pól ekranu
        screen.stwrBird = this

        // wyłączenie obecnego ekranu
        if (screenStack.isNotEmpty()) {
            screenStack.peek()?.active = false
            screenStack.peek()?.pause()
            screenStack.peek()?.hide()
        }

        // wrzucenie ekranu na stos
        screenStack.push(screen)
        screen.create()
        screen.show()
        screen.resume()
        screen.active = true
    }

    /**
     * Zamyka aktywny ekran. Jeżeli zostanie zamknięty ostatni ekran, aplikacja zakończy działanie.
     */
    fun popScreen() {
        fun handleScreen(screen: BaseScreen) {
            screen.active = false
            screen.pause()
            screen.hide()
            screen.dispose()
        }

        val screen = screenStack.pop()
        handleScreen(screen)

        if (screenStack.isNotEmpty()) {
            // zamknięcie wszystkich oczekujących ekranów
            while (screenStack.isNotEmpty() && screenStack.peek()?.toBeClosed == true) {
                handleScreen(screenStack.pop())
            }
        }

        if (screenStack.isNotEmpty()) {
            screenStack.peek()?.show()
            screenStack.peek()?.resume()
            screenStack.peek()?.active = true
        }
        else {
            // brak ekranów, zakończ aplikację
            dispose()
            System.exit(0)
        }
    }

    override fun dispose() {
        // zniszczenie wszystkich aktywnych ekranów
        while (screenStack.isNotEmpty()) {
            val screen = screenStack.pop()
            screen.pause()
            screen.dispose()
        }

        Commons.atlas.dispose()
    }

    companion object {
        var androidInterface: AndroidInterface? = null
            private set
    }
}