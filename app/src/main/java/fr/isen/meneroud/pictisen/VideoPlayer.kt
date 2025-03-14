package fr.isen.meneroud.pictisen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun VideoPlayer(url: String) {
    val currentContext = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(currentContext).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            volume = 1f
        }
    }

    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply { player = exoPlayer }
        },
        modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f)
    )
}