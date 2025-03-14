package fr.isen.meneroud.pictisen.data

import androidx.compose.ui.text.LinkAnnotation


data class User(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val code: String = "",
    val profileImageUrl: String = ""
)
