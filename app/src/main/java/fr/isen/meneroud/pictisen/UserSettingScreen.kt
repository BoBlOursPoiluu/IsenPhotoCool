package fr.isen.meneroud.pictisen

import android.net.Uri
import android.util.Log
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

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
    var email by remember { mutableStateOf("Chargement...") }
    var profileImageUrl by remember { mutableStateOf("") }
    var showImageDialog by remember { mutableStateOf(false) } // ✅ Variable pour afficher la galerie
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        Log.d("UserScreen", "📡 Chargement des données pour UID : $userId")
        userViewModel.fetchUser(userId)
    }


    LaunchedEffect(user) {
        user?.let {
            username = it.username
            email = it.email
            profileImageUrl = it.profileImageUrl
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Réglages du profil", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 📸 Affichage de la photo de profil actuelle
        Image(
            painter = rememberAsyncImagePainter(profileImageUrl.ifEmpty { "https://via.placeholder.com/150" }),
            contentDescription = "Photo de profil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔘 Bouton pour afficher la galerie dans une boîte de dialogue
        Button(onClick = { showImageDialog = true }) {
            Text("Modifier la photo de profil")
        }

        Spacer(modifier = Modifier.height(16.dp))


        UserProfileCard(user)

        if (showImageDialog) {
            AlertDialog(
                onDismissRequest = { showImageDialog = false },
                title = { Text("Choisir une nouvelle photo de profil") },
                text = {
                    ImageGallery(userViewModel) { selectedImageUrl ->
                        profileImageUrl = selectedImageUrl // 🔹 Mise à jour locale
                        userViewModel.updateUserProfile(userId, username, email, selectedImageUrl) // 🔹 Mise à jour Firebase
                        showImageDialog = false // 🔹 Fermer la boîte de dialogue après sélection
                    }
                },
                confirmButton = {
                    Button(onClick = { showImageDialog = false }) {
                        Text("Sauvegarder")
                    }
                }
            )
        }

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
                },confirmButton = {
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


// ✅ Affiche les images de Firebase dans un `AlertDialog` et permet de choisir une image
@Composable
fun ImageGallery(userViewModel: UserViewModel, onImageSelected: (String) -> Unit) {
    var imageList by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        userViewModel.getProfileImagesFromFirebase { images ->
            imageList = images
        }
    }

    LazyColumn {
        items(imageList) { imageData ->
            val isBase64 = imageData.startsWith("data:image")
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onImageSelected(imageData) // ✅ Sélectionner une image et fermer la boîte de dialogue
                    }
            ) {
                if (isBase64) {
                    userViewModel.decodeBase64ToBitmap(imageData)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Photo de profil",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageData),
                        contentDescription = "Photo de profil",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
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