package fr.isen.meneroud.pictisen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

fun Post_Screen(context: Context): LinearLayout {
    val mainLayout = MainLayout(context)
    mainLayout.addView(ChallengeHeader(context))
    mainLayout.addView(UserProfile(context))

    val videoView = createVideoView(context)
    mainLayout.addView(VideoContainer(context, videoView))
    mainLayout.addView(VideoControls(context, videoView))
    mainLayout.addView(LikeSection(context))

    return mainLayout
}

fun MainLayout(context: Context): LinearLayout {
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

fun ChallengeHeader(context: Context): LinearLayout {
    return LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addView(TextView(context).apply {
            text = "Coup de Pied Retourné ➡"
            textSize = 22f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.WHITE)
        })
    }
}

fun UserProfile(context: Context): LinearLayout {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val profileLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(16, 16, 16, 16)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
    val profileImageView = AppCompatImageView(context).apply {
        setImageResource(R.drawable.profil)
        layoutParams = LinearLayout.LayoutParams(100, 100).apply { marginEnd = 20 }
        clipToOutline = true
    }
    val pseudoTextView = TextView(context).apply {
        text = "Chargement..."
        textSize = 18f
        setTextColor(Color.WHITE)
    }
    profileLayout.addView(profileImageView)
    profileLayout.addView(pseudoTextView)
    auth.currentUser?.let { user ->
        database.child("users").child(user.uid).child("username")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pseudoTextView.text = snapshot.getValue(String::class.java) ?: "Utilisateur"
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Erreur lors de la lecture du pseudo", error.toException())
                }
            })
    }
    return profileLayout
}

fun createVideoView(context: Context): VideoView {
    return VideoView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 650
        ).apply { setMargins(0, 20, 0, 0) }
        val path = "android.resource://${context.packageName}/${R.raw.videotest}"
        setVideoURI(Uri.parse(path))
        start() // Démarre la vidéo automatiquement
    }
}

fun VideoContainer(context: Context, videoView: VideoView): LinearLayout {
    return LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setPadding(16, 16, 16, 16)
        background = GradientDrawable().apply {
            setStroke(4, Color.parseColor("#BB86FC"))
            cornerRadius = 20f
        }
        addView(videoView)
    }
}

fun VideoControls(context: Context, videoView: VideoView): LinearLayout {
    return LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val pauseResumeButton = Button(context).apply {
            text = "Pause"
            setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                    text = "Reprendre"
                } else {
                    videoView.start()
                    text = "Pause"
                }
            }
        }
        addView(pauseResumeButton)
    }
}

fun LikeSection(context: Context): LinearLayout {
    val likeLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(16, 16, 16, 16)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
    val likeButton = ImageView(context).apply {
        setImageResource(R.drawable.likev2)
        layoutParams = LinearLayout.LayoutParams(80, 80).apply { marginEnd = 16 }
    }
    val likeCount = TextView(context).apply {
        text = "4211"
        textSize = 18f
        setTextColor(Color.WHITE)
    }
    var liked = false
    likeButton.setOnClickListener {
        liked = !liked
        if (liked) {
            likeButton.setImageResource(R.drawable.likerouge)
            likeCount.text = (likeCount.text.toString().toInt() + 1).toString()
        } else {
            likeButton.setImageResource(R.drawable.likev2)
            likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
        }
    }
    likeLayout.addView(likeButton)
    likeLayout.addView(likeCount)
    return likeLayout
}
