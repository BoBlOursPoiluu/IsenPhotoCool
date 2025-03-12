package fr.isen.meneroud.pictisen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.*
import android.view.Gravity
import com.google.firebase.database.*

// Fonction principale qui crée l'écran de création de post
fun CreationPostScreen(context: Context): LinearLayout {
    val mainLayout = createMainLayout(context)

    mainLayout.addView(TitleSection(context))
    mainLayout.addView(DescriptionField(context))

    // Spinner pour sélectionner un défi
    val challengeSpinner = ChallengeSpinner(context)
    mainLayout.addView(challengeSpinner)

    val videoView = VideoView(context)
    mainLayout.addView(VideoPreview(context, videoView))
    mainLayout.addView(UploadButton(context))
    mainLayout.addView(PublishButton(context))

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

// Bouton pour ouvrir la galerie
fun UploadButton(context: Context): Button {
    return Button(context).apply {
        text = "Ajouter Vidéo"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 10, 0, 20) } // Ajout de marge
    }
}

// Bouton pour simuler la publication
fun PublishButton(context: Context): Button {
    return Button(context).apply {
        text = "Publier"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 10, 0, 20) } // Ajout de marge
        setOnClickListener {
            Toast.makeText(context, "Publication en préparation...", Toast.LENGTH_SHORT).show()
        }
    }
}

// Fonction pour créer le Spinner de sélection de défi
fun ChallengeSpinner(context: Context): Spinner {
    val challengeList = mutableListOf<String>()
    val database = FirebaseDatabase.getInstance().reference.child("challenges")

    val spinner = Spinner(context)

    // Récupérer les défis depuis Firebase
    val challengesAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, challengeList)
    challengesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = challengesAdapter

    // Charger les défis dans la liste
    val challengeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            challengeList.clear()
            for (challengeSnapshot in snapshot.children) {
                val challenge = challengeSnapshot.getValue(Challenge::class.java)
                if (challenge != null) {
                    challengeList.add(challenge.title) // Ajouter le titre du défi à la liste
                }
            }
            challengesAdapter.notifyDataSetChanged() // Mettre à jour l'adaptateur après la récupération des défis
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Erreur de chargement des défis", Toast.LENGTH_SHORT).show()
        }
    }
    database.addValueEventListener(challengeListener)

    return spinner
}

// Modèle Challenge pour récupérer les données de Firebase
data class Challenge(val title: String = "", val description: String = "")
