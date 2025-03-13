package fr.isen.meneroud.pictisen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var usersFunction: UsersFunction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Initialiser Firebase Database
        database = FirebaseDatabase.getInstance().reference
        usersFunction = UsersFunction()

        // Tester l'écriture et la lecture
        testFirebaseConnection()


        // Vérifier si un utilisateur est connecté
        checkUserSession()
    }

    private fun checkUserSession() {
        MainScope().launch {
            // Vérifie si un utilisateur est connecté
            val currentUser = usersFunction.getCurrentUser()

            if (currentUser != null) {
                // Si l'utilisateur est connecté, lancer la page FeedPage
                Log.d("MainActivity", "Utilisateur connecté : ${currentUser.first}")
                val feedIntent = Intent(this@MainActivity, FeedPage::class.java)
                startActivity(feedIntent)
                finish()  // Fermer MainActivity pour éviter de revenir dessus en arrière
            } else {
                // Sinon, lancer la page de LoginScreen
                Log.d("MainActivity", "Aucun utilisateur connecté")
                setContent {
                    val navController = rememberNavController()
                    LoginScreen(navController, this@MainActivity)
                }
            }
        }
    }

    private fun testFirebaseConnection() {
        val testRef = database.child("testConnection")

        // 🔹 1️⃣ Écrire une valeur dans Firebase
        testRef.setValue("Connexion réussie !")
            .addOnSuccessListener {
                Log.d("FirebaseTest", "✅ Donnée écrite avec succès dans la BDD")

                // 🔹 2️⃣ Lire la valeur après écriture
                testRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue(String::class.java)
                        Log.d("FirebaseTest", "📥 Valeur lue : $value")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseTest", "❌ Erreur de lecture", error.toException())
                    }
                })
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "❌ Erreur d'écriture", e)
            }
    }
}
