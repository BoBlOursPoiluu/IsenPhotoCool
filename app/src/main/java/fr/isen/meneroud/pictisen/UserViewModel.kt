package fr.isen.meneroud.pictisen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import fr.isen.meneroud.pictisen.data.User

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    var currentUser = mutableStateOf<User?>(null)
        private set

    fun fetchUser(userId: String) {
        Log.d("UserViewModel", "📡 Récupération des données pour UID : $userId")

        database.child("users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    currentUser.value = snapshot.getValue(User::class.java)
                    Log.d("UserViewModel", " Utilisateur récupéré : ${currentUser.value}")
                } else {
                    Log.e("UserViewModel", "️ Aucune donnée trouvée pour cet utilisateur !")
                }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", " Erreur Firebase : ${it.message}")
            }
    }

    fun updateUserProfile(userId: String, username: String, email: String, newImageUrl: String) {
        val user = auth.currentUser ?: return

        val updates = mutableMapOf<String, Any>(
            "username" to username,
            "email" to email,
            "profileImageUrl" to newImageUrl
        )

        database.child("users").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                Log.d("UserViewModel", " Profil mis à jour avec succès !")
            }
            .addOnFailureListener {
                Log.e("UserViewModel", " Erreur de mise à jour : ${it.message}")
            }

        if (email != user.email) {
            user.updateEmail(email)
                .addOnSuccessListener {
                    Log.d("UserViewModel", " Email mis à jour dans Firebase Auth !")
                }
                .addOnFailureListener {
                    Log.e("UserViewModel", " Erreur de mise à jour de l'email : ${it.message}")
                }
        }
    }

    fun getProfileImagesFromFirebase(onImagesFetched: (List<String>) -> Unit) {
        val imagesRef =
            database.child("photo_profil")

        imagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageList = mutableListOf<String>()
                for (imageSnapshot in snapshot.children) {
                    val imageUrl = imageSnapshot.getValue(String::class.java)
                    if (imageUrl != null) {
                        imageList.add(
                            imageUrl.replace("url(", "").replace(")", "")
                        )
                    }
                }
                onImagesFetched(imageList) // 📌 On retourne la liste des photos de profil
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    "FirebaseError",
                    " Erreur de lecture des photos de profil",
                    error.toException()
                )
            }
        })
    }

    fun updateProfileImage(userId: String, newImageUrl: String) {
        val userRef = database.child("users").child(userId)

        userRef.child("profileImageUrl").setValue(newImageUrl)
            .addOnSuccessListener {
                Log.d("UserViewModel", "✅ Image de profil mise à jour avec succès")
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "❌ Erreur lors de la mise à jour de l'image : ${exception.message}")
            }
    }

// ✅ Fonction pour convertir une image Base64 en `Bitmap`
fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val pureBase64Encoded = base64String.substringAfter("base64,")
        val decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("Base64Decode", " Erreur de décodage Base64 : ${e.message}")
        null
    }
}
}