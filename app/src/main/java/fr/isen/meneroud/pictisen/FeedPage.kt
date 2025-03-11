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
import com.google.android.exoplayer2.ui.StyledPlayerView

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
        addTestPostToFirebase()
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
                Text(text = "Likes: ${post.likes.size}")
                Text(text = "Commentaires: ${post.comments.size}")
                val videoUrl = post.videoUrl

                // Afficher la vidéo si l'URL est présente
                if (videoUrl.isNotEmpty()) {
                    VideoPlayer(url = videoUrl)
                }
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
    private fun addTestPostToFirebase() {
        val postId = database.child("posts").push().key ?: return

        // Créez un Post avec une URL de vidéo
        val testPost = Post(
            userId = "user_1",
            challengeId = "challenge_1",
            content = "Voici un post de test avec une vidéo.",
            likes = mapOf("user_2" to true),
            comments = mapOf("user_3" to "Super défi !"),
            timestamp = System.currentTimeMillis(),
            videoUrl = "https://www.youtube.com/watch?v=XDNyJbqmLCE&ab_channel=Cartoonvie"  // Lien vers la vidéo
        )

        val postRef = database.child("posts").child(postId)
        postRef.setValue(testPost)
            .addOnSuccessListener {
                Log.d("Firebase", "Post de test ajouté avec succès.")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Erreur lors de l'ajout du post de test", it)
            }
    }

}
