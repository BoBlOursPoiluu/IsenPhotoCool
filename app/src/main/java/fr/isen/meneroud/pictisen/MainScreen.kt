/*package fr.isen.meneroud.pictisen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController)
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Feed.route) { HomeScreen() }
        composable(Screen.Defi.route) { DefiScreen() }
        composable(Screen.Profil.route) { ProfileScreen(navController) }
        //composable("user_settings") { UserScreen(userId = "currentUserId") }
        composable("user_settings") {
            val usernameState = remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val session = FirebaseService.getCurrentUser()
                session?.let { (username, _) ->
                    usernameState.value = username // 🔹 Met à jour l'état avec le username
                }
            }

            usernameState.value?.let { username ->
                UserScreen(userId = username) // 🔹 Passe le username à UserScreen une fois chargé
            } ?: CircularProgressIndicator() // 🔹 Affiche un loader en attendant
        }
    }
}

// Écrans temporaires
@Composable
fun HomeScreen() {
    ScreenContent(title = "Accueil")
}

@Composable
fun DefiScreen() {
    ScreenContent(title = "Défis")
}

@Composable
fun ProfilScreen() {
    ScreenContent(title = "Profil")
}

@Composable
fun ScreenContent(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White, style = MaterialTheme.typography.headlineLarge)
    }
}
*/