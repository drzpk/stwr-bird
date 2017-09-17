package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.gitlab.drzepka.stwrbird.Commons
import com.gitlab.drzepka.stwrbird.trueWidth

class PlayGameOverlay : Table() {

    private val background = Image(Commons.atlas.findRegion("title_get_ready"))
    private val tapToPlay = Image(Commons.atlas.findRegion("tap"))


    init {
        setFillParent(true)
        add(background).trueWidth(Commons.dpi(260)).padBottom(Commons.dpi(40))
        row()
        add(tapToPlay).trueWidth(Commons.dpi(140))
        debug = Commons.DEBUG
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}
