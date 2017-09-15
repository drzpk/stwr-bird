package com.gitlab.drzepka.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.gitlab.drzepka.Commons
import java.util.*

/**
 * Aktor obsługujący tło i rury.
 */
class BackgroundActor : BaseActor() {

    /** Wysokość podłoża */
    private val GROUND_HEIGHT = Commons.dpi(80)
    /** Szerokość rury */
    private val PIPE_WIDTH = Commons.dpi(65)
    /** Odległość między rurami */
    private val PIPE_DISTANCE = Commons.dpi(120)
    /** Wysokość przestrzeni między rurami */
    private val PIPE_GAP = Commons.dpi(115)
    /** Minimalna wysokość widocznej częsci rury */
    private val MIN_PIPE_HEIGHT = Commons.dpi(50)


    private val backgroundDay: TextureRegion by lazy { Commons.atlas.findRegion("background_day") }
    //private val backgroundNight: TextureRegion by lazy { Commons.atlas.findRegion("background_night") }
    private val ground: TextureRegion by lazy { Commons.atlas.findRegion("ground") }
    private val greenPipe: TextureRegion by lazy { Commons.atlas.findRegion("pipe_green") }

    private lateinit var chosenBackground: TextureRegion
    private val pipeQueue = ArrayDeque<Pipe>()

    private var groundSeries = 0
    private var groundOffset = 0f
    private var groundWidth = 0
    private var pipeHeight = 0f

    override fun prepare() {
        // obliczanie długości i liczby podłóg
        val ratio = ground.regionWidth.toFloat() / ground.regionHeight
        groundWidth = Math.round(ratio * GROUND_HEIGHT)
        groundSeries = Math.ceil(Gdx.app.graphics.width.toDouble() / groundWidth).toInt()

        // obliczenie maksymalnej długości rury
        val pipeRatio = PIPE_WIDTH / greenPipe.regionWidth
        pipeHeight = greenPipe.regionHeight * pipeRatio

        // wypełnienie kolejki rurami
        val amount = Math.ceil(Gdx.app.graphics.width * 2f / (PIPE_WIDTH + PIPE_GAP).toDouble()).toInt()
        var position = Gdx.app.graphics.width * 1.5f
        for (i in 0 until amount) {
            val pipe = Pipe(position)
            computeGap(pipe)
            pipeQueue.add(pipe)
            position += (PIPE_WIDTH + PIPE_DISTANCE)
        }

        // TODO: losowanie tła
        chosenBackground = backgroundDay
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val moveDistance = Commons.SPEED * Gdx.graphics.deltaTime

        // TŁO
        batch?.draw(chosenBackground, 0f, 0f, Gdx.app.graphics.width.toFloat(), Gdx.app.graphics.height.toFloat())

        // RURY
        if (pipeQueue.first.toBeRemoved) {
            val pipe = pipeQueue.removeFirst()
            pipe.position = pipeQueue.last.position + PIPE_WIDTH + PIPE_DISTANCE
            pipe.toBeRemoved = false
            computeGap(pipe)
            pipeQueue.addLast(pipe)
        }

        for (pipe in pipeQueue) {
            pipe.move(moveDistance)
            if (pipe.isInViewport()) {
                // dolna rura
                batch?.draw(greenPipe,
                        pipe.position,
                        pipe.gapPoint - pipeHeight,
                        PIPE_WIDTH,
                        pipeHeight)

                // górna rura
                batch?.draw(greenPipe.texture,
                        pipe.position,
                        pipe.gapPoint + PIPE_GAP,
                        PIPE_WIDTH,
                        pipeHeight,
                        greenPipe.regionX,
                        greenPipe.regionY,
                        greenPipe.regionWidth,
                        greenPipe.regionHeight,
                        false,
                        true)
            }
        }

        // PODŁOGA
        for (i in 0..groundSeries)
            batch?.draw(ground, i * groundWidth - groundOffset, 0f, groundWidth.toFloat(), GROUND_HEIGHT)

        groundOffset += moveDistance
        if (groundOffset >= groundWidth)
            groundOffset -= groundWidth
    }

    private fun computeGap(pipe: Pipe) {
        val range = Gdx.app.graphics.height - GROUND_HEIGHT - PIPE_GAP - 2 * MIN_PIPE_HEIGHT
        val random = Random().nextInt(range.toInt())
        pipe.gapPoint = random + GROUND_HEIGHT + MIN_PIPE_HEIGHT
    }

    inner class Pipe(var position: Float) {
        var gapPoint = 0f
        var toBeRemoved = false

        fun move(delta: Float) {
            position -= delta
            if (position + PIPE_WIDTH < 0)
                toBeRemoved = true
        }

        fun isInViewport() = position <= Gdx.app.graphics.width
    }
}