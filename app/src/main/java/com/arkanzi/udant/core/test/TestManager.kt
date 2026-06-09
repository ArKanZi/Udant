package com.arkanzi.udant.core.test

import javax.inject.Inject

class TestManager @Inject constructor() {

    fun message(): String {
        return "Hilt Working"
    }
}