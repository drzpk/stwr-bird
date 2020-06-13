package com.gitlab.drzepka.stwrbird.components.game.pipe

import com.badlogic.gdx.Gdx
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.config.Pipes
import kotlin.math.max
import kotlin.math.min

class CollapsingPipeColumn(y: Float, height: Float) : BasePipeColumn(y, height) {

    private val startFactor = (height - 2 * Pipes.MIN_PIPE_HEIGHT) / Pipes.GAP_SIZE
    private val endFactor = 0f
    private val collapseOffset = Commons.dpi(75) * 2

    override fun act(delta: Float) {
        val ratio = min(max(x + collapseOffset, 0f) / (Gdx.graphics.width), 1f)
        gapSizeFactor = (startFactor - endFactor) * ratio
        setGapPosition(0.6f * (1 - ratio))
    }
}