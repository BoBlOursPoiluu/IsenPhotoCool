package fr.isen.meneroud.pictisen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserScreen(userViewModel: UserViewModel = viewModel(), userId: String){
    // Utilisation de currentUser depuis le ViewModel pour récupérer les données de l'utilisateur
    val user by userViewModel.currentUser

    LaunchedEffect(userId) {
        userViewModel.fetchUser(userId) // Appel pour récupérer l'utilisateur
    }

    // On affiche un indicateur de chargement tant que les données de l'utilisateur ne sont pas récupérées
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        user?.let {
            // Affichage des informations de l'utilisateur
            Text("Nom d'utilisateur : ${it.username}")
            Text("Email : ${it.email}")
            Text("Prénom : ${it.firstName}")
            Text("Nom : ${it.lastName}")
        } ?: CircularProgressIndicator() // Chargement en cours
    }
}
