package fr.isen.meneroud.pictisen

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import fr.isen.meneroud.pictisen.data.User

object FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // Ajouter un utilisateur dans Firebase Firestore
    suspend fun addUser(user: User): Boolean {
        return try {
            usersCollection.document(user.email).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Vérifier si un utilisateur existe dans Firebase Firestore
    suspend fun getUser(email: String, code: String): User? {
        return try {
            val document = usersCollection.document(email).get().await()
            val user = document.toObject(User::class.java)
            if (user != null && user.code == code) user else null
        } catch (e: Exception) {
            null
        }
    }
}
