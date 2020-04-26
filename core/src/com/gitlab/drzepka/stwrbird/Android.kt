package com.gitlab.drzepka.stwrbird

interface Android {

    /**
     * Wyświetla toast.
     * [text] - teskt do wyświetlenia
     * [long] - czy tekst ma być wyświetlany długo czy krótko
     */
    fun toast(text: String, long: Boolean)
}