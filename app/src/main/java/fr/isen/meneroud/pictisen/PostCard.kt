package fr.isen.meneroud.pictisen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Défi: ${post.challengeId}", fontSize = 32.sp)
            Text(text = "Utilisateur: ${post.userId}")
            Text(text = "Contenu: ${post.content}")

            if (post.videoUrl.isNotEmpty()) {
                VideoPlayer(url = post.videoUrl)
            }

            // Actions avec le système de like et commentaires
            PostActions(post.likes.size, post.comments.size, post.postId,post.userId)
        }
    }
}