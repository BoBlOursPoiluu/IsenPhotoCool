package fr.isen.meneroud.pictisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import fr.isen.meneroud.pictisen.ui.theme.PictIsenTheme

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        setContent {
            PictIsenTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "topics") {
                    composable("topics") { TopicScreen(navController) }
                    composable("challenge/{challengeTitle}") { backStackEntry ->
                        val challengeTitle =
                            backStackEntry.arguments?.getString("challengeTitle") ?: ""
                        ChallengeScreen(navController, challengeTitle)
                    }
                }
            }
        }
    }
}