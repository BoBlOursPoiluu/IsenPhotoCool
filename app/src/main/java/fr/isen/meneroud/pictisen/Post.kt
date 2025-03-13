package fr.isen.meneroud.pictisen

data class Post(
    val postId: String = "",
    val userId: String = "",
    val challengeId: String = "",
    val content: String = "",
    val likes: Map<String, Boolean> = mapOf(),
    val comments: Map<String, String> = mapOf(),
    val timestamp: Long = 0L,
    val videoUrl: String = ""
)
