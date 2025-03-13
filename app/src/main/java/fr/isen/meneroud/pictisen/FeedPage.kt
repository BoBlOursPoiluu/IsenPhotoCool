package fr.isen.meneroud.pictisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import fr.isen.meneroud.pictisen.FirebaseService
import fr.isen.meneroud.pictisen.FeedPageContent

class FeedPage : ComponentActivity() {
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            val navController = rememberNavController()
            FeedPageContent(navController)
        }
    }
}
