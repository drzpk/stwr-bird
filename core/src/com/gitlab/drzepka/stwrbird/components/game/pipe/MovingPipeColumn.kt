package com.gitlab.drzepka.stwrbird.components.game.pipe

import com.gitlab.drzepka.stwrbird.config.Pipes

class MovingPipeColumn(y: Float, height: Float) : BasePipeColumn(y, height) {

    private val minGapPos: Float = y + Pipes.MIN_PIPE_HEIGHT
    private val maxGapPos: Float = height - Pipes.MIN_PIPE_HEIGHT - Pipes.PIPE_GAP

    /** Kierunek ruchu przestrzeni między rurami, 1 - góra, -1 - dół*/
    private var movementDirection = 1


    override fun act(delta: Float) {
        gapPos += Pipes.VERTICAL_MOVEMENT_SPEED * delta * movementDirection
        if (gapPos > maxGapPos) {
            gapPos = maxGapPos
            movementDirection = -1
        } else if (gapPos < minGapPos) {
            gapPos = minGapPos
            movementDirection = 1
        }
    }
}