package fr.isen.meneroud.pictisen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.Gravity
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.setPadding

fun Post_Screen(context: Context): LinearLayout {
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Color.parseColor("#2E1A47"), Color.parseColor("#120A16"))
    )

    val mainLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(32)
        background = gradientDrawable
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    val challengeHeader = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val challengeTitle = TextView(context).apply {
        text = "Coup de Pied Retourné ➡"
        textSize = 22f
        setPadding(16)
        setTextColor(Color.WHITE)
    }

    challengeHeader.addView(challengeTitle)

    val profileLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(16)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val profileImageView = AppCompatImageView(context).apply {
        setImageResource(R.drawable.profil)
        layoutParams = LinearLayout.LayoutParams(100, 100).apply { marginEnd = 20 }
        clipToOutline = true
        outlineProvider = ViewOutlineProvider.BACKGROUND
    }

    val pseudoTextView = TextView(context).apply {
        text = "BobL'oursPoilu"
        textSize = 18f
        setTextColor(Color.WHITE)
    }

    profileLayout.addView(profileImageView)
    profileLayout.addView(pseudoTextView)

    val postVideoView = VideoView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            650
        ).apply { setMargins(0, 20, 0, 0) }
        val path = "android.resource://${context.packageName}/${R.raw.videotest}"
        setVideoURI(Uri.parse(path))
        start()
    }

    val postContainer = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setPadding(16)
        background = GradientDrawable().apply {
            setStroke(4, Color.parseColor("#BB86FC"))
            cornerRadius = 20f
        }
        addView(postVideoView)
    }

    val likeLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(16)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val likeButton = ImageView(context).apply {
        setImageResource(R.drawable.like)
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
            likeButton.setImageResource(R.drawable.like)
            likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
        }
    }

    likeLayout.addView(likeButton)
    likeLayout.addView(likeCount)

    mainLayout.addView(challengeHeader)
    mainLayout.addView(profileLayout)
    mainLayout.addView(postContainer)
    mainLayout.addView(likeLayout)

    return mainLayout
}
