package fr.isen.meneroud.pictisen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPageContent(navController: NavController) {
    val backgroundColor = Color(0xFF121212) // Noir
    val primaryColor = Color(0xFF8A2BE2) // Violet
    val textColor = Color.White
    val posts = remember { mutableStateListOf<Post>() }
    val firebaseService = FirebaseService

    /*val newPost = Post(
        postId = "unique_post_id",
        userId = "user123",
        challengeId = "challenge456",
        content = "Ceci est un nouveau post",
        likes = mapOf("user1" to true, "user2" to false),
        comments = mapOf("user1" to "Super post !"),
        timestamp = System.currentTimeMillis(),
        videoUrl = "https://imgur.com/a/Yy95UZn"
    )

    firebaseService.addPost(newPost) { isSuccess ->
        if (isSuccess) {
            Log.d("Firebase", "Post ajouté avec succès !")
        } else {
            Log.d("Firebase", "Échec de l'ajout du post.")
        }
    }*/


    LaunchedEffect(Unit) {
        firebaseService.getPostsFromFirebase(posts)
    }


    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Déf'ISEN", color = textColor) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor)) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("createPost")
            },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Créer un post")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}
