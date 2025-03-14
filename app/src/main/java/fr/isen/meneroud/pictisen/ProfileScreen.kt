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
import com.google.firebase.database.*
import fr.isen.meneroud.pictisen.base64ToBitmap
import fr.isen.meneroud.pictisen.FirebaseService
import fr.isen.meneroud.pictisen.data.User
import fr.isen.meneroud.pictisen.ui.theme.DarkBackground
import fr.isen.meneroud.pictisen.ui.theme.DarkSurface
import fr.isen.meneroud.pictisen.ui.theme.VioletPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val primaryColor = Color(0xFF8A2BE2) // Violet
    val backgroundColor = Color(0xFF121212) // Noir
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userProfile = remember { mutableStateOf<User?>(null) }
    val username = remember { mutableStateOf<String?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val database = FirebaseDatabase.getInstance().reference.child("challenges")
    var userChallenges by remember { mutableStateOf<List<Challenge>>(emptyList()) } // 🔥 Liste des défis postés par l'utilisateur

    // Récupérer les infos de l'utilisateur connecté et ses défis
    LaunchedEffect(Unit) {
        val session = FirebaseService.getCurrentUser()
        session?.let { (currentUsername, _) ->
            username.value = currentUsername
            val user = FirebaseService.getUserProfile(currentUsername)
            userProfile.value = user
            user?.profileImageUrl?.let { base64 ->
                bitmap.value = base64ToBitmap(base64)
            }

            // 🔥 Récupérer les défis postés par cet utilisateur
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val challengesList = mutableListOf<Challenge>()
                    for (challengeSnapshot in snapshot.children) {
                        val challenge = challengeSnapshot.getValue(Challenge::class.java)
                        if (challenge != null && challenge.userId == currentUsername) { // 🔥 Filtrer les défis de cet utilisateur
                            challengesList.add(challenge)
                        }
                    }
                    userChallenges = challengesList // 🔥 Met à jour la liste des défis
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Firebase error: ${error.message}")
                }
            })
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopBar() // ✅ Ajout de la TopBar Générale
                TopAppBar(
                    title = { Text("Mon Profil", color = Color.White) }, // ✅ Ajout du titre spécifique
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface) // ✅ Ajout du fond sombre
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController) }, // ✅ Correction ici
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Photo de profil
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

            Spacer(modifier = Modifier.height(16.dp))

            // Affichage du nom d'utilisateur
            Text(
                text = username.value ?: "Chargement...",
                fontSize = 24.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour modifier le profil
            Button(
                onClick = { navController.navigate("user_settings") },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Modifier mon profil", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔥 Section "Mes Défis"
            Text("Mes Défis", fontSize = 22.sp, color = primaryColor, modifier = Modifier.padding(bottom = 8.dp))

            if (userChallenges.isEmpty()) {
                Text("Aucun défi posté.", color = Color.White, fontSize = 16.sp)
            } else {
                userChallenges.forEach { challenge ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = challenge.title, fontSize = 20.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = challenge.description, fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
