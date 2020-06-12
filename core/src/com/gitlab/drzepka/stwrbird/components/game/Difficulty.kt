package com.gitlab.drzepka.stwrbird.components.game

import com.gitlab.drzepka.stwrbird.components.game.pipe.BasePipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.MovingPipeColumn
import com.gitlab.drzepka.stwrbird.components.game.pipe.StationaryPipeColumn
import kotlin.reflect.KClass

val DIFFICULTY_LEVELS = listOf(
        StationaryDifficulty(0, 3),
        RandomMovingDifficulty(3, null)
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
    override fun preparePipe(pipe: StationaryPipeColumn, relativeNo: Int, absoluteNo: Int) = Unit
}

class RandomMovingDifficulty(fromPipe: Int, toPipe: Int?)
    : Difficulty<MovingPipeColumn>(MovingPipeColumn::class, fromPipe, toPipe) {
    override fun preparePipe(pipe: MovingPipeColumn, relativeNo: Int, absoluteNo: Int) = Unit
}