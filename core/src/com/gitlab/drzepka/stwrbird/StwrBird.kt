package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.gitlab.drzepka.stwrbird.screen.BaseScreen
import com.gitlab.drzepka.stwrbird.screen.GameScreen
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class StwrBird private constructor() : ApplicationAdapter() {

    private val screenStack = Stack<BaseScreen>()

    // Flaga określająca, czy stos okien jest w tej chwili modyfikowany. Zapobiega kolejnym
    // przełączeniom okien, podczas gdy animacja poprzedniego przełączenia jest jeszcze odtwarzana
    private val isStackBeingModified = AtomicBoolean(false)


    constructor(androidIface: Android) : this() {
        android = androidIface
    }

    override fun create() {
        // dodanie ekranu głównego
        pushScreen(GameScreen())
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 01f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Jednocześnie może być aktywny więcej niż jeden ekran jednocześnie (np. podczas animacji przejść)
        screenStack.forEach {
            if (it.visible)
                it.render(Gdx.graphics.rawDeltaTime)
        }
    }

    /**
     * Dodaje nowy ekran do stosu. Dodany ekran staje się aktywny, podczas gdy poprzednio aktywny ekran jest usypiany.
     */
    fun pushScreen(screen: BaseScreen) {
        if (isStackBeingModified.get()) return
        isStackBeingModified.set(true)

        // ustawienie pól ekranu
        screen.stwrBird = this

        val currentScreen = if (screenStack.isNotEmpty()) screenStack.peek() else null

        screenStack.push(screen)
        screen.visible = true
        screen.create()
        screen.show()
        screen.resume()

        if (screenStack.size > 1) {
            // Jeśli dodany ekran nachodzi na inny, wyświetl animację nakładania
            screen.fadeIn {
                currentScreen?.apply {
                    visible = false
                    pause()
                    hide()
                }
            }
        }

        isStackBeingModified.set(false)
    }

    /**
     * Zamyka aktywny ekran. Jeżeli zostanie zamknięty ostatni ekran, aplikacja zakończy działanie.
     */
    fun popScreen() {
        if (isStackBeingModified.get()) return
        isStackBeingModified.set(true)

        fun cleanScreen(screen: BaseScreen) {
            screen.apply {
                visible = false
                pause()
                hide()
                dispose()
            }
        }

        if (screenStack.size > 1) {
            screenStack[screenStack.size - 2].apply {
                visible = true
                show()
                resume()
            }

            screenStack.peek().fadeOut {
                cleanScreen(screenStack.pop())
                isStackBeingModified.set(false)
            }
        } else {
            // brak ekranów, zakończ aplikację
            cleanScreen(screenStack.pop())
            dispose()
            exitProcess(0)
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
        Audio.dispose()
    }

    companion object {
        var android: Android? = null
            private set
    }
}