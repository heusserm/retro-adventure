package com.xndev.retroadventure.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidEnterBackgroundNotification

@Composable
actual fun OnBackground(onBackground: () -> Unit) {
    val current = rememberUpdatedState(onBackground)
    DisposableEffect(Unit) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ -> current.value() }
        onDispose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }
}
