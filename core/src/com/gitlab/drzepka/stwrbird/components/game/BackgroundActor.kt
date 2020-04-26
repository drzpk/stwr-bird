package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.utils.Timer
import com.gitlab.drzepka.stwrbird.Audio
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.BaseActor
import com.gitlab.drzepka.stwrbird.font.BaseFont
import com.gitlab.drzepka.stwrbird.font.BigFont
import java.util.*
import kotlin.collections.ArrayList
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

    /** Szerokość rury */
    private val PIPE_WIDTH = Commons.dpi(79)
    /** Odległość między rurami */
    private val PIPE_DISTANCE = Commons.dpi(140)
    /** Wysokość przestrzeni między rurami */
    private val PIPE_GAP = Commons.dpi(135)
    /** Minimalna wysokość widocznej częsci rury */
    private val MIN_PIPE_HEIGHT = Commons.dpi(50)

    /** Startuje lub zatrzymuje ruch tła */
    var started = true
    /** Włącza lub wyłącza generowanie i ruch rur */
    var generatePipes = false

    private val backgroundDay: TextureRegion by lazy { Commons.atlas.findRegion("background_day") }
    private val backgroundNight: TextureRegion by lazy { Commons.atlas.findRegion("background_night") }
    private val ground: TextureRegion by lazy { Commons.atlas.findRegion("ground") }
    private val greenPipe: TextureRegion by lazy { Commons.atlas.findRegion("pipe_green") }
    private val scoreText: BigFont by lazy { BigFont() }

    private lateinit var chosenBackground: TextureRegion
    private val pipeQueue = PipeQueue()
    private val upperPipePolygon = Polygon()
    private val lowerPipePolygon = Polygon()

    private var groundSeries = 0
    private var groundOffset = 0f
    private var groundWidth = 0
    private var pipeHeight = 0f
    private var pipeSwitched = false

    val score: Int
        get() = scoreText.value


    override fun prepare() {
        // obliczanie długości i liczby podłóg
        val ratio = ground.regionWidth.toFloat() / ground.regionHeight
        groundWidth = (ratio * GROUND_HEIGHT).roundToInt()
        groundSeries = ceil(Gdx.app.graphics.width.toDouble() / groundWidth).toInt()

        // obliczenie maksymalnej długości rury
        val pipeRatio = PIPE_WIDTH / greenPipe.regionWidth
        pipeHeight = greenPipe.regionHeight * pipeRatio

        scoreText.setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height - Commons.dpi(81), BaseFont.Align.CENTER)
        reset()

        // Ustawienie wielokąta rur do sprawdzania kolizji.
        upperPipePolygon.vertices = floatArrayOf(
                0f, 0f,
                0f, pipeHeight,
                PIPE_WIDTH, pipeHeight,
                PIPE_WIDTH, 0f
        )
        lowerPipePolygon.vertices = upperPipePolygon.vertices.copyOf()

        debug = Commons.DEBUG
    }

    override fun reset() {
        // wypełnienie kolejki rurami
        val amount = ceil(Gdx.app.graphics.width * 2f / (PIPE_WIDTH + PIPE_DISTANCE).toDouble()).toInt()
        var position = Gdx.app.graphics.width * 1.5f
        val list = ArrayList<Pipe>()
        for (i in 0 until amount) {
            val pipe = Pipe(position)
            computeGap(pipe)
            list.add(pipe)
            position += (PIPE_WIDTH + PIPE_DISTANCE)
        }
        pipeQueue.setCollection(list)

        // losowanie tła
        chosenBackground = if (Random().nextBoolean()) backgroundDay else backgroundNight

        // zresetowanie flag i zmiennych
        started = true
        generatePipes = false
        pipeSwitched = false
        scoreText.value = 0
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
        val nearestPipe = pipeQueue.minBy { abs(rightX - it.position) }!!
        upperPipePolygon.setPosition(nearestPipe.position, nearestPipe.gapPoint + PIPE_GAP)
        lowerPipePolygon.setPosition(nearestPipe.position, nearestPipe.gapPoint - pipeHeight)
        if (Intersector.overlapConvexPolygons(actorPolygon, upperPipePolygon)
                || Intersector.overlapConvexPolygons(actorPolygon, lowerPipePolygon)) {
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
        if (!pipeSwitched && nearestPipe.position + PIPE_WIDTH < rightX) {
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
            if (pipeQueue.first().toBeRemoved) {
                val pipe = pipeQueue.first()
                pipe.position = pipeQueue.last().position + PIPE_WIDTH + PIPE_DISTANCE
                pipe.toBeRemoved = false
                computeGap(pipe)
                pipeQueue.shift()
                pipeSwitched = false
            }
            pipeQueue.forEach { it.move(moveDistance) }
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

        // WYNIK
        scoreText.draw(batch!!)
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        // przed rozpoczęciem gry rury nie mają jeszcze obliczonej pozycji
        if (!generatePipes)
            return
        shapes?.setColor(1f, 0f, 0f, 1f)
        shapes?.polygon(upperPipePolygon.transformedVertices)
        shapes?.polygon(lowerPipePolygon.transformedVertices)
    }

    private fun computeGap(pipe: Pipe) {
        val range = Gdx.app.graphics.height - GROUND_HEIGHT - PIPE_GAP - 2 * MIN_PIPE_HEIGHT
        val random = Random().nextInt(range.toInt())
        pipe.gapPoint = random + GROUND_HEIGHT + MIN_PIPE_HEIGHT
    }

    inner class Pipe(/** Pozycja X początku rury*/ var position: Float) {
        /** Dolny punkt Y przestrzeni pomiędzy rurami - (górny punkt Y dolnej rury) */
        var gapPoint = 0f
        var toBeRemoved = false

        fun move(delta: Float) {
            position -= delta
            if (position + PIPE_WIDTH < 0)
                toBeRemoved = true
        }

        fun isInViewport() = position <= Gdx.app.graphics.width
    }

    /**
     * Kolejka rur.
     */
    class PipeQueue : Iterable<Pipe> {

        private lateinit var array: Array<Pipe>
        private var current = 0

        /**
         * Ustawia nową zawartość kolejki na podstawie podanej kolekcji.
         */
        fun setCollection(collection: Collection<Pipe>) {
            array = collection.toTypedArray()
            current = 0
        }

        /**
         * Zwraca pierwszy element kolejki.
         */
        fun first(): Pipe = array[current]

        /**
         * Zwraca ostatni element kolejki.
         */
        fun last(): Pipe = array[if (current > 0) current - 1 else array.lastIndex]

        /**
         * Przesuwa pierwszy element na ostatnią pozycję.
         */
        fun shift() {
            current = (current + 1) % array.size
        }

        override fun iterator(): Iterator<Pipe> {
            return object : Iterator<Pipe> {

                private var index = current
                private var passed = 0

                override fun hasNext(): Boolean = passed < array.size

                override fun next(): Pipe {
                    val pipe = array[index]
                    index = (index + 1) % array.size
                    passed++
                    return pipe
                }

            }
        }
    }
}
