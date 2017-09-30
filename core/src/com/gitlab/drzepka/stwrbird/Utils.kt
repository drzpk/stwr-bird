package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Widget

/**
 * Ustawia szerokość elementu w tablicy z zachowaniem proporcji
 */
fun <T : Actor?> Cell<T>.trueWidth(width: Float): Cell<T> {
    val ratio = prefWidth / prefHeight
    this.width(width)
    this.height(width / ratio)
    return this
}

fun <T: Widget> T.trueWidth(width: Float): T {
    val ratio = prefWidth / prefHeight
    this.width = width
    this.height = width / ratio
    return this
}