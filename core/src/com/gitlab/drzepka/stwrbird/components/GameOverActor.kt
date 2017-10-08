package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.font.MediumFont
import com.gitlab.drzepka.stwrbird.trueWidth

class GameOverActor : Table(), ActorInterface {

    private val gameOverTitle = Image(Commons.atlas.findRegion("title_game_over"))


    override fun prepare() {
        setFillParent(true)
        add(gameOverTitle).trueWidth(Commons.dpi(250)).padBottom(Commons.dpi(35))
        row()
        add(BoardActor()).trueWidth(Commons.dpi(280))

        debug = true
    }

    override fun reset() = Unit

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

    inner class BoardActor : Table(), ActorInterface {

        private val summaryBoard = TextureRegionDrawable(Commons.atlas.findRegion("summary_board"))
        private val medal = Image(Commons.atlas.findRegion("medal_bronze"))
        private val newImage = Image(Commons.atlas.findRegion("new_label"))
        private val scoreText = MediumFont()
        private val bestScoreText = MediumFont()

        init {
            background = summaryBoard

            medal.trueWidth(Commons.dpi(55))
            medal.setPosition(Commons.dpi(31), Commons.dpi(35))
            newImage.trueWidth(Commons.dpi(34))
            newImage.setPosition(Commons.dpi(174), Commons.dpi(53))

            scoreText.setPosition(Commons.dpi(251), Commons.dpi(79), true)
            scoreText.value = 1234
            bestScoreText.setPosition(Commons.dpi(251), Commons.dpi(29), true)
            bestScoreText.value = 99
        }

        override fun prepare() = Unit

        override fun reset() = Unit

        override fun draw(batch: Batch?, parentAlpha: Float) {
            super.draw(batch, parentAlpha)
            applyTransform(batch, computeTransform())
            medal.draw(batch!!, 1f)
            newImage.draw(batch, 1f)
            scoreText.draw(batch)
            bestScoreText.draw(batch)
        }
    }
}