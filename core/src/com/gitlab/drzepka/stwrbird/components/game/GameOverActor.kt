package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.GameComponent
import com.gitlab.drzepka.stwrbird.font.BaseFont
import com.gitlab.drzepka.stwrbird.font.MediumFont
import com.gitlab.drzepka.stwrbird.screen.GameScreen
import com.gitlab.drzepka.stwrbird.trueWidth
import java.util.*

class GameOverActor(private val gameScreen: GameScreen) : Table(), GameComponent {

    private val gameOverTitle = Image(Commons.atlas.findRegion("title_game_over"))
    private val boardActor = BoardActor()
    private val controlsActor = ControlsActor()
    private var dirty = true

    /** Wynik po ostatniej grze */
    var score = 0
        set(value) {
            field = value
            dirty = true
        }

    /** Najlepszy wynik */
    var bestScore = 0
        set(value) {
            field = value
            dirty = true
        }

    /** Czy wynik po ostatniej grze jest najwyższy */
    var newBest = false
        set(value) {
            field = value
            dirty = true
        }

    /** Przyznany medal */
    var medal = Medal.NONE
        set(value) {
            field = value
            dirty = true
        }


    override fun prepare() {
        setFillParent(true)
        add(gameOverTitle).trueWidth(Commons.dpi(250)).padBottom(Commons.dpi(35))
        row()
        add(boardActor).trueWidth(Commons.dpi(280))
        row().spaceTop(Commons.dpi(20))

        add(controlsActor).trueWidth(Commons.dpi(280))
        controlsActor.onClickListener = {
            when (it) {
                ControlsActor.Button.PLAY -> gameScreen.setMode(GameScreen.Mode.TAP_TO_PLAY)
                ControlsActor.Button.SCORES ->
                    Commons.android.toast("Najlepsze wyniki nie są w tej chwili dostępne", false)
            }
        }

        debug = Commons.DEBUG
    }

    override fun reset() = Unit

    inner class BoardActor : Table(), GameComponent {

        private val summaryBoard = TextureRegionDrawable(Commons.atlas.findRegion("summary_board"))
        private val newLabel = Image(Commons.atlas.findRegion("new_label"))
        private val scoreText = MediumFont()
        private val bestScoreText = MediumFont()
        private var medal: Image? = null

        init {
            background = summaryBoard

            newLabel.trueWidth(Commons.dpi(34))
            newLabel.setPosition(Commons.dpi(174), Commons.dpi(53))

            scoreText.setPosition(Commons.dpi(251), Commons.dpi(79), BaseFont.Align.RIGHT)
            bestScoreText.setPosition(Commons.dpi(251), Commons.dpi(29), BaseFont.Align.RIGHT)
        }

        override fun prepare() = Unit

        override fun reset() = Unit

        override fun draw(batch: Batch?, parentAlpha: Float) {
            super.draw(batch!!, parentAlpha)
            if (dirty) refresh()

            applyTransform(batch, computeTransform())
            medal?.draw(batch, 1f)
            scoreText.draw(batch)
            bestScoreText.draw(batch)
            if (newBest) newLabel.draw(batch, 1f)
            resetTransform(batch)
        }

        private fun refresh() {
            dirty = false
            scoreText.value = score
            bestScoreText.value = bestScore

            val medalName = this@GameOverActor.medal
            medal = if (medalName != Medal.NONE)
                Image(Commons.atlas.findRegion("medal_" + medalName.toString().toLowerCase(Locale.ENGLISH)))
            else
                null
            medal?.trueWidth(Commons.dpi(55))
            medal?.setPosition(Commons.dpi(31), Commons.dpi(35))
        }
    }

    enum class Medal {
        NONE, BRONZE, SILVER, GOLD, PLATINIUM
    }
}