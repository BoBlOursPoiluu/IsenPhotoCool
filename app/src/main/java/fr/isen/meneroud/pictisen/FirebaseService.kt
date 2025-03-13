package fr.isen.meneroud.pictisen


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await

data class User(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val code: String = "",
    val profileImageBase64: String = ""
)

object FirebaseService {
    private val db = FirebaseDatabase.getInstance()

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            val snapshot = db.getReference("users").child(username).get().await()
            val isAvailable = !snapshot.exists()
            println("✅ Vérification du username `$username`: Disponible = $isAvailable")
            isAvailable
        } catch (e: Exception) {
            println("❌ Erreur lors de la vérification du username `$username`: ${e.message}")
            false
        }
    }


    // Ajouter un utilisateur dans Firebase Realtime Database
    suspend fun addUser(user: User): Boolean {
        return try {
            if (isUsernameAvailable(user.username)) {
                db.getReference("users").child(user.username).setValue(user).await()
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

    // Vérifier si un utilisateur existe
    suspend fun getUser(username: String, code: String): User? {
        return try {
            val snapshot = db.getReference("users").child(username).get().await()
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
        if (username.isBlank()) {
            db.getReference("currentSession").removeValue().await() // 🔥 Supprime la session
        } else {
            val sessionData = mapOf("username" to username, "code" to code)
            db.getReference("currentSession").setValue(sessionData).await()
        }
    }

    // 🔥 Récupérer le `username` de l'utilisateur connecté
    suspend fun getCurrentUser(): Pair<String, String>? {
        return try {
            val snapshot = db.getReference("currentSession").get().await()
            //val sessionData = snapshot.getValue(Map::class.java) as? Map<*, *>
            val typeIndicator = object : GenericTypeIndicator<Map<String, String>>() {} // 🔥 Correction ici
            val sessionData: Map<String, String>? = snapshot.getValue(typeIndicator)

            if (sessionData != null) {
                val username = sessionData["username"] as? String
                val code = sessionData["code"] as? String
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

    suspend fun getUserProfile(username: String): User? {
        return try {
            val snapshot = db.getReference("users").child(username).get().await()
            val user = snapshot.getValue(User::class.java)

            if (user != null) {
                println("✅ Profil utilisateur `$username` récupéré.")
            } else {
                println("⚠️ Utilisateur `$username` introuvable.")
            }
            user
        } catch (e: Exception) {
            println("❌ Erreur lors de la récupération du profil `$username`: ${e.message}")
            null
        }
    }

}


