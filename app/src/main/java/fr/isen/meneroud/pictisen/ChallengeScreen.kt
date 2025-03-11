package fr.isen.meneroud.pictisen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(navController: NavController, challengeTitle: String) {
    var challenge by remember { mutableStateOf<Challenge?>(null) }
    var mediaList by remember { mutableStateOf<List<String>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference.child("challenges").child(challengeTitle)

    // Récupération des infos du défi
    LaunchedEffect(Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                challenge = snapshot.getValue(Challenge::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur lors de la récupération du défi", error.toException())
            }
        })
    }

    // Récupération des médias postés
    LaunchedEffect(Unit) {
        database.child("media").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mediaUrls = mutableListOf<String>()
                for (mediaSnapshot in snapshot.children) {
                    val mediaUrl = mediaSnapshot.getValue(String::class.java)
                    if (mediaUrl != null) {
                        mediaUrls.add(mediaUrl)
                    }
                }
                mediaList = mediaUrls
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur lors de la récupération des médias", error.toException())
            }
        })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(challengeTitle) }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            challenge?.let {
                Text(text = it.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
            }
            LazyColumn {
                items(mediaList) { mediaUrl ->
                    Image(
                        painter = rememberImagePainter(mediaUrl),
                        contentDescription = "Média du défi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
