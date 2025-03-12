package fr.isen.meneroud.pictisen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserScreen(userViewModel: UserViewModel = viewModel(), userId: String) {
    val user by userViewModel.currentUser
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 🔥 Vérifier que `fetchUser()` est bien appelé
    LaunchedEffect(userId) {
        println("🛠 [DEBUG] fetchUser() est appelé avec userId=$userId")
        userViewModel.fetchUser(userId)
    }

    // 🔄 Mise à jour de l’UI après modification de `user`
    LaunchedEffect(user) {
        println("🔄 [DEBUG] UI mise à jour avec : Username=${user?.username}, Email=${user?.email}")
        username = user?.username ?: ""
        email = user?.email ?: ""
    }

    // 🔄 Affichage d'un écran de chargement si `user` est null
    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileCard(user!!)

        Spacer(modifier = Modifier.height(16.dp))

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
                        Spacer(modifier = Modifier.height(8.dp))

                        if (email != user?.email) {
                            TextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Mot de passe actuel (nécessaire pour changer l'email)") }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        println("🟢 [DEBUG] Envoi de la mise à jour : Username=$username, Email=$email")
                        userViewModel.updateUserProfile(username, email, currentPassword)
                        showDialog = false
                    }) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userViewModel.logout()
                Toast.makeText(context, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("Se déconnecter")
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