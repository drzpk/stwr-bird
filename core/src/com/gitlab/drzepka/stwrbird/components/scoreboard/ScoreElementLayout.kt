package com.gitlab.drzepka.stwrbird.components.scoreboard

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.font.BaseFont
import com.gitlab.drzepka.stwrbird.font.MediumFont
import com.gitlab.drzepka.stwrbird.model.Medal
import com.gitlab.drzepka.stwrbird.model.Score
import java.util.*

class ScoreElementLayout(pos: Int, score: Score) : Table() {

    private val positionFont = MediumFont()
    private val scoreFont = MediumFont()
    private val playTimeFont = MediumFont()

    init {
        initNinePatch()
        pad(PADDING)
        add().width(WIDTH + PADDING).height(HEIGHT + PADDING)

        positionFont.value = pos
        scoreFont.value = score.score
        playTimeFont.value = score.playTime
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch!!.draw(backgroundImage, x + padLeft, y + padBottom,
                originX, originY, width - padLeft - padRight, height - padBottom - padTop,
                scaleX, scaleY, rotation)

        batch.draw(medals!![0],
                x + padLeft + WIDTH * 0.28f,
                y + padBottom + HEIGHT * 0.23f,
                MEDAL_WIDTH,
                MEDAL_WIDTH)

        positionFont.setPosition(
                x + padLeft + WIDTH * 0.04f,
                y + padBottom + HEIGHT * 0.81f,
                align = BaseFont.Align.LEFT
        )
        positionFont.value = positionFont.value // todo: do poprawienia
        positionFont.draw(batch)

        scoreFont.setPosition(
                x + padLeft + WIDTH * 0.91f,
                y + padBottom + HEIGHT * 0.6f,
                align = BaseFont.Align.RIGHT
        )
        scoreFont.value = scoreFont.value // todo: do poprawienia
        scoreFont.draw(batch)

        playTimeFont.setPosition(
                x + padLeft + WIDTH * 0.91f,
                y + padBottom + HEIGHT * 0.21f,
                align = BaseFont.Align.RIGHT
        )
        playTimeFont.value = playTimeFont.value // todo: do poprawienia
        playTimeFont.draw(batch)
    }

    companion object {
        private const val RATIO = 114f / 226

        private val WIDTH = Commons.dpi(250)
        private val HEIGHT = WIDTH * RATIO
        private val PADDING = Commons.dpi(5)
        private val MEDAL_WIDTH = WIDTH * 0.21f

        private var backgroundImage: TextureRegion? = null
        private var medals: Array<TextureRegion>? = null

        private fun initNinePatch() {
            if (backgroundImage != null)
                return

            backgroundImage = TextureRegion(Commons.atlas.findRegion("high_score_item"))

            medals = Medal.values()
                    .filter { it != Medal.NONE }
                    .map { it.name.toLowerCase(Locale.ROOT) }
                    .map { TextureRegion(Commons.atlas.findRegion("medal_$it")) }
                    .toTypedArray()
        }
    }
}