package fr.isen.meneroud.pictisen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun UserScreen(userViewModel: UserViewModel = viewModel(), userId: String) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser


    if (userId == null) {
        Log.e("UserScreen", "❌ Aucun utilisateur connecté !")
        return
    }

    val user by userViewModel.currentUser
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        Log.d("UserScreen", "📡 Chargement des données pour UID : $userId")
        userViewModel.fetchUser(userId)
    }

    LaunchedEffect(user) {
        if (user != null) {
            username = user!!.username
            email = user!!.email
            Log.d("UserScreen", "✅ Données utilisateur chargées : $username, $email")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        UserProfileCard(user)

        Button(onClick = { showDialog = true }) {
            Text("Modifier le profil")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Modifier le profil") },
                text = {
                    Column {
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Nom d'utilisateur") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            Log.d("UserScreen", "🟢 [DEBUG] Mise à jour envoyée : Username=$username, Email=$email")

                            userId?.let {
                                FirebaseDatabase.getInstance().getReference("users")
                                    .child(it)
                                    .updateChildren(mapOf("username" to username)) // ✅ Correction ici
                                    .addOnSuccessListener {
                                        Log.d("UserScreen", "✅ Nom d'utilisateur mis à jour dans Realtime Database !")
                                        userViewModel.fetchUser(userId) // 🔄 Rafraîchir les données après mise à jour
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("UserScreen", "❌ Erreur de mise à jour Realtime DB : ${exception.message}")
                                    }
                            } ?: Log.e("UserScreen", "❌ Aucun utilisateur connecté !")

                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotEmpty()
                    ) {
                        Text("Sauvegarder")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun UserProfileCard(user: User?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Informations du profil",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "User", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nom d'utilisateur : ${user?.username ?: "Chargement..."}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Email : ${user?.email ?: "Chargement..."}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
