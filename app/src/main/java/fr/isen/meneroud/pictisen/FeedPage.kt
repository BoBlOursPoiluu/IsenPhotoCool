package fr.isen.meneroud.pictisen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.android.exoplayer2.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

val supabase = createSupabaseClient(
    supabaseUrl = "https://xhznjmokqslhhhjmwpnb.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhoem5qbW9rcXNsaGhoam13cG5iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE3MDAyOTEsImV4cCI6MjA1NzI3NjI5MX0.0EsQgSp3WHzY5TURXe9otmCMo7oxT-UAbejiA5gHSFE"
) {
    install(Postgrest)
    //install other modules
}

data class Post(
    val userId: String = "",
    val challengeId: String = "",
    val content: String = "",
    val likes: Map<String, Boolean> = mapOf(),
    val comments: Map<String, String> = mapOf(),
    val timestamp: Long = 0L,
    val videoUrl: String= ""
)

// Define Tab Items
data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class FeedPage : ComponentActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        setContent {
            FeedPageContent()
        }

        // Ajouter un post de test
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FeedPageContent() {
        val posts = remember { mutableStateListOf<Post>() }

        LaunchedEffect(Unit) {
            getPostsFromFirebase(posts)
        }

        val navController = rememberNavController()

        // Créer les éléments de l'onglet ici
        val homeTab = TabBarItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        )
        val eventTab = TabBarItem(
            title = "Challenge",
            selectedIcon = Icons.Filled.DateRange,
            unselectedIcon = Icons.Outlined.DateRange
        )
        val accountTab = TabBarItem(
            title = "Profile",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle
        )

        val tabBarItems = listOf(homeTab, eventTab, accountTab)

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Déf'ISEN") }
                )
            },
            bottomBar = { TabView(tabBarItems, navController) }
        ) { paddingValues ->
            NavHost(
                navController,
                startDestination = homeTab.title,
                Modifier.padding(paddingValues)
            ) {
                composable(homeTab.title) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(posts) { post ->
                            PostCard(post)
                        }
                    }
                }
                composable(eventTab.title) { /* Ajouter la vue de Challenge ici */ }
                composable(accountTab.title) { /* Ajouter la vue de Profil ici */ }
            }
        }
    }


    @Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    Icon(
                        imageVector = if (selectedTabIndex == index) tabBarItem.selectedIcon else tabBarItem.unselectedIcon,
                        contentDescription = tabBarItem.title
                    )
                },
                label = { Text(tabBarItem.title) }
            )
        }
    }
}


    // Fonction pour récupérer les posts depuis Firebase
    private fun getPostsFromFirebase(posts: MutableList<Post>) {
        val postsRef = database.child("posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPosts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        newPosts.add(post)
                    }
                }
                posts.clear()
                posts.addAll(newPosts) // Met à jour la liste de manière réactive
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erreur de lecture des posts", error.toException())
            }
        })
    }

    @Composable
    fun PostCard(post: Post) {
        // Utilisation de fillMaxWidth pour que la carte occupe toute la largeur de l'écran
        Card(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()) {

            // Utilisation de fillMaxWidth pour que la colonne occupe toute la largeur de la carte
            Column(modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()) {

                // Afficher les informations du post
                Text(text = "Défi: ${post.challengeId}",
                    fontSize = 32.sp)
                Text(text = "Utilisateur: ${post.userId}")
                Text(text = "Contenu: ${post.content}")
                val videoUrl = post.videoUrl

                // Afficher la vidéo si l'URL est présente
                if (videoUrl.isNotEmpty()) {
                    VideoPlayer(url = videoUrl)
                }

                PostActions(post.likes.size, post.comments.size)
            }
        }
    }


    @Composable
    fun VideoPlayer(url: String) {
        val currentContext = LocalContext.current // Renommer la variable context ici

        // Création du lecteur ExoPlayer
        val exoPlayer = remember {
            ExoPlayer.Builder(currentContext)
                .build()
                .apply {
                    val mediaItem = MediaItem.fromUri(Uri.parse(url))
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                    volume = 1f
                }
        }

        // Utilisation d'AndroidView pour afficher StyledPlayerView
        AndroidView(
            factory = { context ->
                // Création de StyledPlayerView
                StyledPlayerView(context).apply {
                    player = exoPlayer // Assigner exoPlayer au StyledPlayerView
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)  // Ratio d'aspect pour la vidéo
        )
    }


    // Fonction pour ajouter un post de test dans Firebase
    private fun addTestPostToSupabase(videoFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val videoName = "${UUID.randomUUID()}.mp4" // Générer un nom unique
                val bucketName = "video" // Nom du bucket dans Supabase Storage

                // 1️⃣ Upload de la vidéo sur Supabase Storage
                val storage = supabase.storage
                storage.from(bucketName).upload(videoName, videoFile.readBytes())

                // 2️⃣ Récupérer l'URL publique de la vidéo
                val videoUrl = storage.from(bucketName).publicUrl(videoName)

                // 3️⃣ Ajouter le post dans Firebase avec l'URL de la vidéo
                val postId = database.child("posts").push().key ?: return@launch
                val testPost = Post(
                    userId = "user_1",
                    challengeId = "challenge_1",
                    content = "Voici un post avec une vidéo stockée sur Supabase.",
                    likes = mapOf("user_2" to true),
                    comments = mapOf("user_3" to "Super défi !"),
                    timestamp = System.currentTimeMillis(),
                    videoUrl = videoUrl
                )

                database.child("posts").child(postId).setValue(testPost)
                    .addOnSuccessListener {
                        Log.d("Supabase", "Post ajouté avec succès avec vidéo sur Supabase.")
                    }
                    .addOnFailureListener {
                        Log.e("Supabase", "Erreur lors de l'ajout du post", it)
                    }

            } catch (e: Exception) {
                Log.e("Supabase", "Erreur lors de l'upload de la vidéo", e)
            }
        }
    }

    @Composable
    fun PostActions(nbrLike: Int, nbrComment: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite, // Icône de cœur
                contentDescription = "Like",
                tint = Color.Red
            )
            Text(text = nbrLike.toString())
            Icon(
                imageVector = Icons.Default.Menu, // Icône de bulle de discussion
                contentDescription = "Comment",
                tint = Color.Gray
            )
            Text(text = nbrComment.toString())
        }
    }


}
