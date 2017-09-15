package com.gitlab.drzepka.components

import com.badlogic.gdx.scenes.scene2d.Actor

abstract class BaseActor : Actor() {

    /** Wywoływana po utworzeniu obiektu i ustawieniu pól */
    open fun prepare() = Unit
    /** Wywoływana, gdy aktor powinien zwolnić zaalokowane zasoby */
    open fun dispose() = Unit

    override fun remove(): Boolean {
        dispose()
        return super.remove()
    }
}