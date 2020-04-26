package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Commons {
    /** Prędkość obiektów w pikselach na sekundę */
    val SPEED = dpi(115)
    /** Atlas tekstur */
    val atlas: TextureAtlas by lazy { TextureAtlas(Gdx.files.internal("texture_atlas.atlas")) }
    /** Interfejs Androida */
    val android: Android = StwrBird.android!!
    /**
     * Ustawia tryb debugowania.
     */
    const val DEBUG = false

    /**
     * Konwertuje DPI na piksele.
     */
    fun dpi(dpi: Int): Float = dpi * Gdx.graphics.density

    /**
     * Konwertuje DPI na piksele.
     */
    fun dpi(dpi: Float): Float = dpi * Gdx.graphics.density
}