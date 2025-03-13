package fr.isen.meneroud.pictisen

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.view.Gravity
import java.util.UUID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

fun CreationPostScreen(context: Context): LinearLayout {
    val mainLayout = createMainLayout(context)

    mainLayout.addView(TitleSection(context))

    val descriptionField = DescriptionField(context)
    mainLayout.addView(descriptionField)

    val challengeSpinner = ChallengeSpinner(context)
    mainLayout.addView(challengeSpinner)

    val videoView = VideoView(context)
    mainLayout.addView(VideoPreview(context, videoView))

    mainLayout.addView(UploadButton(context))

    val publishButton = PublishButton(context, descriptionField, challengeSpinner)
    mainLayout.addView(publishButton)

    return mainLayout
}

// Layout principal avec un fond dégradé
fun createMainLayout(context: Context): LinearLayout {
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Color.parseColor("#2E1A47"), Color.parseColor("#120A16"))
    )
    return LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(32, 32, 32, 32)
        background = gradientDrawable
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }
}

// Titre "Créer un Post"
fun TitleSection(context: Context): TextView {
    return TextView(context).apply {
        text = "Créer un Post"
        textSize = 24f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setPadding(0, 20, 0, 20)
    }
}

// Champ de texte pour la description
fun DescriptionField(context: Context): EditText {
    return EditText(context).apply {
        hint = "Ajoutez une description..."
        setHintTextColor(Color.GRAY)
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.TRANSPARENT)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 10, 0, 20) }
    }
}

// Aperçu vidéo (vide pour l’instant)
fun VideoPreview(context: Context, videoView: VideoView): LinearLayout {
    return LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            650
        ).apply { setMargins(0, 20, 0, 20) }
        setBackgroundColor(Color.DKGRAY)
        gravity = Gravity.CENTER
        addView(videoView.apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setVideoURI(null) // Pas encore de vidéo
        })
    }
}

// Bouton Ajout vidéo
fun UploadButton(context: Context): Button {
    return Button(context).apply {
        text = "Ajouter Vidéo"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 10, 0, 20) }

        setOnClickListener {
            val videoUrl = "https://www.youtube.com/shorts/u-3mBTHlpCg"

            // Appel de la fonction d'ajout de vidéo avec l'URL
            if (videoUrl.isNotEmpty()) {
                addVideo(videoUrl)
                Toast.makeText(context, "Vidéo ajoutée à la galerie !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "URL de vidéo invalide", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


// Bouton Publier avec enregistrement Firebase
fun PublishButton(context: Context, descriptionField: EditText, challengeSpinner: Spinner): Button {
    return Button(context).apply {
        text = "Publier"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 10, 0, 20) }

        setOnClickListener {
            val description = descriptionField.text.toString().trim()
            val selectedChallenge = challengeSpinner.selectedItem?.toString() ?: ""

            if (description.isEmpty()) {
                Toast.makeText(context, "La description ne peut pas être vide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedChallenge.isEmpty()) {
                Toast.makeText(context, "Sélectionnez un défi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savePostToFirebase(context, description, selectedChallenge)
        }
    }
}

// Fonction pour enregistrer le post dans Firebase
fun savePostToFirebase(context: Context, description: String, challengeId: String) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("posts")

    val postId = UUID.randomUUID().toString() // ID unique pour le post
    val userId = "user123" // 🔥 Remplace avec l'ID utilisateur réel
    val videoUrl = "https://imgur.com/a/Yy95UZn" // 🔥 Remplace avec la vraie URL de la vidéo
    val timestamp = System.currentTimeMillis()

    val post = Post(
        postId = postId,
        userId = userId,
        challengeId = challengeId,
        content = description,
        videoUrl = videoUrl,
        timestamp = timestamp
    )

    database.child(postId).setValue(post).addOnSuccessListener {
        Toast.makeText(context, "Post publié avec succès", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Erreur lors de la publication", Toast.LENGTH_SHORT).show()
    }
}

// Modèle de données Post pour Firebase
data class Post(
    val postId: String = "",
    val userId: String = "",
    val challengeId: String = "",
    val content: String = "",
    val videoUrl: String = "",
    val timestamp: Long = 0
)

// Spinner pour sélectionner un défi
fun ChallengeSpinner(context: Context): Spinner {
    val challengeList = mutableListOf<String>()
    val database = FirebaseDatabase.getInstance().reference.child("challenges")

    val spinner = Spinner(context)
    val challengesAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, challengeList)
    challengesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = challengesAdapter

    database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            challengeList.clear()
            for (challengeSnapshot in snapshot.children) {
                val challenge = challengeSnapshot.getValue(Challenge::class.java)
                if (challenge != null) {
                    challengeList.add(challenge.title)
                }
            }
            challengesAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Erreur de chargement des défis", Toast.LENGTH_SHORT).show()
        }
    })

    return spinner
}

// Modèle Challenge
data class Challenge(val title: String = "", val description: String = "")
