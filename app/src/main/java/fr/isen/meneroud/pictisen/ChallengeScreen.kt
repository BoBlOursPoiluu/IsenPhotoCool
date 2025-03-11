package fr.isen.meneroud.pictisen

import android.net.Uri
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.firebase.database.*
import fr.isen.meneroud.pictisen.ui.theme.DarkBackground
import fr.isen.meneroud.pictisen.ui.theme.DarkSurface
import fr.isen.meneroud.pictisen.ui.theme.VioletPrimary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(navController: NavController, challengeTitle: String) {
    var challenge by remember { mutableStateOf<Challenge?>(null) }
    var mediaList by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference.child("challenges")

    // 🔥 Récupération des infos du défi
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (challengeSnapshot in snapshot.children) {
                    val challengeData = challengeSnapshot.getValue(Challenge::class.java)
                    if (challengeData != null && challengeData.title == challengeTitle) {
                        challenge = challengeData
                        Log.d("Firebase", "Défi trouvé : ${challengeData.title}")
                        break
                    }
                }
                if (challenge == null) {
                    Log.e("Firebase", "Défi non trouvé dans la base de données")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur lors de la récupération du défi", error.toException())
            }
        })
    }

    // 🔥 Récupération des médias (images et vidéos)
    LaunchedEffect(Unit) {
        database.child("media").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mediaItems = mutableListOf<MediaItem>()
                for (mediaSnapshot in snapshot.children) {
                    val url = mediaSnapshot.child("url").getValue(String::class.java)
                    val type = mediaSnapshot.child("type").getValue(String::class.java) // "image" ou "video"
                    if (url != null && type != null) {
                        mediaItems.add(MediaItem(url, type))
                    }
                }
                mediaList = mediaItems
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur lors de la récupération des médias", error.toException())
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = challenge?.title ?: "Défi",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        listOf(VioletPrimary.copy(alpha = 0.9f), VioletPrimary.copy(alpha = 0.7f))
                    )
                ) // ✅ Dégradé ajouté
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            if (challenge != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = challenge!!.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Text(
                    text = "Chargement du défi...",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Affichage des médias (images & vidéos)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = mediaList) { media -> // ✅ Correction : Ajout de `items =`
                    if (media.type == "image") {
                        ImageItem(media.url)
                    } else if (media.type == "video") {
                        VideoItem(media.url)
                    }
                }
            }

        }
    }
}

// ✅ Composant pour afficher une image
@Composable
fun ImageItem(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(DarkSurface)
    ) {
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = "Image du défi",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
        )
    }
}

// ✅ Composant pour afficher une vidéo
@Composable
fun VideoItem(videoUrl: String) {
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                setVideoURI(Uri.parse(videoUrl))
                setMediaController(MediaController(context).apply { setAnchorView(this@apply) })
                setOnPreparedListener { start() }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(DarkSurface)
    )
}

// ✅ Modèle pour les médias
data class MediaItem(
    val url: String = "",
    val type: String = "" // "image" ou "video"
)
