package fr.isen.meneroud.pictisen


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.IgnoreExtraProperties

data class User(
    val email: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val code: String = "",
    val profileImageBase64: String = ""
)

object FirebaseService {
    private val db = FirebaseDatabase.getInstance()

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            println("🔍 [DEBUG] Vérification de disponibilité du username: $username")
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
                db.getReference("usernames").child(user.username).setValue(user.username).await()
                setCurrentUser(user.username, user.email, user.code) // 🔥 Enregistrer l'utilisateur en session
                println("✅ Utilisateur `${user.username}` inscrit et session créée.")
                true
            } else {
                println("⚠️ Le username `${user.username}` est déjà pris.")
                false
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de l'inscription de `${user.username}`: ${e.message}")
            db.getReference("usernames").child(user.username).removeValue().await()
            false
        }
    }

    // Vérifier si un utilisateur existe
    suspend fun getUser(email: String, code: String): User? {
        return try {
            println("🔍 [DEBUG] Recherche de user avec email: $email")

            val snapshot = db.getReference("users").get().await() // 🔹 Récupère tous les utilisateurs
            for (child in snapshot.children) {
                val user = child.getValue(User::class.java)
                println("🔍 [DEBUG] Utilisateur trouvé : ${user?.email}") // 🔹 Log des emails trouvés

                if (user != null && user.email == email && user.code == code) {
                    println("✅ Utilisateur trouvé avec l'email `$email`.")
                    return user
                }
            }

            println("❌ [ERREUR] Aucun utilisateur trouvé pour email: $email")
            null
        } catch (e: Exception) {
            println("❌ Erreur lors de la récupération de l'utilisateur avec email `$email`: ${e.message}")
            null
        }
        /*return try {
            println("🔍 [DEBUG] Recherche de user avec username: $email")

            val snapshot = db.getReference("users").get().await()
            if (!snapshot.exists()) {
                println("❌ [ERREUR] Aucun utilisateur trouvé sous users/$email")
                return null
            }

            val user = snapshot.getValue(User::class.java)
            if (user != null && user.code == code) {
                println("✅ Utilisateur `$email` trouvé.")
                user
            } else {
                println("⚠️ Identifiants incorrects pour `$email`.")
                return null
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de la récupération de `$email` : ${e.message}")
            null
        }*/
    }

    suspend fun setCurrentUser(username: String, email: String, code: String) {
        if (username.isBlank()|| email.isBlank()) {
            db.getReference("currentSession").removeValue().await() // 🔥 Supprime la session
            println("⚠️ [DEBUG] Session supprimée car username ou email vide.")
        } else {
            val sessionData = mapOf("username" to username, "email" to email,"code" to code)
            db.getReference("currentSession").setValue(sessionData).await()
            println("✅ [DEBUG] Session enregistrée : $sessionData")
        }
    }

    // 🔥 Récupérer le `username` de l'utilisateur connecté
    suspend fun getCurrentUser(): Triple<String, String, String>? {
        return try {
            val snapshot = db.getReference("currentSession").get().await()
            val typeIndicator = object : GenericTypeIndicator<Map<String, String>>() {}
            val sessionData: Map<String, String>? = snapshot.getValue(typeIndicator)

            if (sessionData != null) {
                val username = sessionData["username"] as? String
                val email = sessionData["email"] as? String
                val code = sessionData["code"] as? String

                println("🔍 [DEBUG] Session récupérée : username=$username, email=$email, code=$code")

                if (username != null && email != null && code != null) {
                    return Triple(username, email, code)
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


