package com.gitlab.drzepka

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Commons {
    /** Prędkość obiektów w pikselach na sekundę */
    val SPEED = dpi(100)
    /** Atlas tekstur */
    val atlas: TextureAtlas by lazy { TextureAtlas(Gdx.files.internal("texture_atlas.atlas")) }

    /**
     * Konwertuje DPI na piksele.
     */
    fun dpi(dpi: Int): Float = dpi * Gdx.graphics.density
}