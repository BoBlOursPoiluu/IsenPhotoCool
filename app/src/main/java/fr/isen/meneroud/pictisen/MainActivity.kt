package fr.isen.meneroud.pictisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Appel à UserScreen sans avoir à passer un userId
            UserScreen(userId = "Mila-casolari")
        }
    }
}
