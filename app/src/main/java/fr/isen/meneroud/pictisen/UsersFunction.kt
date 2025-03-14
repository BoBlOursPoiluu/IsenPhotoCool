package fr.isen.meneroud.pictisen

import com.google.firebase.database.getValue
import fr.isen.meneroud.pictisen.data.User
import kotlinx.coroutines.tasks.await

class UsersFunction {
    private val db = FirebaseService.database // 🔥 Utilisation de FirebaseService

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            val snapshot = db.child("users").child(username).get().await()
            val isAvailable = !snapshot.exists()
            println("✅ Vérification du username `$username`: Disponible = $isAvailable")
            isAvailable
        } catch (e: Exception) {
            println("❌ Erreur lors de la vérification du username `$username`: ${e.message}")
            false
        }
    }

    suspend fun addUser(user: User): Boolean {
        return try {
            if (isUsernameAvailable(user.username)) {
                db.child("users").child(user.username).setValue(user).await()
                setCurrentUser(user.username, user.code) // 🔥 Enregistrer l'utilisateur en session
                println("✅ Utilisateur `${user.username}` inscrit et session créée.")
                true
            } else {
                println("⚠️ Le username `${user.username}` est déjà pris.")
                false
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de l'inscription de `${user.username}`: ${e.message}")
            false
        }
    }

    suspend fun getUser(username: String, code: String): User? {
        return try {
            val snapshot = db.child("users").child(username).get().await()
            val user = snapshot.getValue(User::class.java)

            if (user != null && user.code == code) {
                println("✅ Utilisateur `$username` trouvé.")
                user
            } else {
                println("⚠️ Identifiants incorrects pour `$username`.")
                null
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de la récupération de `$username` : ${e.message}")
            null
        }
    }

    suspend fun setCurrentUser(username: String, code: String) {
        try {
            if (username.isBlank()) {
                db.child("currentSession").removeValue().await() // 🔥 Supprime la session
            } else {
                val sessionData = mapOf("username" to username, "code" to code)
                db.child("currentSession").setValue(sessionData).await()
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de la mise à jour de la session : ${e.message}")
        }
    }

    suspend fun getCurrentUser(): Pair<String, String>? {
        return try {
            val snapshot = db.child("currentSession").get().await()
            val sessionData = snapshot.getValue<Map<String, String>>()


            sessionData?.let {
                val username = it["username"]
                val code = it["code"]
                if (username != null && code != null) {
                    return Pair(username, code)
                }
            }
            println("⚠️ Aucune session active trouvée dans Firebase.")
            null
        } catch (e: Exception) {
            println("❌ Erreur lors de la récupération de la session : ${e.message}")
            null
        }
    }
}
