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
import fr.isen.meneroud.pictisen.ui.theme.DarkBackground
import fr.isen.meneroud.pictisen.ui.theme.DarkSurface
import fr.isen.meneroud.pictisen.ui.theme.VioletPrimary
import fr.isen.meneroud.pictisen.ui.theme.GrayText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(navController: NavController? = null) {
    val database = Firebase.database.reference.child("challenges") // 🔥 Référence à Firebase
    var challenges by remember { mutableStateOf<List<Challenge>>(emptyList()) }
    var selectedChallenge by remember { mutableStateOf<String?>(null) }
    var showAddChallengeDialog by remember { mutableStateOf(false) }


    // 🔥 Écoute en temps réel des défis depuis Firebase
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
            TopAppBar(
                title = {
                    Text(
                        "Défis disponibles",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 22.sp,
                            shadow = Shadow(
                                color = VioletPrimary.copy(alpha = 0.8f),
                                blurRadius = 8f
                            )
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddChallengeDialog = true }, // 🔥 Afficher la boîte de dialogue pour ajouter un défi
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
                        navController?.navigate("challenge/${challenge.title}")
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
    val title: String = "",
    val description: String = ""
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
                    textStyle = TextStyle(color = Color.White), // ✅ Correction : texte saisi en blanc
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = VioletPrimary,
                        unfocusedLabelColor = GrayText,
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = GrayText,
                        cursorColor = VioletPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color.White) },
                    textStyle = TextStyle(color = Color.White), // ✅ Correction : texte saisi en blanc
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = VioletPrimary,
                        unfocusedLabelColor = GrayText,
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = GrayText,
                        cursorColor = VioletPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val challengeId = database.push().key
                        if (challengeId != null) {
                            val newChallenge = Challenge(title, description)
                            database.child(challengeId).setValue(newChallenge)
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
                Text("Annuler")
            }
        },
        containerColor = DarkSurface
    )
}

@Preview
@Composable
fun PreviewTopicScreen() {
    TopicScreen()
}
