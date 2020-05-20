package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
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

/**
 * Ustawia szerokość widżetu z zachowaniem proporcji. Parametr [updateOrigin] określa, czy środek
 * elementu ma zostać w tym samym miejscu (np. jeśli element jest wyśrodkowany).
 */
fun <T : Widget> T.trueWidth(width: Float, updateOrigin: Boolean = false): T {
    val ratio = prefWidth / prefHeight
    if (updateOrigin) {
        // zmiana współrzędnych
        this.x -= (width - this.width) / 2
        this.y -= (width / ratio - this.height) / 2
    }

    this.width = width
    this.height = width / ratio
    return this
}

/**
 * Sprawdza, czy dana liczba znajduje się w przedziale (domkniętym).
 */
infix fun Number.inRange(pair: Pair<Number, Number>): Boolean =
        this.toFloat() >= pair.first.toFloat() && this.toFloat() <= pair.second.toFloat()

fun loadBitmapFont(ttfFontPath: String, fontSizePx: Int): BitmapFont {
    val internal = Gdx.files.internal(ttfFontPath)
    val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = fontSizePx
    parameter.color = Color(0f, 0f, 0f, 1f)

    val generator = FreeTypeFontGenerator(internal)
    val generated = generator.generateFont(parameter)
    generator.dispose()

    return generated
}