package com.gitlab.drzepka.stwrbird.components.game

import com.gitlab.drzepka.stwrbird.components.game.pipe.BasePipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.CollapsingPipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.MovingPipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.StationaryPipeColumn
import java.util.*
import kotlin.reflect.KClass

val DIFFICULTY_LEVELS = listOf(
        StationaryDifficulty(0, 3),
        CollapsingDifficulty(3, 7),
        RandomMovement(7, 10, 1f, 1.3f),
        SynchronousMovementDifficulty(10, null)
)

abstract class Difficulty<T : BasePipeColumn>(
        val type: KClass<T>,
        val fromPipe: Int,
        val toPipe: Int?
) {

    @Suppress("UNCHECKED_CAST")
    fun basePrepareType(pipe: BasePipeColumn, relativeNo: Int, absoluteNo: Int) {
        preparePipe(pipe as T, relativeNo, absoluteNo)
    }

    /**
     * Dostosowuje rurę do odpowiedniego poziomu trudności
     * @param pipe rura
     * @param relativeNo numer rury relatywny względem tego poziomu trudności
     * @param absoluteNo numer rury od początku gry
     */
    abstract fun preparePipe(pipe: T, relativeNo: Int, absoluteNo: Int)
}

class StationaryDifficulty(fromPipe: Int, toPipe: Int?)
    : Difficulty<StationaryPipeColumn>(StationaryPipeColumn::class, fromPipe, toPipe) {
    override fun preparePipe(pipe: StationaryPipeColumn, relativeNo: Int, absoluteNo: Int) {
        pipe.setRandomGapPosition()
    }
}

class SynchronousMovementDifficulty(fromPipe: Int, toPipe: Int?)
    : Difficulty<MovingPipeColumn>(MovingPipeColumn::class, fromPipe, toPipe) {
    override fun preparePipe(pipe: MovingPipeColumn, relativeNo: Int, absoluteNo: Int) {
        val pipesPerHeight = 5
        pipe.gapSizeFactor = 1f
        pipe.setGapPosition(relativeNo.rem(pipesPerHeight + 1) / pipesPerHeight.toFloat())
    }
}

class RandomMovement(
        fromPipe: Int,
        toPipe: Int?,
        private val verticalSpeedFactor: Float,
        private val gapSizeFactor: Float
) : Difficulty<MovingPipeColumn>(MovingPipeColumn::class, fromPipe, toPipe) {

    override fun preparePipe(pipe: MovingPipeColumn, relativeNo: Int, absoluteNo: Int) {
        pipe.verticalMovementSpeedFactor = verticalSpeedFactor
        pipe.gapSizeFactor = gapSizeFactor
        pipe.setRandomGapPosition()
        pipe.movementDirection = if (Random().nextBoolean()) 1 else -1
    }
}

class CollapsingDifficulty(
    fromPipe: Int,
    toPipe: Int?
) : Difficulty<CollapsingPipeColumn>(CollapsingPipeColumn::class, fromPipe, toPipe) {
    override fun preparePipe(pipe: CollapsingPipeColumn, relativeNo: Int, absoluteNo: Int) = Unit
}