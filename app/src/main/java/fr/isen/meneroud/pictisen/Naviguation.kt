package fr.isen.meneroud.pictisen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import fr.isen.meneroud.pictisen.SignUpScreen
import fr.isen.meneroud.pictisen.LoginScreen

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "signup") {
        composable("signup") { SignUpScreen(navController, context) }
        composable("login") { LoginScreen(navController, context) }
        composable("home") { HomeScreen(navController) }
    }
}