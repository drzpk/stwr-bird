package com.gitlab.drzepka.stwrbird.components.game

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

}
