package fr.isen.meneroud.pictisen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.meneroud.pictisen.FirebaseService
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(navController: NavController) {
    val backgroundColor = Color(0xFF121212) // Noir
    val primaryColor = Color(0xFF8A2BE2)   // Violet
    var user by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val session = FirebaseService.getCurrentUser()
            //val username = FirebaseService.getCurrentUser()
            if (session != null) {
                val (username, code) = session
                val userData = FirebaseService.getUser(username, code)
                if (userData != null) {
                    user = userData // Récupère l'utilisateur sans vérifier le code
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Nom de l'application
            Text(
                text = "Déf'ISEN",
                color = primaryColor,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (user != null) {
                Text(
                    text = "Bienvenue, ${user!!.firstName} ${user!!.lastName} !",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            } else {
                CircularProgressIndicator(color = primaryColor) // Affiche un loader en attendant la récupération des données
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 Bouton de déconnexion
            Button(
                onClick = {
                    scope.launch {
                        FirebaseService.setCurrentUser("","") // Efface la session active
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se déconnecter", color = Color.White)
            }
        }
    }
}