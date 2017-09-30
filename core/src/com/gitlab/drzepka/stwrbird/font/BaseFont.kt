package com.gitlab.drzepka.stwrbird.font

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.gitlab.drzepka.stwrbird.Commons

/**
 * Klasa bazowa do fontów liczbowych.
 *
 * @param fontSize rozmiar czcionki (szerokość) w pikselach
 * @param resourceName nazwa tekstury z czcionką
 * @param digitWidth szerokość znaku w pliku tesktury
 * @param digitSpacing odległość między znakami w pliku tekstury
 */
abstract class BaseFont(
        private val fontSize: Float,
        private val resourceName: String,
        private val digitWidth: Int,
        private val digitSpacing: Int = 1) {

    /** Odległość między wyświetlanymi znakami w pikselach */
    open protected val spacing = Commons.dpi(3)

    private val fontTexture: TextureRegion = Commons.atlas.findRegion(resourceName)

    private var queue: Array<Int> = arrayOf(0)
    private var posX = 0f
    private var posY = fontTexture.regionHeight.toFloat()
    private val width: Float
    private val height: Float
    private var rightAlign = false
    private var delta = 0f

    /** Wyświetlana wartość */
    var value = 0
        set(value) {
            field = value
            update()
        }

    init {
        // ustawienie wyświetlanych wymiarów czcionki
        val ratio = digitWidth.toFloat() / fontTexture.regionHeight
        width = fontSize
        height = fontSize / ratio

        // wymuszenie aktualizacji
        value = 0
    }

    /**
     * Ustawia pozycję i wyrównanie tekstu. Współrzędna Y wzkazuje na dolną krawędź napisu. Jeśli wyrównanie do
     * prawej jest włączone, współrzędna X wskazuje na prawą krawędź napisu. Pozycja musi zostać ustawiona przed
     * wartością, lub liczba będzie wyświetlana niepoprawnie.
     */
    fun setPosition(posX: Float, posY: Float, rightAlign: Boolean = false) {
        this.posX = if (!rightAlign) posX else posX - digitWidth.toFloat()
        this.posY = posY
        this.rightAlign = rightAlign
    }

    private fun update() {
        var tmpVal = value
        val list = ArrayList<Int>()
        while (tmpVal != 0) {
            val digit = tmpVal % 10
            list.add(digit * (digitWidth + digitSpacing))
            tmpVal /= 10
        }

        queue = list.toTypedArray()
        if (!rightAlign)
            queue.reverse()

        // ustawienie odległości między początkami znaków
        delta = (fontSize + spacing) * (if (rightAlign) -1 else 1)
    }

    fun draw(batch: Batch) {
        var offset = 0f
        queue.forEach {
            batch.draw(fontTexture.texture,
                    posX + offset,
                    posY,
                    width,
                    height,
                    fontTexture.regionX + it,
                    fontTexture.regionY,
                    digitWidth,
                    fontTexture.regionHeight,
                    false,
                    false)
            offset += delta
        }
    }
}
