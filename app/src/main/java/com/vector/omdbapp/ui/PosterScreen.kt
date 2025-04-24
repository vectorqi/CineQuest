package com.vector.omdbapp.ui

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vector.omdbapp.R

/**
 * PosterScreen displays a full-size poster with zoom and pan support.
 * Single tap anywhere will navigate back. Double tap toggles zoom.
 */
@Composable
fun PosterScreen(posterUrl: String, navController: NavController) {
    val context = LocalContext.current
    val decodedUrl = Uri.decode(posterUrl)
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()

        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale = (scale * zoom).coerceIn(1f, 5f)
                offsetX += pan.x
                offsetY += pan.y
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    if (scale > 1f) {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    } else {
                        scale = 2f
                    }
                },
                onTap = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(decodedUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.poster_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}
