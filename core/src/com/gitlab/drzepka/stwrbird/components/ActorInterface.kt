package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.graphics.g2d.Sprite

interface ActorInterface {

    /** Wywoływana po utworzeniu obiektu i ustawieniu pól */
    fun prepare() = Unit
    /** Wywoływana, gdy aktor powinien zwolnić zaalokowane zasoby */
    fun dispose() = Unit

    /**
     * Zwraca głównego [Sprite]'a danego aktora
     */
    fun getMainSprite(): Sprite? = null
}