package fr.isen.meneroud.pictisen

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth

@Composable

fun PostCard(post: Post) {

    val background = Color(0xFF363333) // Fond de la carte

    val textColor = Color(0xFFB0B0B0)  // Gris clair pour le texte

    val titleColor = Color.White        // Blanc pour le titre

    Card(

        modifier = Modifier

            .padding(8.dp)

            .fillMaxWidth(),

        colors = CardDefaults.cardColors(containerColor = background) // Appliquer la couleur de fond à la Card

    ) {

        Column(

            modifier = Modifier

                .padding(8.dp)

                .fillMaxWidth()

        ) {

            Text(text = "Défi : ${post.challengeId}", fontSize = 32.sp, color = titleColor)

            Text(text = "Utilisateur: ${post.userId}", color = textColor)

            Spacer(modifier = Modifier.height(8.dp))

            //Text(text = "Contenu: ${post.content}", color = textColor)

            if (post.videoUrl.isNotEmpty()) {

                VideoPlayer(url = post.videoUrl)

            }

            // Actions avec le système de like et commentaires

            PostActions(post.likes.size, post.postId, FirebaseAuth.getInstance().currentUser?.uid ?: "")

        }

    }

}

