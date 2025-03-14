package fr.isen.meneroud.pictisen

import android.util.Log

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import androidx.navigation.NavController

import com.google.firebase.database.*

import fr.isen.meneroud.pictisen.ui.theme.DarkBackground

import fr.isen.meneroud.pictisen.ui.theme.VioletPrimary

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun ChallengeScreen(navController: NavController, challengeTitle: String) {

    var challenge by remember { mutableStateOf<Challenge?>(null) }

    val database = FirebaseDatabase.getInstance().reference.child("challenges")

    val primaryColor = Color(0xFF8A2BE2) // Violet


    // 🔥 Récupération des infos du défi

    LaunchedEffect(Unit) {

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (challengeSnapshot in snapshot.children) {

                    val challengeData = challengeSnapshot.getValue(Challenge::class.java)

                    if (challengeData != null && challengeData.title == challengeTitle) {

                        challenge = challengeData

                        Log.d("Firebase", "Défi trouvé : ${challengeData.title}")

                        break

                    }

                }

                if (challenge == null) {

                    Log.e("Firebase", "Défi non trouvé dans la base de données")

                }

            }

            override fun onCancelled(error: DatabaseError) {

                Log.e("Firebase", "Erreur lors de la récupération du défi", error.toException())

            }

        })

    }

    Scaffold(

        topBar = {

            Column {

                TopBar()

                TopAppBar(

                    title = {

                        Text(

                            text = challenge?.title ?: "Défi",

                            color = Color.White

                        )

                    },

                    navigationIcon = {

                        IconButton(onClick = { navController.popBackStack() }) {

                            Icon(

                                imageVector = Icons.Default.ArrowBack,

                                contentDescription = "Retour",

                                tint = Color.White

                            )

                        }

                    },

                    colors = TopAppBarDefaults.topAppBarColors(containerColor = VioletPrimary) // ✅ Barre violette

                )

            }

        },

        bottomBar = { BottomNavigationBar(navController) },

        floatingActionButton = @androidx.compose.runtime.Composable {

            FloatingActionButton(onClick = {

                navController.navigate("createPost")

            },

                containerColor = primaryColor

            ) {

                Icon(Icons.Default.Add, contentDescription = "Créer un post")

            }

        }

        //containerColor = DarkBackground

    ) { padding ->

        Column(

            modifier = Modifier

                .padding(padding)

                .fillMaxSize()

                .background(DarkBackground)

        ) {

            if (challenge != null) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(

                    text = challenge!!.description,

                    style = MaterialTheme.typography.bodyLarge,

                    color = Color.White.copy(alpha = 0.8f),

                    modifier = Modifier.padding(horizontal = 16.dp)

                )

            } else {

                Text(

                    text = "Chargement du défi...",

                    color = Color.White,

                    modifier = Modifier.padding(16.dp)

                )

            }

        }

    }

}

