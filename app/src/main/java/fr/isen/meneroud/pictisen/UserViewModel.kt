package fr.isen.meneroud.pictisen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    var currentUser = mutableStateOf<User?>(null)
        private set

    fun fetchUser(userId: String) {
        Log.d("UserViewModel", "📡 Récupération des données pour UID : $userId")

        database.child("users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    currentUser.value = snapshot.getValue(User::class.java)
                    Log.d("UserViewModel", "✅ Utilisateur récupéré : ${currentUser.value}")
                } else {
                    Log.e("UserViewModel", "⚠️ Aucune donnée trouvée pour cet utilisateur !")
                }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "❌ Erreur Firebase : ${it.message}")
            }
    }

    fun updateUserProfile(username: String, email: String, password: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("UserViewModel", "⚠️ Aucun utilisateur connecté !")
            return
        }

        val userId = user.uid
        val userRef = database.child("users").child(userId)

        val updates = mutableMapOf<String, Any>()
        updates["username"] = username
        updates["email"] = email

        Log.d("UserViewModel", "📝 Mise à jour des données Firebase : $updates")

        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("UserViewModel", "✅ Profil mis à jour avec succès !")
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "❌ Erreur de mise à jour : ${it.message}")
            }

        // Si l'email change, il faut aussi le mettre à jour dans Firebase Auth
        if (email != user.email) {
            user.updateEmail(email)
                .addOnSuccessListener {
                    Log.d("UserViewModel", "✅ Email mis à jour dans Firebase Auth !")
                }
                .addOnFailureListener {
                    Log.e("UserViewModel", "❌ Erreur de mise à jour de l'email : ${it.message}")
                }
        }
    }
}
