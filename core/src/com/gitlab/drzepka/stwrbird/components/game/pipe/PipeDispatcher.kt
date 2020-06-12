package com.gitlab.drzepka.stwrbird.components.game.pipe

import com.badlogic.gdx.Gdx
import com.gitlab.drzepka.stwrbird.components.game.DIFFICULTY_LEVELS
import com.gitlab.drzepka.stwrbird.components.game.Difficulty
import com.gitlab.drzepka.stwrbird.config.Pipes
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/**
 * Zarządza rurami i poziomem trudności
 */
class PipeDispatcher(queueSize: Int, pipeY: Float, pipeHeight: Float) {

    private var nextPipeNo = 0
    private var nextDifficultyNo = 0
    private var nextDifficultyPipeNo = 0

    private val queues = HashMap<KClass<out BasePipeColumn>, PipeColumnQueue>()
    private var currentDifficulty: Difficulty<out BasePipeColumn>? = null

    private var currentQueue: PipeColumnQueue? = null
    private var previousPipe: BasePipeColumn? = null

    init {
        DIFFICULTY_LEVELS
                .map { it.type }
                .distinct()
                .forEach {
                    val list = (0 until queueSize).map { _ -> createPipeColumn(it, pipeY, pipeHeight) }
                    queues[it] = PipeColumnQueue(list)
                }

        if (DIFFICULTY_LEVELS.size > 1)
            nextDifficultyPipeNo = DIFFICULTY_LEVELS[1].fromPipe
    }

    fun getNextPipe(): BasePipeColumn {
        var setDistance = false
        if (nextPipeNo == nextDifficultyPipeNo) {
            // Zwiększenie poziomu trudności
            currentDifficulty = DIFFICULTY_LEVELS[nextDifficultyNo]
            currentQueue = queues[currentDifficulty!!.type]!!

            nextDifficultyNo++
            nextDifficultyPipeNo = if (currentDifficulty!!.toPipe != null) currentDifficulty?.toPipe!! + 1 else Int.MAX_VALUE
            setDistance = true
        }

        currentQueue!!.shift()
        val next = currentQueue!!.first()
        next.reset(previousPipe)
        currentDifficulty?.basePrepareType(next, nextPipeNo - currentDifficulty?.fromPipe!!, nextPipeNo)
        if (setDistance) {
            val interGroupDistance = if (nextDifficultyNo == 1)
                Pipes.INITIAL_PIPE_GROUP_DISTANCE * Gdx.graphics.width else Pipes.PIPE_GROUP_DISTANCE
            next.x = (previousPipe?.x ?: 0f) + interGroupDistance + Pipes.PIPE_WIDTH
        }

        nextPipeNo++
        previousPipe = next
        return next
    }

    fun reset() {
        nextPipeNo = 0
        nextDifficultyNo = 0
        nextDifficultyPipeNo = 0
        previousPipe = null
    }

    private fun <T : BasePipeColumn> createPipeColumn(clazz: KClass<T>, pipeY: Float, pipeHeight: Float): T {
        try {
            return clazz.primaryConstructor!!.call(pipeY, pipeHeight)
        } catch (e: Exception) {
            throw IllegalStateException("Wrong constructor parameters for class ${clazz.simpleName}", e)
        }
    }
}
