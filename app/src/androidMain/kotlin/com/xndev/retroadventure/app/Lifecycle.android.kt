package com.xndev.retroadventure.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
actual fun OnBackground(onBackground: () -> Unit) {
    val current = rememberUpdatedState(onBackground)
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        // ON_STOP, not ON_PAUSE: pause fires for a dialog over the activity too,
        // and saving on every dialog would be noise. Stop is the point after
        // which the process is a candidate for being killed.
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) current.value()
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
}
