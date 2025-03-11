package fr.isen.meneroud.pictisen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserScreen(userViewModel: UserViewModel = viewModel(), userId: String) {
    val user by userViewModel.currentUser
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var newUsername by remember { mutableStateOf(username) }
    var newEmail by remember { mutableStateOf(email) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userViewModel.fetchUser(userId)
        println("Données récupérées : ${userViewModel.currentUser.value}")
    }

    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }

    if (showToast) {
        // Afficher le Toast une fois que les informations ont été modifiées
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Informations modifiées", Toast.LENGTH_SHORT).show()
            showToast = false // Réinitialiser le Toast après l'avoir affiché
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Réglages du profil", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nom d'utilisateur : ${user?.username ?: "Chargement..."}")
        Text("Email : ${user?.email ?: "Chargement..."}")

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour éditer le profil
        Button(
            onClick = {
                newUsername = username
                newEmail = email
                showDialog = true
            }
        ) {
            Text("Modifier le profil")
        }

        // Pop-up pour modifier les informations
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Modifier le profil") },
                text = {
                    Column {
                        // Champs de saisie pour le nom d'utilisateur et l'email
                        TextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("Nom d'utilisateur") }
                        )
                        TextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Email") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Mise à jour dans Firebase
                            userViewModel.updateUserProfile(newUsername, newEmail)

                            // Mettre à jour les variables locales avec les nouvelles valeurs
                            username = newUsername
                            email = newEmail

                            // Fermer la boîte de dialogue
                            showDialog = false

                            // Afficher le message de confirmation
                            showToast = true
                        }
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
