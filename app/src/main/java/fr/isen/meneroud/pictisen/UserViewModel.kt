package fr.isen.meneroud.pictisen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
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
    val code: String = ""
) {
    constructor() : this("", "", "", "", "")
}

class UserViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("users")

    // MutableState pour gérer l'état local de l'utilisateur
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser = _currentUser

    // Fonction pour récupérer les données de l'utilisateur par son ID
    fun fetchUser(userId: String) {
        // Vérifie si l'ID de l'utilisateur n'est pas nul
        if (userId.isNotEmpty()) {
            // Récupère les données de l'utilisateur dans Firebase
            database.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    _currentUser.value = user // Stocke les données dans _currentUser
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Firebase error: ${error.message}")
                }
            })
        }
    }
}
