package fr.isen.meneroud.pictisen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TestApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}