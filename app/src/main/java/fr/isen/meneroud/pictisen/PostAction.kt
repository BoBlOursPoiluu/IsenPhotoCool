package fr.isen.meneroud.pictisen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun PostActions(initialLikes: Int, nbrComment: Int, postId: String, userId: String) {
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(initialLikes) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = {
                liked = !liked
                likeCount = if (liked) likeCount + 1 else likeCount - 1
                toggleLike(postId, userId)
            }
        ) {
            Icon(
                imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                contentDescription = "Like",
                tint = if (liked) Color.Red else Color.Gray
            )
        }
        Text(text = likeCount.toString())
    }
}

private fun toggleLike(postId: String, userId: String) {
    val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(postId)
    postRef.child("likes").child(userId).get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            postRef.child("likes").child(userId).removeValue()
        } else {
            postRef.child("likes").child(userId).setValue(true)
        }
    }
}