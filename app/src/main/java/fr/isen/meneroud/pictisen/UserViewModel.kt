package fr.isen.meneroud.pictisen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    fun updateUserProfile(newUsername: String?, newEmail: String?, currentPassword: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser
        val updates = mutableMapOf<String, Any>()

        // Mise à jour du nom d'utilisateur
        newUsername?.let {
            updates["username"] = it
            println("✅ [DEBUG] Nouveau username à enregistrer : $it")
        }

        // Mise à jour de l'email avec re-authentification si nécessaire
        if (newEmail != null && newEmail != currentUser?.email && currentPassword != null) {
            val credential = EmailAuthProvider.getCredential(currentUser?.email!!, currentPassword)

            currentUser.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    println("✅ [DEBUG] Re-authentification réussie !")

                    currentUser.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            updates["email"] = newEmail
                            println("✅ [DEBUG] Email mis à jour avec succès")
                        } else {
                            println("❌ [ERREUR] Échec de la mise à jour de l'email : ${emailTask.exception?.message}")
                        }
                    }
                } else {
                    println("❌ [ERREUR] Re-authentification échouée : ${authTask.exception?.message}")
                }
            }
        }

        // Sauvegarde des changements dans Firebase Database
        if (updates.isNotEmpty()) {
            FirebaseDatabase.getInstance().reference.child("users").child(userId).updateChildren(updates)
                .addOnSuccessListener {
                    println("✅ [DEBUG] Mise à jour réussie : $updates")
                }
                .addOnFailureListener { exception ->
                    println("❌ [ERREUR] Firebase n'a pas pu mettre à jour les valeurs : ${exception.message}")
                }
        }
    }



    fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
            println("Déconnexion réussie")
        } catch (e: Exception) {
            println("Erreur lors de la déconnexion : ${e.message}")
        }
    }
}
