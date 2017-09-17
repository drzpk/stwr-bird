@file:Suppress("ConstantConditionIf")

package com.gitlab.drzepka.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.gitlab.drzepka.Commons
import java.util.*

class BirdActor : BaseActor() {

    private val BIRD_FRAME_DURATION = 0.2f
    private val GRAVITY_DELTA = Commons.dpi(0.43f)
    private val BIRD_SIZE = Commons.dpi(43)

    private val animation: Animation<TextureRegion>
    private val bird: Sprite
    private val debugRenderer = if (Commons.DEBUG) ShapeRenderer() else null

    private var started = true
    private var speed = 0f
    private var birdHeight = 0f
    private var stateTime = 0f

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
        stateTime += delta
        bird.setRegion(animation.getKeyFrame(stateTime))

        speed -= GRAVITY_DELTA
        bird.y = Math.max(BackgroundActor.GROUND_HEIGHT, bird.y + speed)
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
            speed = GRAVITY_DELTA * 13
        }
        return null
    }
}