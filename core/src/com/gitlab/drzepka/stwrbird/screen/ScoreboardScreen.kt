package com.gitlab.drzepka.stwrbird.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.components.scoreboard.ScoreElementLayout
import com.gitlab.drzepka.stwrbird.data.ScoreData
import com.gitlab.drzepka.stwrbird.loadBitmapFont

class ScoreboardScreen : BaseScreen() {

    private val mainContainer = Table()
    private val backgroundImage = Image(Commons.atlas.findRegion("background_day"))

    private lateinit var title: Label
    private lateinit var board: VerticalGroup

    override fun create() {
        super.create()

        backgroundImage.setSize(Gdx.app.graphics.width.toFloat(), Gdx.app.graphics.height.toFloat())

        val titleStyle = Label.LabelStyle()
        titleStyle.fontColor = Color.WHITE
        titleStyle.font = loadBitmapFont("fonts/FlappyBirdy.ttf", 150)
        title = Label("Highest scores", titleStyle)
        mainContainer.add(title)
        mainContainer.row().expandY()

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

    override fun dispose() = Unit

    private fun getScoreActors(): List<Actor> =
            ScoreData.getHighestScores().mapIndexed {i, it -> ScoreElementLayout(i + 1, it) }
}