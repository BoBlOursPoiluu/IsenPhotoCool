package fr.isen.meneroud.pictisen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person

// Définition des écrans
sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", Icons.Filled.Home)
    object Defi : Screen("defi", Icons.Filled.PlayArrow)
    object Profil : Screen("profil", Icons.Filled.Person)
}

// Barre de navigation
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Defi, Screen.Profil)
    val primaryColor = Color(0xFF8A2BE2) // Violet
    val backgroundColor = Color(0xFF121212) // Noir

    NavigationBar(
        containerColor = backgroundColor,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp)) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = backgroundColor
                )
            )
        }
    }
}
