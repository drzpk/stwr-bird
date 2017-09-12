package com.gitlab.drzepka.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class GameScreen : BaseScreen() {

    /** Prędkość w pikselach na sekundę */
    private val SPEED = 240

    private val backgroundDay: TextureRegion by lazy { atlas.findRegion("background_day") }
    private val batch = SpriteBatch()

    private var backgroundSeries = 0
    private var backgroundOffset = 0
    private var backgroundWidth = 0

    override fun create() {
        val scale = backgroundDay.regionHeight.toDouble() / height
        backgroundWidth = Math.round(backgroundDay.regionWidth / scale.toFloat())
        backgroundSeries = Math.round(width.toDouble() / (backgroundDay.regionWidth * scale)).toInt()
    }

    override fun render(delta: Float) {
        super.render(delta)
        batch.begin()
        for (i in 0 until backgroundSeries)
            batch.draw(backgroundDay, -backgroundOffset.toFloat() + backgroundWidth * i, 0f, backgroundWidth.toFloat(), height.toFloat())
        batch.end()

        backgroundOffset += Math.round(SPEED.toDouble() * delta).toInt()
        if (backgroundOffset >= backgroundWidth)
            backgroundOffset -= backgroundWidth
    }
}