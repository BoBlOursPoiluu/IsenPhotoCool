package fr.isen.meneroud.pictisen


import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val code: String = ""
)

object FirebaseService {
    private val db = FirebaseDatabase.getInstance().getReference("users")

    // Ajouter un utilisateur dans Firebase Realtime Database
    suspend fun addUser(user: User): Boolean {
        return try {
            db.child(user.email.replace(".", ",")).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Vérifier si un utilisateur existe
    suspend fun getUser(email: String, code: String): User? {
        return try {
            val snapshot = db.child(email.replace(".", ",")).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null && user.code == code) user else null
        } catch (e: Exception) {
            null
        }
    }
}

