package com.gitlab.drzepka.stwrbird.font

import com.gitlab.drzepka.stwrbird.Commons

class BigFont : BaseFont(Commons.dpi(32), "font_big", 24) {
    override val spacing = Commons.dpi(2.4f)
}