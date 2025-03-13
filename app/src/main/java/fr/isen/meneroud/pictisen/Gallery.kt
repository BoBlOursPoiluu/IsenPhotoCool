package fr.isen.meneroud.pictisen

import com.google.firebase.database.FirebaseDatabase

// Modèle de données pour une vidéo
data class Gallery(
    val videoId: String = "",
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis() // Ajout du timestamp
)

// Référence à la base de données Firebase
private val database = FirebaseDatabase.getInstance().reference.child("gallery")

fun addVideo(url: String) {
    if (url.isBlank()) {
        println("❌ Erreur : L'URL de la vidéo est vide")
        return
    }

    val videoId = database.push().key ?: return // Génère un ID unique
    val videoToAdd = Gallery(videoId = videoId, url = url)

    database.child(videoId).setValue(videoToAdd).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            println("✅ Vidéo ajoutée avec succès à Firebase !")
        } else {
            println("❌ Erreur lors de l'ajout de la vidéo : ${task.exception?.message}")
        }
    }
}
