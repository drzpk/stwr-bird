package com.gitlab.drzepka.stwrbird

import com.badlogic.gdx.Gdx

@Suppress("LibGDXStaticResource")
/**
 * Klasa odpowiadająca za efekty dźwiękowe
 */
object Audio {
    val fall = Gdx.audio.newSound(Gdx.files.internal("sfx/fall.ogg"))!!
    val hit = Gdx.audio.newSound(Gdx.files.internal("sfx/hit.ogg"))!!
    val point = Gdx.audio.newSound(Gdx.files.internal("sfx/point.ogg"))!!
    val swoosh = Gdx.audio.newSound(Gdx.files.internal("sfx/swoosh.ogg"))!!
    val wing = Gdx.audio.newSound(Gdx.files.internal("sfx/wing.ogg"))!!

    fun dispose() {
        fall.dispose()
        hit.dispose()
        point.dispose()
        swoosh.dispose()
        wing.dispose()
    }
}