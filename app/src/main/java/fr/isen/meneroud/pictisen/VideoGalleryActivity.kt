package fr.isen.meneroud.pictisen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.meneroud.pictisen.ui.theme.PictIsenTheme

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
        Text(text = videoUrl)
    }
}
