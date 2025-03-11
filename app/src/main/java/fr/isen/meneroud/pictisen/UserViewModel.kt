package fr.isen.meneroud.pictisen

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.meneroud.pictisen.UserScreen

data class User(
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val code: String = "",
    //val profileImageUri: String? = null
)

class UserViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private val auth = FirebaseAuth.getInstance()

    // MutableState pour gérer l'état local de l'utilisateur
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser = _currentUser

    // Fonction pour récupérer les données de l'utilisateur par son ID
    fun fetchUser(userId: String) {
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    println("Données récupérées : ${user.username}, ${user.email}")
                    _currentUser.value = user
                } else {
                    println("Aucune donnée trouvée pour cet utilisateur.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erreur lors de la récupération des données utilisateur : ${error.message}")
            }
        })
    }


    fun updateUserProfile(
    newUsername: String?,
    newEmail: String?
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mutableMapOf<String, Any>()

        newUsername?.let { updates["username"] = it }
        newEmail?.let { updates["email"] = it }


        if (updates.isNotEmpty()) {
            println("Mise à jour des informations : $updates")
            database.child(userId).updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Profil mis à jour avec succès")
                    _currentUser.value?.let {
                        _currentUser.value = it.copy(
                            username = newUsername ?: it.username,
                            email = newEmail ?: it.email

                        )
                        println("Mise à jour des informations : $updates")
                    }
                } else {
                    println("Erreur de mise à jour du profil : ${task.exception?.message}")
                }
            }
        }
    }


    fun logout() {
        try {
            auth.signOut()
            _currentUser.value = null
            println("Utilisateur déconnecté avec succès.")
        } catch (e: Exception) {
            println("Erreur lors de la déconnexion : ${e.message}")
        }
    }
}
