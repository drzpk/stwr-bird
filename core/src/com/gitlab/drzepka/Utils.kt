package com.gitlab.drzepka

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell

/**
 * Ustawia szerokość elementu w tablicy z zachowaniem proporcji
 */
fun <T : Actor?> Cell<T>.trueWidth(width: Float): Cell<T> {
    val ratio = prefWidth / prefHeight
    this.width(width)
    this.height(width / ratio)
    return this
}