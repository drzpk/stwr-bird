package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.scoreboard.ScoreElementLayout
import com.gitlab.drzepka.stwrbird.loadBitmapFont
import com.gitlab.drzepka.stwrbird.model.Medal
import com.gitlab.drzepka.stwrbird.model.Score
import java.util.*

class ScoreboardScreen : BaseScreen() {

    private val mainContainer = Table()
    private val backgroundImage = Image(Commons.atlas.findRegion("background_day"))

    private lateinit var title: Label
    private lateinit var board: VerticalGroup

    override fun create() {
        Gdx.input.inputProcessor = stage

        backgroundImage.setSize(Gdx.app.graphics.width.toFloat(), Gdx.app.graphics.height.toFloat())

        val titleStyle = Label.LabelStyle()
        titleStyle.fontColor = Color.WHITE
        titleStyle.font = loadBitmapFont("fonts/FlappyBirdy.ttf", 150)
        title = Label("Highest scores", titleStyle)
        mainContainer.add(title)
        mainContainer.row()

        board = VerticalGroup()
        getScoreActors().forEach { board.addActor(it) }

        val scrollPane = ScrollPane(board)
        mainContainer.add(scrollPane).fill()

        stage.addActor(backgroundImage)
        stage.addActor(mainContainer)
        mainContainer.setFillParent(true)
    }

    override fun render(delta: Float) {
        stage.act()
        stage.draw()
    }

    override fun dispose() {
    }

    private fun getScoreActors(): List<Actor> = getHighestScores().map { ScoreElementLayout(it) }

    private fun getHighestScores(): List<Score> {
        // todo: Score database

        return (10 downTo 0).map {
            val medal = try {
                Medal.values()[10 - it]
            } catch (ignored: Exception) {
                null
            }
            Score("test player", it * 10, medal, Date())
        }.toList()
    }
}