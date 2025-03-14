package fr.isen.meneroud.pictisen

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class VideoPicker(private val onVideoSelected: (Uri) -> Unit) {

    fun pickVideo(activityResultLauncher: ActivityResultLauncher<String>) {
        activityResultLauncher.launch("video/*")
    }
}

@Composable
fun rememberVideoPicker(onVideoSelected: (Uri) -> Unit): VideoPicker {
    val context = LocalContext.current
    return remember { VideoPicker(onVideoSelected) }
}
