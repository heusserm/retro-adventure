package com.xndev.retroadventure.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xndev.retroadventure.app.resources.Res
import com.xndev.retroadventure.app.resources.excelon_logo
import org.jetbrains.compose.resources.painterResource

const val PUBLISHER_NAME = "Excelon Development"

/**
 * The publisher's mark, in the top corner of the game screen.
 *
 * The source is xndev_logo__two_color_big.png with its outer white made
 * transparent. The emblem's white strokes are open to the edge of the circle,
 * so they went transparent too: that is the logo's own knockout design, and it
 * reads correctly because the app's background is always light.
 */
@Composable
fun PublisherLogo(heightDp: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.excelon_logo),
        contentDescription = PUBLISHER_NAME,
        modifier = modifier.height(heightDp.dp),
    )
}
