package com.gitlab.drzepka.stwrbird.components.game.pipe

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.BaseActor
import com.gitlab.drzepka.stwrbird.config.Pipes
import java.util.*

abstract class BasePipeColumn(y: Float, height: Float) : BaseActor() {

    /** Czy kolumna przeszła już przez cały ekran i jest całkowicie niewidoczna */
    var isDead = false
        private set

    /** Współczynik rozmiaru przerwy między rurami względem standardowego rozmiaru*/
    var gapSizeFactor = 1f

    protected open var gapPos = y + Pipes.MIN_PIPE_HEIGHT

    private val upperCollisionPolygon = Polygon()
    private val lowerCollisionPolygon = Polygon()

    init {
        this.y = y
        this.width = Pipes.PIPE_WIDTH
        this.height = height

        upperCollisionPolygon.vertices = floatArrayOf(
                0f, 0f,
                0f, height,
                width, height,
                width, 0f
        )
        lowerCollisionPolygon.vertices = upperCollisionPolygon.vertices.copyOf()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // Dolna rura
        batch?.draw(
                greenPipe,
                x,
                gapPos - pipeHeight,
                width,
                pipeHeight
        )

        // Górna rura
        batch?.draw(
                greenPipe.texture,
                x,
                gapPos + Pipes.GAP_SIZE * gapSizeFactor,
                width,
                pipeHeight,
                greenPipe.regionX,
                greenPipe.regionY,
                greenPipe.regionWidth,
                greenPipe.regionHeight,
                false,
                true
        )
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        shapes?.setColor(1f, 0f, 0f, 1f)
        shapes?.polygon(upperCollisionPolygon.transformedVertices)
        shapes?.polygon(lowerCollisionPolygon.transformedVertices)
    }

    /**
     * Przesuwa rurę w kierunku gracza
     */
    fun move(distance: Float) {
        moveBy(-distance, 0f)
        if (x + width < 0)
            isDead = true
    }

    fun reset(moveAfter: BasePipeColumn? = null, distanceFactor: Float = 1f) {
        if (moveAfter != null)
            x = moveAfter.x + Pipes.PIPE_WIDTH + Pipes.PIPE_DISTANCE * distanceFactor
        isDead = false
    }

    fun setRandomGapPosition() {
        setGapPosition(random.nextFloat())
    }

    fun setGapPosition(percentage: Float) {
        val range = height - Pipes.GAP_SIZE * gapSizeFactor - 2 * Pipes.MIN_PIPE_HEIGHT
        gapPos = range * percentage + y + Pipes.MIN_PIPE_HEIGHT
    }

    fun isInViewport() = x <= Gdx.graphics.width

    fun collidesWith(polygon: Polygon): Boolean {
        upperCollisionPolygon.setPosition(x, gapPos - height)
        lowerCollisionPolygon.setPosition(x, gapPos + Pipes.GAP_SIZE * gapSizeFactor)

        return Intersector.overlapConvexPolygons(polygon, upperCollisionPolygon)
                || Intersector.overlapConvexPolygons(polygon, lowerCollisionPolygon)
    }

    companion object {
        private val greenPipe: TextureRegion by lazy { Commons.atlas.findRegion("pipe_green") }
        private val pipeHeight = greenPipe.regionHeight * Pipes.PIPE_WIDTH / greenPipe.regionWidth

        private val random = Random()
    }
}