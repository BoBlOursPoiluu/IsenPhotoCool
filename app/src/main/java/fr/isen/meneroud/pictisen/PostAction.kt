package fr.isen.meneroud.pictisen

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.BasicTextField

import androidx.compose.foundation.text.KeyboardActions

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Favorite

import androidx.compose.material.icons.filled.Menu

import androidx.compose.material.icons.outlined.Favorite

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.unit.dp

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.DatabaseError

import com.google.firebase.database.ValueEventListener

import com.google.firebase.auth.FirebaseAuth

@Composable

fun PostActions(initialLikes: Int, postId: String, userId: String) {

    var liked by remember { mutableStateOf(false) }

    var likeCount by remember { mutableStateOf(initialLikes) }

    var commentText by remember { mutableStateOf("") }

    var comments by remember { mutableStateOf(listOf<String>()) }

    var showAllComments by remember { mutableStateOf(false) }

    var showCommentBox by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }

    LaunchedEffect(userId) {

        loadUsername(userId) { fetchedUsername ->

            username = fetchedUsername

        }

    }

    LaunchedEffect(postId) {

        loadComments(postId) { fetchedComments ->

            comments = fetchedComments

        }

    }

    Column(modifier = Modifier.padding(8.dp)) {

        Row(

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.spacedBy(8.dp)

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

            IconButton(onClick = { showCommentBox = !showCommentBox }) {

                Icon(

                    imageVector = Icons.Filled.Menu,

                    contentDescription = "Comment",

                    tint = Color.Gray

                )

            }

            Text(text = comments.size.toString())

        }

        comments.take(if (showAllComments) comments.size else 2).forEach { comment ->

            Text(text = comment, modifier = Modifier.padding(4.dp), color = Color.White)

        }

        if (comments.size > 2 && !showAllComments) {

            Text(

                text = "Voir plus",

                color = Color.Blue,

                modifier = Modifier.padding(4.dp).clickable { showAllComments = true }

            )

        }

        if (showCommentBox || showAllComments) {

            BasicTextField(

                value = commentText,

                onValueChange = { commentText = it },

                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),

                keyboardActions = KeyboardActions(onSend = {

                    if (commentText.isNotBlank()) {

                        postComment(postId, username, commentText)

                        commentText = ""

                    }

                }),

                modifier = Modifier.fillMaxWidth().padding(8.dp)

            )

            Button(onClick = {

                if (commentText.isNotBlank()) {

                    postComment(postId, username, commentText)

                    commentText = ""

                }

            }) {

                Text("Envoyer")

            }

        }

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

private fun postComment(postId: String, username: String, comment: String) {

    val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(postId).child("comments")

    val newCommentRef = postRef.push()

    newCommentRef.setValue("$username: $comment")

}

private fun loadComments(postId: String, onCommentsLoaded: (List<String>) -> Unit) {

    val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(postId).child("comments")

    postRef.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {

            val commentsList = snapshot.children.mapNotNull { it.getValue(String::class.java) }

            onCommentsLoaded(commentsList)

        }

        override fun onCancelled(error: DatabaseError) {}

    })

}

private fun loadUsername(userId: String, onUsernameLoaded: (String) -> Unit) {

    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("username")

    userRef.get().addOnSuccessListener { snapshot ->

        val username = snapshot.getValue(String::class.java) ?: "Utilisateur inconnu"

        onUsernameLoaded(username)

    }

}

