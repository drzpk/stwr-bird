package com.gitlab.drzepka.stwrbird.components.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.inRange
import com.gitlab.drzepka.stwrbird.screen.GameScreen
import com.gitlab.drzepka.stwrbird.trueWidth

class PlayGameOverlay(private val gameScreen: GameScreen) : Table() {

    var onPlayerTypeSelected: ((type: PlayerActor.PlayerType) -> Unit)? = null

    private val background = Image(Commons.atlas.findRegion("title_get_ready"))
    private val tapToPlay = Image(Commons.atlas.findRegion("tap"))
    private val birdPlayer = Image(Commons.atlas.findRegion("bird/bird_blue", 0))
    private val catPlayer = Image(Commons.atlas.findRegion("cat/cat", 0))

    private var player = birdPlayer
    private var playerTypeClickPolygon = Polygon()

    init {
        setFillParent(true)
        touchable = Touchable.enabled

        add(background).trueWidth(Commons.dpi(260)).padBottom(Commons.dpi(40))
        row()
        add(tapToPlay).trueWidth(Commons.dpi(140))

        prepareImage(birdPlayer, PlayerActor.PlayerType.BIRD)
        prepareImage(catPlayer, PlayerActor.PlayerType.CAT)
        setPlayerType(PlayerActor.PlayerType.BIRD)

        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                gameScreen.setMode(GameScreen.Mode.GAME)
            }
        })

        debug = Commons.DEBUG
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {

        if (x inRange Pair(player.x, player.x + player.width * player.scaleX)
                && y inRange Pair(player.y, player.y + player.height * player.scaleY)) {
            return player
        }

        return super.hit(x, y, touchable)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        player.draw(batch, parentAlpha)
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        super.drawDebug(shapes)
        shapes?.setColor(1f, 0f, 0f, 1f)
        shapes?.polygon(playerTypeClickPolygon.transformedVertices)
    }

    private fun prepareImage(image: Image, playerType: PlayerActor.PlayerType) {
        val scale = playerType.textureWidth / image.width
        image.setScale(scale)

        val x = Gdx.graphics.width - image.width * scale - Commons.dpi(20)
        val y = Gdx.graphics.height - image.height * scale - Commons.dpi(20)
        image.setPosition(x, y)

        image.clearListeners()
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val newType = if (playerType == PlayerActor.PlayerType.BIRD)
                    PlayerActor.PlayerType.CAT else PlayerActor.PlayerType.BIRD
                setPlayerType(newType)
                onPlayerTypeSelected?.invoke(newType)
            }
        })
    }

    private fun setPlayerType(playerType: PlayerActor.PlayerType) {
        player = if (playerType == PlayerActor.PlayerType.BIRD) birdPlayer else catPlayer
        playerTypeClickPolygon.vertices = floatArrayOf(
                player.x, player.y,
                player.x, player.y + player.height * player.scaleY,
                player.x + player.width * player.scaleX, player.y + player.height * player.scaleY,
                player.x + player.width * player.scaleX, player.y
        )
    }
}
