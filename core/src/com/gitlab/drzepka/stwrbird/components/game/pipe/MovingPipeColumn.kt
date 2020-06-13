package com.gitlab.drzepka.stwrbird.components.game.pipe

import com.gitlab.drzepka.stwrbird.config.Pipes

class MovingPipeColumn(y: Float, height: Float) : BasePipeColumn(y, height) {

    /** Kierunek ruchu przestrzeni między rurami, 1 - góra, -1 - dół*/
    var movementDirection = 1

    var verticalMovementSpeedFactor = 1f

    override var gapPos: Float
        get() = super.gapPos
        set(value) {
            super.gapPos = value
            maxGapPos = height - Pipes.MIN_PIPE_HEIGHT - Pipes.GAP_SIZE * gapSizeFactor
        }

    private val minGapPos: Float = y + Pipes.MIN_PIPE_HEIGHT
    private var maxGapPos: Float = height - Pipes.MIN_PIPE_HEIGHT - Pipes.GAP_SIZE * gapSizeFactor

    override fun act(delta: Float) {
        gapPos += Pipes.VERTICAL_MOVEMENT_SPEED * delta * movementDirection * verticalMovementSpeedFactor
        if (gapPos > maxGapPos) {
            gapPos = maxGapPos
            movementDirection = -1
        } else if (gapPos < minGapPos) {
            gapPos = minGapPos
            movementDirection = 1
        }
    }
}