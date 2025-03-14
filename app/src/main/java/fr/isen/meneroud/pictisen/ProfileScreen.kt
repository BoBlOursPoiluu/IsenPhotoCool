package fr.isen.meneroud.pictisen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fr.isen.meneroud.pictisen.base64ToBitmap

import fr.isen.meneroud.pictisen.FirebaseService
import fr.isen.meneroud.pictisen.data.User
//import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val primaryColor = Color(0xFF8A2BE2) // Violet
    val backgroundColor = Color(0xFF121212) // Noir
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userProfile = remember { mutableStateOf<User?>(null) }
    val username = remember { mutableStateOf<String?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }


    // Récupérer les infos de l'utilisateur connecté
    /*LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            getUserProfile(it.uid) { user ->
                userProfile.value = user
                user?.profileImageBase64?.let { base64 ->
                    bitmap.value = base64ToBitmap(base64)
                }
            }
        }
    }*/

    LaunchedEffect(Unit) {
        val session = FirebaseService.getCurrentUser()
        session?.let { (currentUsername, _) ->
            username.value = currentUsername
            val user = FirebaseService.getUserProfile(currentUsername)
            userProfile.value = user
            user?.profileImageUrl?.let { base64 ->
                bitmap.value = base64ToBitmap(base64)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Photo de profil
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    bitmap.value?.let {
                        AsyncImage(model = it, contentDescription = "Photo de profil")
                    } ?: Text("Aucune photo", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Nom d'utilisateur
                    Text(
                        text = username.value ?: "Chargement...",
                        fontSize = 24.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bouton Modifier le profil
                    TextButton(
                        onClick = { navController.navigate("user_settings") }
                    ) {
                        Text("Modifier mon profil", color = primaryColor)
                    }
                }
            }
        }
    }
}
            /*Spacer(modifier = Modifier.height(16.dp))

            // Nom d'utilisateur
            Text(
                text = username.value ?: "Chargement...",
                fontSize = 24.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("user_settings") },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modifier le profil", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Placeholder pour les posts
            Text("Posts de l'utilisateur", fontSize = 18.sp, color = primaryColor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Ici apparaîtront les posts de l'utilisateur", color = Color.White)
            }



        }
    }
}*/
