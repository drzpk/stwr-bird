package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.GameComponent
import com.gitlab.drzepka.stwrbird.trueWidth

class ControlsActor : Table(), GameComponent {

    private val playButton = Image(Commons.atlas.findRegion("play_button"))
    private val scoresButton = Image(Commons.atlas.findRegion("scores_button"))

    var onClickListener: ((button: Button) -> Unit)? = null

    init {
        add(playButton).expand()
        add(scoresButton).expand()

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClickListener?.invoke(Button.PLAY)
            }
        })
        scoresButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClickListener?.invoke(Button.SCORES)
            }
        })
    }

    override fun layout() {
        super.layout()
        val width = this.width / 2 * 0.91f
        playButton.trueWidth(width, true)
        scoresButton.trueWidth(width, true)
    }

//    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
//        if (onClickListener != null && Gdx.input.justTouched()) {
//            // sprawdzenie, czy i który przycisk został kliknięty
//
//            if (x inRange(Pair(playButton.x, playButton.x + playButton.width))
//                    && y inRange(Pair(playButton.y, playButton.y + playButton.height))) {
//                // kliknięto przycisk 'play'
//                Gdx.app.postRunnable {
//                    onClickListener?.invoke(Button.PLAY)
//                }
//            }
//
//            if (x inRange(Pair(scoresButton.x, scoresButton.x + scoresButton.width))
//                    && y inRange(Pair(scoresButton.y, scoresButton.y + scoresButton.height))) {
//                // kliknięto przycisk 'scores'
//                Gdx.app.postRunnable {
//                    onClickListener?.invoke(Button.SCORES)
//                }
//            }
//        }
//
//        return super.hit(x, y, touchable)
//    }

    override fun reset() {
        super<Table>.reset()
    }

    enum class Button {
        PLAY, SCORES
    }
}