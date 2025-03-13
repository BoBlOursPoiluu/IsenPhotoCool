package fr.isen.meneroud.pictisen

import android.util.Log
import com.google.firebase.database.*
import fr.isen.meneroud.pictisen.Post

class FirebaseService {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getPostsFromFirebase(posts: MutableList<Post>) {
        val postsRef = database.child("posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPosts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        newPosts.add(post)
                    }
                }
                posts.clear()
                posts.addAll(newPosts)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erreur de lecture des posts", error.toException())
            }
        })
    }

    // Fonction pour ajouter un post à Firebase
    fun addPost(post: Post, onComplete: (Boolean) -> Unit) {
        val postId = post.postId
        val postRef = database.child("posts").child(postId)

        postRef.setValue(post).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseService", "Post ajouté avec succès")
                onComplete(true)
            } else {
                Log.e("FirebaseService", "Erreur lors de l'ajout du post", task.exception)
                onComplete(false)
            }
        }
    }
}
