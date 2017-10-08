@file:Suppress("ConstantConditionIf")

package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.scenes.scene2d.Actor
import com.gitlab.drzepka.stwrbird.Commons
import java.util.*

class BirdActor : BaseActor() {

    /** Czas wyświetlania jednej klatki animacji w sekundach */
    private val BIRD_FRAME_DURATION = 0.2f
    /** Siła grawitacji w pikselach na sekundę */
    private val GRAVITY_DELTA = Commons.dpi(14.07f)
    /** Prędkość ptaka po kliknięciu w pikselach na sekundę */
    private val PUSH_SPEED = Commons.dpi(4.52f)
    /** Rozmiar ptaka (szerokość) w pikselach */
    private val BIRD_SIZE = Commons.dpi(38)
    /** Współczynnik określający stosunek "pudełka kolizji" do rozmiaru ptaka */
    private val BIRD_COLLISION_BOX_FACTOR = 0.85f
    /** Pozycja ptaka względem lewej krawędzi ekranu w pikselach */
    private val BIRD_POSITION = Commons.dpi(38)
    /** Wychylenie pkata podczas kołysania się przed rozpoczęciem gry */
    private val BIRD_SWING = Commons.dpi(9)
    /** Maksymalna prędkość spadania w pikselach na sekundę (używana do obliczania pochylenia ptaka) */
    private val MAX_DOWN_SPEED = GRAVITY_DELTA * 0.97f
    /** Maksymalne odchylenie ptaka w górę w stopniach */
    private val MAX_UP_ANGLE = 27f
    /** Szybkość rotacji w górę w stopniach na sekundę */
    private val ROTATION_DELTA = 369f

    /** Włącza lub zatrzymuje ruch ptaka */
    var started = false
        set(value) {
            swingMode = false // aby przywrócić początkową wartość pola, należy wywołać metodę reset()
            field = value
        }

    private val animation: Animation<TextureRegion>
    private val bird: Sprite
    private val polygon: Polygon

    private var speed = 0f
    private var birdHeight = 0f
    private var collisionBoxXOffset = 0f
    private var collisionBoxYOffset = 0f
    private var stateTime = 0f
    private var swingMode = true

    init {
        val regionName = "bird/bird_" + when (Random().nextInt(3)) {
            0 -> "blue"
            1 -> "orange"
            else -> "red"
        }

        animation = Animation(BIRD_FRAME_DURATION, Commons.atlas.findRegions(regionName), Animation.PlayMode.LOOP_PINGPONG)
        bird = Sprite(animation.getKeyFrame(0f))
        bird.setPosition(BIRD_POSITION, Gdx.graphics.height / 2f)
        bird.setOriginCenter()

        val scale = BIRD_SIZE / animation.getKeyFrame(0f).regionWidth
        bird.setSize(bird.width * scale, bird.height * scale)

        // utworzenie wielokąta wykorzystywanego do wykrywania kolizji
        val box = bird.boundingRectangle
        polygon = Polygon(floatArrayOf(
                0f, 0f,
                0f, box.height * BIRD_COLLISION_BOX_FACTOR,
                box.width * BIRD_COLLISION_BOX_FACTOR, box.height * BIRD_COLLISION_BOX_FACTOR,
                box.width * BIRD_COLLISION_BOX_FACTOR, 0f
        ))

        // zmniejszenie pudełka kolizji
        collisionBoxXOffset = (box.width * (1f - BIRD_COLLISION_BOX_FACTOR)) / 2
        collisionBoxYOffset = (box.height * (1f - BIRD_COLLISION_BOX_FACTOR)) / 2

        polygon.setPosition(box.x, box.y)
        polygon.setOrigin(box.width / 2, box.height / 2)

        debug = Commons.DEBUG
    }

    /**
     * Resetuje pozycję ptaka do domyślnej i wyłącza jego ruch.
     */
    fun reset() {
        started = false
        birdHeight = Gdx.app.graphics.height / 2f
        stateTime = 0f
        swingMode = true
    }

    override fun getPolygon(): Polygon? = polygon

    override fun act(delta: Float) {
        super.act(delta)

        // animacja skrzydeł
        if (bird.rotation < -20) {
            stateTime = 0f
            bird.setRegion(animation.getKeyFrame(BIRD_FRAME_DURATION))
        }
        else {
            stateTime += delta
            bird.setRegion(animation.getKeyFrame(stateTime))
        }

        if (!started && swingMode) {
            // animacja kołysania się ptaka
            val height = Gdx.graphics.height / 2 + (Math.sin(stateTime.toDouble() * 6.4f) * BIRD_SWING)
            bird.y = height.toFloat()
            return
        }

        // prędkość i położenie
        speed -= GRAVITY_DELTA * delta
        if (speed < -MAX_DOWN_SPEED) speed = -MAX_DOWN_SPEED
        bird.y = Math.max(BackgroundActor.GROUND_HEIGHT, bird.y + speed)
        polygon.setPosition(bird.x + collisionBoxXOffset, bird.y + collisionBoxYOffset)
        polygon.setOrigin(bird.originX, bird.originY)

        // obrót
        if (speed >= 0) {
            bird.rotation = Math.min(bird.rotation + ROTATION_DELTA * delta, MAX_UP_ANGLE)
        }
        else {
            bird.rotation = Math.max(bird.rotation + speed * 0.26f, -90f)
        }
        polygon.rotation = bird.rotation
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        bird.draw(batch)
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        super.drawDebug(shapes)
        shapes?.setColor(1f, 0f, 0f, 1f)
        shapes?.polygon(polygon.transformedVertices)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (started) {
            speed = PUSH_SPEED
        }
        return null
    }
}
