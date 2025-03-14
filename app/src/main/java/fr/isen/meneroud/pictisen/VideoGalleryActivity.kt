package fr.isen.meneroud.pictisen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.meneroud.pictisen.ui.theme.PictIsenTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PictIsenTheme {
                VideoGalleryScreen { selectedVideoUrl ->
                    val resultIntent = Intent().apply {
                        putExtra("selectedVideoUrl", selectedVideoUrl)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // Ferme l'activité après la sélection
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoGalleryScreen(onVideoSelected: (String) -> Unit) {
    val context = LocalContext.current
    var videoUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Charger les vidéos depuis Firebase
    LaunchedEffect(Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("gallery")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val urls = snapshot.children.mapNotNull {
                    it.child("videoUrl").getValue(String::class.java)
                }
                videoUrls = urls
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de récupération : ${error.message}")
                Toast.makeText(context, "Erreur de récupération", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Galerie Vidéo") }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (videoUrls.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    Text("Aucune vidéo disponible.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(videoUrls) { videoUrl ->
                        VideoGridItem(videoUrl, onVideoSelected)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoGridItem(videoUrl: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(200.dp) // Une taille de vidéo fixe pour chaque élément
            .clickable { onClick(videoUrl) },
        contentAlignment = Alignment.Center
    ) {
        // Affichage d'un simple texte avec l'URL de la vidéo pour maintenant
        videoPlayer(videoUrl)
    }
}

@Composable
fun videoPlayer(url: String) {
    val currentContext = LocalContext.current

    // Utilisation de ExoPlayer pour obtenir une vignette
    val exoPlayer = remember {
        ExoPlayer.Builder(currentContext).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    // Extraire la vignette vidéo à l'aide de ExoPlayer et afficher en tant qu'aperçu
    var videoThumbnail by remember { mutableStateOf<Bitmap?>(null) }

    // Utiliser un thread pour récupérer la vignette de la vidéo en arrière-plan
    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(currentContext, Uri.parse(url))
                val bitmap = retriever.getFrameAtTime(0)
                videoThumbnail = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }

    // Affichage du preview de la vidéo
    if (videoThumbnail != null) {
        Image(
            bitmap = videoThumbnail!!.asImageBitmap(),
            contentDescription = "Thumbnail preview",
            modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f)
        )
    } else {
        // Affichage d'un message si la vignette est introuvable
        Toast.makeText(currentContext, "Erreur lors de la récupération de l'aperçu", Toast.LENGTH_SHORT).show()
    }

    // Affichage de l'ExoPlayer si la vidéo est prête
    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f)
    )
}