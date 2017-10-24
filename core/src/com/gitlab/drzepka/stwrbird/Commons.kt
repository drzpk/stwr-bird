package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Commons {
    /** Prędkość obiektów w pikselach na sekundę */
    val SPEED = dpi(100)
    /** Atlas tekstur */
    val atlas: TextureAtlas by lazy { TextureAtlas(Gdx.files.internal("texture_atlas.atlas")) }
    /** Interfejs Androida */
    val androidInterface: AndroidInterface = StwrBird.androidInterface!!
    /**
     * Ustawia tryb debugowania.
     */
    val DEBUG = true

    /**
     * Konwertuje DPI na piksele.
     */
    fun dpi(dpi: Int): Float = dpi * Gdx.graphics.density

    /**
     * Konwertuje DPI na piksele.
     */
    fun dpi(dpi: Float): Float = dpi * Gdx.graphics.density
}