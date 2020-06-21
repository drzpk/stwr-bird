@file:Suppress("ConstantConditionIf")

package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.scenes.scene2d.Actor
import com.gitlab.drzepka.stwrbird.Audio
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.BaseActor
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class PlayerActor : BaseActor() {

    /** Siła grawitacji w pikselach na sekundę */
    private val GRAVITY_DELTA = Commons.dpi(13.1f)

    /** Prędkość ptaka po kliknięciu w pikselach na sekundę */
    private val PUSH_SPEED = Commons.dpi(5.62f)

    /** Pozycja aktora względem lewej krawędzi ekranu w pikselach */
    private val ACTOR_POSITION = Commons.dpi(38)

    /** Wychylenie aktora podczas kołysania się przed rozpoczęciem gry */
    private val ACTOR_SWING = Commons.dpi(9)

    /** Maksymalna prędkość spadania w pikselach na sekundę (używana do obliczania pochylenia ptaka) */
    private val MAX_DOWN_SPEED = GRAVITY_DELTA * 0.97f

    /** Maksymalne odchylenie aktora w górę w stopniach */
    private val MAX_UP_ANGLE = 27f

    /** Szybkość rotacji w górę w stopniach na sekundę */
    private val ROTATION_DELTA = 369f

    /** Włącza lub zatrzymuje ruch ptaka */
    var started = false
        set(value) {
            swingMode = false // aby przywrócić początkową wartość pola, należy wywołać metodę reset()
            field = value
        }

    var gameOver = false

    var playerType = PlayerType.BIRD
        set(value) {
            field = value
            reset()
        }

    private lateinit var animation: Animation<TextureRegion>
    private var sprite = Sprite()
    private var polygon = Polygon()

    private var speed = 0f
    private var stateTime = 0f
    private var swingMode = true

    override fun prepare() {
        reset()
        debug = Commons.DEBUG
    }

    /**
     * Resetuje pozycję ptaka do domyślnej i wyłącza jego ruch.
     */
    override fun reset() {
        val regionName = when (playerType) {
            PlayerType.BIRD -> {
                "bird/bird_" + when (Random().nextInt(3)) {
                    0 -> "blue"
                    1 -> "orange"
                    else -> "red"
                }
            }
            PlayerType.CAT -> "cat/cat"
        }

        animation = Animation(playerType.frameDuration, Commons.atlas.findRegions(regionName), playerType.animationMode)

        sprite = Sprite(animation.getKeyFrame(0f))
        sprite.setPosition(ACTOR_POSITION, Gdx.app.graphics.height / 2f)

        stateTime = 0f
        speed = 0f
        sprite.rotation = 0f

        val scale = playerType.textureWidth / animation.getKeyFrame(0f).regionWidth
        sprite.setScale(scale)

        resetCollisionBox()

        started = false
        gameOver = false
        swingMode = true
    }

    override fun getPolygon(): Polygon? = polygon

    override fun act(delta: Float) {
        super.act(delta)

        // animacja skrzydeł
        if (sprite.rotation < -20 || gameOver) {
            stateTime = 0f
            sprite.setRegion(animation.getKeyFrame(playerType.frameDuration))
        } else {
            stateTime += delta
            sprite.setRegion(animation.getKeyFrame(stateTime))
        }

        if (!started && swingMode) {
            // animacja kołysania się ptaka
            val height = Gdx.graphics.height / 2 + (sin(stateTime.toDouble() * 6.4f) * ACTOR_SWING)
            sprite.y = height.toFloat()
            return
        }

        // prędkość i położenie
        speed -= GRAVITY_DELTA * delta
        if (speed < -MAX_DOWN_SPEED) speed = -MAX_DOWN_SPEED
        sprite.y = max(BackgroundActor.GROUND_HEIGHT - 10, sprite.y + speed)
        polygon.setPosition(sprite.x + sprite.originX, sprite.y + sprite.originY)

        // obrót
        if (playerType.rotate) {
            if (speed >= 0) {
                sprite.rotation = min(sprite.rotation + ROTATION_DELTA * delta, MAX_UP_ANGLE)
            } else {
                sprite.rotation = max(sprite.rotation + speed * 0.26f, -90f)
            }
            polygon.rotation = sprite.rotation
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        sprite.draw(batch)
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        super.drawDebug(shapes)
        shapes?.setColor(1f, 0f, 0f, 1f)
        shapes?.polygon(polygon.transformedVertices)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (started && Gdx.input.justTouched())
            fly()
        return null
    }

    fun fly() {
        speed = PUSH_SPEED
        // dźwięk machania skrzydłami
        Audio.wing.play(1.0f)
    }

    private fun resetCollisionBox() {
        val factor = playerType.collisionBoxFactor

        val w2 = sprite.width / 2f
        val h2 = sprite.height / 2f
        polygon = Polygon(floatArrayOf(
                -w2, -h2,
                -w2, h2,
                w2, h2,
                w2, -h2
        ))
        polygon.setScale(sprite.scaleX * factor, sprite.scaleY * factor)
    }

    enum class PlayerType(
            /** Szerokość tekstury w pikselach */
            val textureWidth: Float,
            /** Czas wyświetlania jednej klatki animacji w sekundach */
            val frameDuration: Float,
            /** Współczynnik określający stosunek "pudełka kolizji" do rozmiaru aktora */
            val collisionBoxFactor: Float,
            val rotate: Boolean,
            val animationMode: Animation.PlayMode
    ) {
        BIRD(Commons.dpi(38), 0.2f, 1f, true, Animation.PlayMode.LOOP_PINGPONG),
        CAT(Commons.dpi(85), 0.13f, 0.54f, false, Animation.PlayMode.LOOP)
    }
}
