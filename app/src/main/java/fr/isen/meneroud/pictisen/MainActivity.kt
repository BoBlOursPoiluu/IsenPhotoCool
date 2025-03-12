package fr.isen.meneroud.pictisen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Initialiser Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Tester l'écriture et la lecture
        testFirebaseConnection()

        // Initialiser FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Si un utilisateur est connecté
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Accéder aux données de l'utilisateur
            val userId = currentUser.uid
            Log.d("UserInfo", "Utilisateur connecté : $userId")
        } else {
            Log.d("UserInfo", "Aucun utilisateur connecté")
        }

        //setContentView(Post_Screen(this))
        setContentView(CreationPostScreen(this))
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
