package fr.isen.meneroud.pictisen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import fr.isen.meneroud.pictisen.ui.theme.DarkBackground
import fr.isen.meneroud.pictisen.ui.theme.DarkSurface
import fr.isen.meneroud.pictisen.ui.theme.VioletPrimary
import fr.isen.meneroud.pictisen.ui.theme.GrayText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.TextFieldDefaults
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(navController: NavController) {
    val database = Firebase.database.reference.child("challenges")
    var challenges by remember { mutableStateOf<List<Challenge>>(emptyList()) }
    var selectedChallenge by remember { mutableStateOf<String?>(null) }
    var showAddChallengeDialog by remember { mutableStateOf(false) }

    // Écoute en temps réel des défis depuis Firebase
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val challengeList = mutableListOf<Challenge>()
                for (challengeSnapshot in snapshot.children) {
                    val challenge = challengeSnapshot.getValue(Challenge::class.java)
                    if (challenge != null) {
                        challengeList.add(challenge)
                    }
                }
                challenges = challengeList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }

    Scaffold(
        topBar = {
            Column {
                TopBar() // ✅ Ajout de la TopBar Générale
                TopAppBar(
                    title = { Text("Défis disponibles", color = Color.White) }, // ✅ Ajout du titre spécifique
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface) // ✅ Ajout du fond sombre
                )
            }
        },

        bottomBar = {
            navController?.let { BottomNavigationBar(it) } // ✅ Vérifie que navController n'est pas null
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddChallengeDialog = true },
                containerColor = VioletPrimary
            ) {
                Text("+", color = Color.White, fontSize = 24.sp)
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
        ) {
            items(challenges) { challenge ->
                ChallengeItem(
                    challenge = challenge,
                    isSelected = challenge.title == selectedChallenge,
                    onClick = {
                        selectedChallenge = challenge.title
                        navController.navigate("defi/${challenge.title}")
                    }
                )
            }
        }
    }

    if (showAddChallengeDialog) {
        AddChallengeDialog(onDismiss = { showAddChallengeDialog = false })
    }
}




@Composable
fun ChallengeItem(challenge: Challenge, isSelected: Boolean, onClick: () -> Unit) {
    val shadowColor = VioletPrimary.copy(alpha = 0.9f) // Ombre violette plus marquée
    val textShadow = Shadow(
        color = VioletPrimary.copy(alpha = 0.8f),
        blurRadius = 10f
    )

    val backgroundBrush = if (isSelected) {
        Brush.verticalGradient(listOf(VioletPrimary.copy(alpha = 0.8f), DarkSurface))
    } else {
        Brush.verticalGradient(listOf(DarkSurface, DarkSurface))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(
                elevation = 24.dp, // Augmentation de l'ombre
                shape = RoundedCornerShape(12.dp),
                ambientColor = shadowColor, // Ombre violette
                spotColor = shadowColor // Ombre plus prononcée
            )
            .clip(RoundedCornerShape(12.dp))
            .background(brush = backgroundBrush)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = challenge.title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = textShadow // ✅ Utilisation correcte de l'ombre sur le texte
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = challenge.description,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = GrayText,
                    shadow = textShadow // ✅ Ombre aussi sur la description
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Modèle de données pour un défi
data class Challenge(
    val challengeId: String = "", // ✅ Ajout du challengeId
    val title: String = "",
    val description: String = "",
    val userId: String = ""
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChallengeDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val database = Firebase.database.reference.child("challenges")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Ajouter un défi", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre du défi", color = Color.White) },
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = VioletPrimary,
                        unfocusedLabelColor = GrayText,
                        focusedContainerColor = DarkSurface, // Optionnel : fond du champ
                        unfocusedContainerColor = DarkSurface,
                        cursorColor = VioletPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color.White) },
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface, // ✅ Fond du champ en mode focus
                        unfocusedContainerColor = DarkSurface, // ✅ Fond du champ non focus
                        cursorColor = VioletPrimary,
                        focusedIndicatorColor = VioletPrimary, // ✅ Bordure en focus
                        unfocusedIndicatorColor = GrayText // ✅ Bordure non focus
                    )
                )

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val challengeId = database.push().key // 🔥 Génère un ID unique
                        if (challengeId != null) {
                            val newChallenge = Challenge(
                                challengeId = challengeId, // 🔥 Ajoute l'ID généré
                                title = title,
                                description = description,
                                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // Associe l'utilisateur
                            )
                            database.child(challengeId).setValue(newChallenge) // 🔥 Stocke avec l'ID unique
                        }
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
            ) {
                Text("Ajouter", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Annuler", color = Color.White)
            }
        },
        containerColor = DarkSurface
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopicScreen() {
    val navController = rememberNavController() // ✅ Crée un NavController mocké pour le Preview
    TopicScreen(navController = navController)
}
