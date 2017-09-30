package com.gitlab.drzepka.stwrbird.components

import com.badlogic.gdx.scenes.scene2d.Actor

abstract class BaseActor : Actor(), ActorInterface {
    override fun remove(): Boolean {
        dispose()
        return super.remove()
    }
}