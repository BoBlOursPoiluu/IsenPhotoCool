package fr.isen.meneroud.pictisen



import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch


import fr.isen.meneroud.pictisen.FirebaseService


@Composable
fun LoginScreen(navController: NavController, context: Context) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Couleurs de l'interface
    val backgroundColor = Color(0xFF121212) // Noir
    val primaryColor = Color(0xFF8A2BE2)   // Violet
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nom de l'application
            Text(
                text = "Déf'ISEN",
                color = primaryColor,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Connexion",
                        color = textColor,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        primaryColor,
                        KeyboardType.Email
                    )
                    /*CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Nom d'utilisateur",
                        primaryColor
                    )*/
                    CustomTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = "Code secret",
                        primaryColor = primaryColor,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouton de connexion
                    Button(
                        onClick = {
                            scope.launch {
                                /*if (username.isNotBlank() && code.isNotBlank()) {
                                    val user = FirebaseService.getUser(username, code)
                                    //val success = FirebaseService.addUser(user)
                                    if (user != null) {
                                        FirebaseService.setCurrentUser(username, code)
                                        navController.navigate("main") { popUpTo("signup") { inclusive = true } }
                                    } */
                                if (email.isNotBlank() && code.isNotBlank()) {
                                    val user = FirebaseService.getUser(email, code)
                                    //val success = FirebaseService.addUser(user)
                                    if (user != null) {
                                        FirebaseService.setCurrentUser(user.username, user.email, user.code)
                                        navController.navigate("main") { popUpTo("signup") { inclusive = true } }
                                    }else {
                                        errorMessage = "Erreur lors de la connexion"
                                    }
                                } else {
                                    errorMessage = "Veuillez entrer un mail et un code secret"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Se connecter", color = textColor)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        errorMessage,
                        color = if (errorMessage == "Connexion réussie !") Color.Green else Color.Red
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bouton pour aller à la page d'inscription
                    TextButton(onClick = { navController.navigate("signup") }) {
                        Text("Pas encore inscrit ? Inscrivez-vous", color = primaryColor)
                    }
                }
            }
        }
    }
}


