package fr.isen.meneroud.pictisen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import fr.isen.meneroud.pictisen.data.User

@Composable
fun AppNavigation(context: Context, isUserLoggedIn: Boolean) {
    val navController = rememberNavController() // Création du NavController ici
    val startDestination = if (isUserLoggedIn) "feed" else "login"

    NavHost(navController, startDestination = "signup") {
        composable("signup") { SignUpScreen(navController, context) }
        composable("login") { LoginScreen(navController, context) }
        composable("profil") { ProfileScreen(navController) }
        composable("topics") { TopicScreen(navController) }
        composable("user_settings") { backStackEntry ->
            val scope = rememberCoroutineScope()
            var session by remember { mutableStateOf<Triple<String, String, String>?>(null) }

            LaunchedEffect(Unit) {
                session = FirebaseService.getCurrentUser()
            }
            session?.let { (username, _) ->
                UserScreen(userId = username) // Assure-toi que c'est bien `UserScreen`
            }
        }
        //composable("main") { MainScreen() }
        //composable("user_settings") { UserScreen(userId = "currentUserId") }
        composable("feed") { FeedPageContent(navController) }
        composable("createPost") {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            CreationPostScreen(navController)
        }
        composable("defi/{challengeTitle}") { backStackEntry ->
            val challengeTitle = backStackEntry.arguments?.getString("challengeTitle") ?: ""
            ChallengeScreen(navController, challengeTitle)
        }
    }
}