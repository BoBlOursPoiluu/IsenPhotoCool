package fr.isen.meneroud.pictisen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.meneroud.pictisen.Post
import fr.isen.meneroud.pictisen.FirebaseService
import fr.isen.meneroud.pictisen.PostCard
import fr.isen.meneroud.pictisen.TabView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPageContent(navController: NavController) {
    val posts = remember { mutableStateListOf<Post>() }
    val firebaseService = FirebaseService()

    val newPost = Post(
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
    }


    LaunchedEffect(Unit) {
        firebaseService.getPostsFromFirebase(posts)
    }

    val tabBarItems = listOf(
        TabBarItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        TabBarItem("Challenge", Icons.Filled.DateRange, Icons.Outlined.DateRange),
        TabBarItem("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Déf'ISEN") }) },
        bottomBar = { TabView(tabBarItems, navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("createPost") }) {
                Icon(Icons.Default.Add, contentDescription = "Créer un post")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}
