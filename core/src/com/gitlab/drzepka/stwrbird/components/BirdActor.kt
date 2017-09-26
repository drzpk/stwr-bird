@file:Suppress("ConstantConditionIf")

package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.gitlab.drzepka.stwrbird.Commons
import java.util.*

class BirdActor : BaseActor() {

    /** Czas wyświetlania jednej klatki animacji w sekundach */
    private val BIRD_FRAME_DURATION = 0.2f
    /** Siła grawitacji w pikselach na sekundę */
    private val GRAVITY_DELTA = Commons.dpi(14.07f)
    /** Prędkość ptaka po kliknięciu w pikselach na sekundę */
    private val PUSH_SPEED = Commons.dpi(4.73f)
    /** Rozmiar ptaka (szerokość) w pikselach */
    private val BIRD_SIZE = Commons.dpi(43)
    /** Maksymalna prędkość spadania w pikselach na sekundę (używana do obliczania pochylenia ptaka) */
    private val MAX_DOWN_SPEED = GRAVITY_DELTA * 0.97f
    /** Maksymalne odchylenie ptaka w górę w stopniach */
    private val MAX_UP_ANGLE = 27f
    /** Szybkość rotacji w górę w stopniach na sekundę */
    private val ROTATION_DELTA = 369f

    private val animation: Animation<TextureRegion>
    private val bird: Sprite
    private val debugRenderer = if (Commons.DEBUG) ShapeRenderer() else null

    private var started = true
    private var speed = 0f
    private var birdHeight = 0f
    private var stateTime = 0f
    private var justHit = false

    init {
        val regionName = "bird/bird_" + when (Random().nextInt(3)) {
            0 -> "blue"
            1 -> "orange"
            else -> "red"
        }

        animation = Animation(BIRD_FRAME_DURATION, Commons.atlas.findRegions(regionName), Animation.PlayMode.LOOP_PINGPONG)
        bird = Sprite(animation.getKeyFrame(0f))
        bird.setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)

        val scale = BIRD_SIZE / animation.getKeyFrame(0f).regionWidth
        bird.setSize(bird.width * scale, bird.height * scale)

        debug = true
    }

    /**
     * Zatrzymuje ruch ptaka.
     */
    fun stop() {
        started = false
    }

    /**
     * Resetuje pozycję ptaka do domyślnej.
     */
    fun reset() {
        started = true
        birdHeight = Gdx.app.graphics.height / 2f
        stateTime = 0f
    }

    override fun getMainSprite(): Sprite? = bird

    override fun act(delta: Float) {
        super.act(delta)

        // prędkość i położenie
        speed -= GRAVITY_DELTA * delta
        if (speed < -MAX_DOWN_SPEED) speed = -MAX_DOWN_SPEED
        bird.y = Math.max(BackgroundActor.GROUND_HEIGHT, bird.y + speed)

        // obrót
        if (speed >= 0) {
            bird.rotation = Math.min(bird.rotation + ROTATION_DELTA * delta, MAX_UP_ANGLE)
        }
        else {
            bird.rotation = Math.max(bird.rotation + speed * 0.26f, -90f)
        }

        // animacja
        if (bird.rotation < -20) {
            stateTime = 0f
            bird.setRegion(animation.getKeyFrame(BIRD_FRAME_DURATION))
        }
        else {
            stateTime += delta
            bird.setRegion(animation.getKeyFrame(stateTime))
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        bird.draw(batch)

        if (Commons.DEBUG) {
            batch?.end()
            debugRenderer?.begin(ShapeRenderer.ShapeType.Line)
            debugRenderer?.setColor(1f, 0f, 0f, 1f)
            Gdx.gl.glLineWidth(2f)
            val rect = bird.boundingRectangle
            debugRenderer?.rect(rect.x, rect.y, rect.width, rect.height)
            debugRenderer?.end()
            batch?.begin()
        }
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (started) {
            speed = PUSH_SPEED
        }
        return null
    }
}
