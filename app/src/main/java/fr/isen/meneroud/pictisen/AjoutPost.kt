package fr.isen.meneroud.pictisen

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import fr.isen.meneroud.pictisen.FirebaseService.database
import java.util.UUID

@Composable
fun CreatePostScreen(context: Context, userId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TitleSection()
        val descriptionField = remember { mutableStateOf("") }
        DescriptionField(descriptionField)
        val challengeList = remember { mutableListOf<String>() }
        val selectedChallenge = remember { mutableStateOf("") }
        ChallengeSpinner(challengeList, selectedChallenge)
        VideoPreview()
        UploadButton(context)
        PublishButton(context, userId, descriptionField.value, selectedChallenge.value, challengeList)
    }
}

@Composable
fun TitleSection() {
    Text(
        text = "Créer un Post",
        style = MaterialTheme.typography.titleLarge,
        color = ComposeColor.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DescriptionField(description: MutableState<String>) {
    BasicTextField(
        value = description.value,
        onValueChange = { description.value = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // Handle keyboard action (e.g., hide keyboard)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(ComposeColor.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
    )
}

@Composable
fun ChallengeSpinner(challengeList: MutableList<String>, selectedChallenge: MutableState<String>) {
    val database = FirebaseDatabase.getInstance().reference.child("challenges")

    database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            challengeList.clear()
            for (challengeSnapshot in snapshot.children) {
                val challenge = challengeSnapshot.getValue(Challenge::class.java)
                if (challenge != null) {
                    challengeList.add(challenge.title)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("CreatePostScreen", "Erreur de chargement des défis", error.toException())
        }
    })

    /*DropdownMenu(
        expanded = challengeList.isNotEmpty(),
        onDismissRequest = {  },
    ) {
        challengeList.forEach { challenge ->
            DropdownMenuItem(
                onClick = { selectedChallenge.value = challenge }
            ) {
                Text(text = challenge)
            }
        }
    }*/
}

@Composable
fun VideoPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(ComposeColor.Gray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Aperçu Vidéo", color = ComposeColor.White)
    }
}

@Composable
fun UploadButton(context: Context) {
    Button(
        onClick = {
            // Logic to upload a video
            val videoUrl = "https://www.youtube.com/shorts/u-3mBTHlpCg"
            Toast.makeText(context, "Vidéo ajoutée à la galerie !", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = "Ajouter Vidéo")
    }
}

@Composable
fun PublishButton(context: Context, userId: String, description: String, selectedChallenge: String, challengeList: List<String>) {
    Button(
        onClick = {
            if (description.isEmpty()) {
                Toast.makeText(context, "La description ne peut pas être vide", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (selectedChallenge.isEmpty()) {
                Toast.makeText(context, "Sélectionnez un défi", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val post = Post(
                postId = UUID.randomUUID().toString(),
                userId = userId,
                challengeId = selectedChallenge,
                content = description,
                videoUrl = "https://imgur.com/a/Yy95UZn", // 🔥 Remplacer par l'URL réelle
                timestamp = System.currentTimeMillis()
            )

            addPost(post) { success ->
                if (success) {
                    Toast.makeText(context, "Post publié avec succès", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erreur lors de la publication", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = "Publier")
    }
}

fun addPost(post: Post, onComplete: (Boolean) -> Unit) {
    val postId = post.postId
    val postRef = database.child("posts").child(postId)

    postRef.setValue(post).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("FirebaseService", "Post ajouté avec succès")
            onComplete(true)
        } else {
            Log.e("FirebaseService", "Erreur lors de l'ajout du post", task.exception)
            onComplete(false)
        }
    }
}
