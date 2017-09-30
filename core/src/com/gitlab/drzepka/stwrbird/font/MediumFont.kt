package com.gitlab.drzepka.stwrbird.font

import com.gitlab.drzepka.stwrbird.Commons

class MediumFont : BaseFont(Commons.dpi(13), "font_medium", 14) {
    override val spacing = Commons.dpi(2.4f)
}