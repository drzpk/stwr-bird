package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Timer
import com.gitlab.drzepka.stwrbird.Audio
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.BaseActor
import com.gitlab.drzepka.stwrbird.components.game.pipe.BasePipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.PipeDispatcher
import com.gitlab.drzepka.stwrbird.config.Pipes
import com.gitlab.drzepka.stwrbird.font.BaseFont
import com.gitlab.drzepka.stwrbird.font.BigFont
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Aktor obsługujący tło i rury.
 */
class BackgroundActor : BaseActor() {

    companion object {
        /** Wysokość podłoża */
        internal val GROUND_HEIGHT = Commons.dpi(107)
    }

    /** Startuje lub zatrzymuje ruch tła */
    var started = true

    /** Włącza lub wyłącza generowanie i ruch rur */
    var generatePipes = false

    private val backgroundDay: TextureRegion by lazy { Commons.atlas.findRegion("background_day") }
    private val backgroundNight: TextureRegion by lazy { Commons.atlas.findRegion("background_night") }
    private val ground: TextureRegion by lazy { Commons.atlas.findRegion("ground") }
    private val scoreText: BigFont by lazy { BigFont() }

    private lateinit var chosenBackground: TextureRegion
    private var pipeDispatcher: PipeDispatcher
    private val pipeQueue: ArrayDeque<BasePipeColumn>
    private val queueSize = ceil(Gdx.app.graphics.width * 2f / (Pipes.PIPE_WIDTH + Pipes.PIPE_DISTANCE).toDouble()).toInt()


    private var groundSeries = 0
    private var groundOffset = 0f
    private var groundWidth = 0
    private var pipeSwitched = false

    val score: Int
        get() = scoreText.value

    init {
        pipeDispatcher = PipeDispatcher(queueSize, GROUND_HEIGHT, Gdx.graphics.height.toFloat() - GROUND_HEIGHT)
        pipeQueue = ArrayDeque(queueSize)
    }

    override fun prepare() {
        // obliczanie długości i liczby podłóg
        val ratio = ground.regionWidth.toFloat() / ground.regionHeight
        groundWidth = (ratio * GROUND_HEIGHT).roundToInt()
        groundSeries = ceil(Gdx.app.graphics.width.toDouble() / groundWidth).toInt()

        scoreText.setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height - Commons.dpi(81), BaseFont.Align.CENTER)
        reset()

        debug = Commons.DEBUG
    }

    override fun reset() {
        // losowanie tła
        chosenBackground = if (Random().nextBoolean()) backgroundDay else backgroundNight

        // zresetowanie flag i zmiennych
        started = true
        generatePipes = false
        pipeSwitched = false
        scoreText.value = 0
        pipeQueue.clear()
        pipeDispatcher.reset()
    }

    /**
     * Sprawdza, czy dany aktor koliduje z podłożem lub z jedną z rur. Ta metoda musi być wywoływana podczas każdej
     * klatki, w przeciwnym razie punkty nie będą naliczane prawidłowo.
     */
    fun checkForCollision(actor: BirdActor): Boolean {
        val actorPolygon = actor.getPolygon() ?: return false

        // sprawdzenie kolizji z podłożem
        val lowest = actorPolygon.transformedVertices?.filterIndexed { index, _ -> index % 2 != 0 }?.min()
        if (lowest != null && lowest <= GROUND_HEIGHT) {
            // dźwięk uderzenia
            Audio.hit.play()
            return true
        }

        // sprawdzenie kolizji tylko z najbliższą rurą
        val xVertices = actorPolygon.transformedVertices?.filterIndexed { index, _ -> index % 2 == 0 }!!
        val rightX = xVertices.max()!!
        val nearestPipe = pipeQueue.minBy { abs(rightX - it.x) }!!
        println("x: ${nearestPipe.x}")
        if (nearestPipe.collidesWith(actorPolygon)) {
            // kolizja ptaka z rurą
            // dźwięk uderzenia i spadania
            Audio.hit.play()
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    Audio.fall.play()
                }
            }, 0.4f)
            return true
        }

        // naliczanie punktów
        if (!pipeSwitched && nearestPipe.x + nearestPipe.width < rightX) {
            scoreText.value++
            pipeSwitched = true
            // dźwięk zdobywania punktów
            Audio.point.play()
        }

        return false
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!started) return

        val moveDistance = Commons.SPEED * delta

        // RURY
        if (generatePipes) {
            if (pipeQueue.firstOrNull()?.isDead == true || pipeQueue.size < queueSize) {
                val pipe = pipeDispatcher.getNextPipe()
                if (pipeQueue.size == queueSize)
                    pipeQueue.removeFirst()
                pipeQueue.add(pipe)
                pipeSwitched = false
            }

            pipeQueue.forEach {
                it.move(moveDistance)
                it.act(delta)
            }
        }

        // TŁO
        groundOffset += moveDistance
        if (groundOffset >= groundWidth)
            groundOffset -= groundWidth
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // TŁO
        batch?.draw(chosenBackground, 0f, 0f, Gdx.app.graphics.width.toFloat(), Gdx.app.graphics.height.toFloat())

        // RURY
        for (pipe in pipeQueue) {
            if (pipe.isInViewport())
                pipe.draw(batch, parentAlpha)
        }

        // PODŁOGA
        for (i in 0..groundSeries)
            batch?.draw(ground, i * groundWidth - groundOffset, 0f, groundWidth.toFloat(), GROUND_HEIGHT)

        // WYNIK
        scoreText.draw(batch!!)
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        pipeQueue.firstOrNull()?.drawDebug(shapes)
    }
}
