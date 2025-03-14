package fr.isen.meneroud.pictisen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar() {
    val primaryColor = Color(0xFF8A2BE2) // Violet
    val backgroundColor = Color(0xFF121212) // Noir

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Déf'ISEN",
            style = MaterialTheme.typography.headlineLarge.copy( // 🔹 Utilise le style officiel
                //fontFamily = montserratFont, // 🔹 Remplace la police par Montserrat
                fontSize = 32.sp, // 🔹 Taille ajustée pour être plus visible
                color = primaryColor
            )
        )
    }
}
