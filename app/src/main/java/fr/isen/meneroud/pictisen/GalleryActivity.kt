package fr.isen.meneroud.pictisen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import fr.isen.meneroud.pictisen.ui.theme.PictIsenTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.launch

// Connexion à Supabase
val supabase = createSupabaseClient(
    supabaseUrl = "https://xhznjmokqslhhhjmwpnb.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhoem5qbW9rcXNsaGhoam13cG5iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE3MDAyOTEsImV4cCI6MjA1NzI3NjI5MX0.0EsQgSp3WHzY5TURXe9otmCMo7oxT-UAbejiA5gHSFE"
){
    install(Postgrest) // Installation du module Postgrest
    install(Storage)   // Installation du module Storage
}

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PictIsenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoGallery(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Fonction pour récupérer les vidéos de Supabase
@Composable
fun VideoGallery(modifier: Modifier = Modifier) {
    val videoUrls = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Vérification de la connexion à Supabase
                val storage = supabase.storage.from("videos") // Connexion au bucket "videos"

                // Liste des fichiers dans le bucket "videos"
                val files = storage.list()

                // Génération des URLs publics pour chaque fichier vidéo
                val baseUrl = "https://xhznjmokqslhhhjmwpnb.supabase.co/storage/v1/object/public/videos/"
                val urls = files.map { file ->
                    "$baseUrl${file.name}" // Construction manuelle de l'URL publique
                }

                // Ajout des URLs dans la liste
                videoUrls.addAll(urls)
            } catch (e: Exception) {
                Log.e("Supabase", "Erreur lors du chargement des vidéos", e)
            }
        }
    }

    // Si la liste des vidéos est vide, on affiche un indicateur de chargement
    if (videoUrls.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Affichage des vidéos dans une grille
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(videoUrls) { videoUrl ->
                VideoThumbnail(videoUrl)
            }
        }
    }
}

// Affichage d'une vignette de vidéo
@Composable
fun VideoThumbnail(videoUrl: String) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Image(
            painter = rememberImagePainter(data = videoUrl),
            contentDescription = "Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    PictIsenTheme {
        VideoGallery()
    }
}
