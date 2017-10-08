package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.math.Polygon

interface ActorInterface {

    /** Wywoływana po utworzeniu obiektu i ustawieniu pól */
    fun prepare() = Unit

    /** Resetuje aktora do domyślnego stanu */
    fun reset() = Unit

    /** Wywoływana, gdy aktor powinien zwolnić zaalokowane zasoby */
    fun dispose() = Unit

    /**
     * Zwraca wielokąt otaczający aktora. Używany w celu wykrywania kolizji.
     */
    fun getPolygon(): Polygon? = null
}