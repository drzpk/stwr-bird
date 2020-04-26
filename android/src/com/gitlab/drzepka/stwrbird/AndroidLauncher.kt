package com.gitlab.drzepka.stwrbird

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication(), Android {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(StwrBird(this), config)
    }

    override fun toast(text: String, long: Boolean) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, text, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
        }
    }
}
