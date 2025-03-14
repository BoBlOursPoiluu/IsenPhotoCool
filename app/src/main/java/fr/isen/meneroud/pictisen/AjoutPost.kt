package fr.isen.meneroud.pictisen

import android.app.Activity

import android.content.Context

import android.content.Intent

import android.net.Uri

import android.widget.Toast

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.TextFieldValue

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*

import java.util.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun CreationPostScreen(navController: NavController) {

    val context = LocalContext.current

    var description by remember { mutableStateOf(TextFieldValue("")) }

    var selectedChallenge by remember { mutableStateOf("") }

    var challengeList by remember { mutableStateOf(listOf<String>()) }

    var videoUri by remember { mutableStateOf<Uri?>(null) }

    var showDropdown by remember { mutableStateOf(false) }

    // Charger les défis depuis Firebase

    LaunchedEffect(Unit) {

        val database = FirebaseDatabase.getInstance().reference.child("challenges")

        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                challengeList = snapshot.children.mapNotNull { it.child("title").getValue(String::class.java) }

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(context, "Erreur de chargement des défis", Toast.LENGTH_SHORT).show()

            }

        })

    }

    Scaffold(

        topBar = { TopAppBar(title = { Text("Créer un Post") }) }

    ) { padding ->

        Column(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding)

                .background(Color(0xFF2E1A47))

                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text("Créer un Post", fontSize = 24.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Champ de description

            OutlinedTextField(

                value = description,

                onValueChange = { description = it },

                label = { Text("Ajoutez une description...") },

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(8.dp),

                singleLine = false

            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sélection du défi

            Box {

                Button(onClick = { showDropdown = true }) {

                    Text(if (selectedChallenge.isEmpty()) "Sélectionner un défi" else selectedChallenge)

                }

                DropdownMenu(

                    expanded = showDropdown,

                    onDismissRequest = { showDropdown = false }

                ) {

                    challengeList.forEach { challenge ->

                        DropdownMenuItem(

                            text = { Text(challenge) },

                            onClick = {

                                selectedChallenge = challenge

                                showDropdown = false

                            }

                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Ajouter Vidéo (exemple simplifié)

            AddVideoButton(context = context)

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Publier

            Button(

                onClick = {

                    if (description.text.isEmpty()) {

                        Toast.makeText(context, "La description est obligatoire", Toast.LENGTH_SHORT).show()

                        return@Button

                    }

                    if (selectedChallenge.isEmpty()) {

                        Toast.makeText(context, "Sélectionnez un défi", Toast.LENGTH_SHORT).show()

                        return@Button

                    }

                    savePostToFirebase(context, description.text, selectedChallenge, videoUri?.toString() ?: "")

                },

                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)

            ) {

                Text("Publier")

            }

        }

    }

}

// Enregistrer le post sur Firebase

fun savePostToFirebase(context: android.content.Context, description: String, challengeId: String, videoUrl: String) {

    val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("posts")

    val postId = UUID.randomUUID().toString()

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val timestamp = System.currentTimeMillis()

    val post = mapOf(

        "postId" to postId,

        "userId" to userId,

        "challengeId" to challengeId,

        "content" to description,

        "videoUrl" to videoUrl,

        "timestamp" to timestamp

    )

    database.child(postId).setValue(post).addOnSuccessListener {

        Toast.makeText(context, "Post publié avec succès", Toast.LENGTH_SHORT).show()

    }.addOnFailureListener {

        Toast.makeText(context, "Erreur lors de la publication", Toast.LENGTH_SHORT).show()

    }

}

@Composable

fun AddVideoButton(context: Context) {

    Button(onClick = {

        // Lance l'activité VideoGalleryActivity pour choisir une vidéo

        val intent = Intent(context, VideoGalleryActivity::class.java)

        (context as? Activity)?.startActivityForResult(intent, VIDEO_GALLERY_REQUEST_CODE)

    }) {

        Text("Ajouter Vidéo")

    }

}

// Définir un code pour l'activité VideoGalleryActivity

const val VIDEO_GALLERY_REQUEST_CODE = 1001
