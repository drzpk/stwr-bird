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
    private var computedPosX = 0f
    private var posY = fontTexture.regionHeight.toFloat()
    private val width: Float
    private val height: Float
    private var align = Align.LEFT
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
     * Ustawia pozycję i wyrównanie tekstu. Współrzędna Y wzkazuje na dolną krawędź napisu. Wyrównanie
     * określa pozycję X względem napisu (przykładowo jeśli wyrównanie będzie ustawione na [Align.LEFT],
     * pozycja X będzie wskazywała lewą krawędź napisu).
     *
     * Pozycja musi zostać ustawiona przed wartością, lub liczba będzie wyświetlana niepoprawnie.
     */
    fun setPosition(posX: Float, posY: Float, align: Align = Align.LEFT) {
        this.posX = posX
        this.posY = posY
        this.align = align
    }

    private fun update() {
        var tmpVal = value
        val list = ArrayList<Int>()
        while (tmpVal != 0) {
            val digit = tmpVal % 10
            list.add(digit * (digitWidth + digitSpacing))
            tmpVal /= 10
        }

        // konwersja kolejki
        queue = list.toTypedArray()
        if (align != Align.RIGHT)
            queue.reverse()

        // ustawienie odległości między POCZĄTKAMI znaków
        delta = fontSize + spacing

        // ustawienie faktycznego początku napisu, po uwgdlędnieniu wyrównania (align)
        val width = queue.size * delta - spacing
        computedPosX = when (align) {
            BaseFont.Align.LEFT -> posX
            BaseFont.Align.CENTER -> posX - width / 2
            BaseFont.Align.RIGHT -> posX - width
        }
    }

    fun draw(batch: Batch) {
        var offset = 0f
        queue.forEach {
            batch.draw(fontTexture.texture,
                    computedPosX + offset,
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

    enum class Align {
        LEFT, CENTER, RIGHT
    }
}
