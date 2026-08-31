package com.xndev.retroadventure.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Adventure 430") {
        App()
    }
}
