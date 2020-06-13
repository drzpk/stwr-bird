package com.gitlab.drzepka.stwrbird.config

import com.gitlab.drzepka.stwrbird.Commons

@Suppress("MayBeConstant")
object Pipes {

    /** Szerokość rury */
    val PIPE_WIDTH = Commons.dpi(79)

    /** Standardowa odległość między rurami */
    val PIPE_DISTANCE = Commons.dpi(140)

    /** Wysokość standardowej przestrzeni między rurami */
    val GAP_SIZE = Commons.dpi(135)

    /** Minimalna wysokość widocznej częsci rury */
    val MIN_PIPE_HEIGHT = Commons.dpi(50)

    /** Prędkość poruszania się rury w pionie (dotyczy niektórych rodzajów rur; px/s) */
    val VERTICAL_MOVEMENT_SPEED = Commons.dpi(16)

    /** Początkowa odległość od lewej krawędzi ekranu do pierwszsej grupy rur (w długościach ekranu) */
    val INITIAL_PIPE_GROUP_DISTANCE = 1.4f

    /** Odległość między kolejnymi grupami rur (w pikselach) */
    val PIPE_GROUP_DISTANCE = PIPE_DISTANCE * 4
}