package fr.isen.meneroud.pictisen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(context: Context, isUserLoggedIn: Boolean) {
    val navController = rememberNavController() // Création du NavController ici
    val startDestination = if (isUserLoggedIn) "feed" else "login"

    NavHost(navController, startDestination = "signup") {
        composable("signup") { SignUpScreen(navController, context) }
        composable("login") { LoginScreen(navController, context) }
        composable("main") { MainScreen() }
        composable("user_settings") { UserScreen(userId = "currentUserId") }
        composable("feed") { FeedPageContent(navController) }
        composable("createPost") {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            CreatePostScreen(context, userId)
        }
        composable("defi/{challengeTitle}") { backStackEntry ->
            val challengeTitle = backStackEntry.arguments?.getString("challengeTitle") ?: ""
            ChallengeScreen(navController, challengeTitle)
        }
    }
}