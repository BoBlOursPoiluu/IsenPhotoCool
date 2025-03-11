package fr.isen.meneroud.pictisen


import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
//import fr.isen.meneroud.pictisen.data.User
//import fr.isen.meneroud.pictisen.data.UserDatabase
import kotlinx.coroutines.launch
//import androidx.compose.ui.text.input.TextRange
import androidx.compose.ui.text.input.TransformedText

@Composable
fun SignUpScreen(navController: NavController, context: Context) {
    // États des champs
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImageUri = uri
    }

    // Base de données
    //val db = UserDatabase.getDatabase(context)
    //val userDao = db.userDao()
    val scope = rememberCoroutineScope()


    // Définition des couleurs
    val backgroundColor = Color(0xFF121212) // Noir
    //val backgroundColor = Color(0xFF0A192F)
    val primaryColor = Color(0xFF8A2BE2)   // Violet
    val textColor = Color.White

    var emailError by remember { mutableStateOf(false) }
    var birthDateState by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nom de l'application stylisé
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
                    // Photo de profil
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(model = profileImageUri, contentDescription = "Photo de profil")
                        } else {
                            Text("Ajouter", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Champs de texte avec styles
                    CustomTextField(value = firstName, onValueChange = { firstName = it }, label = "Prénom", primaryColor)
                    CustomTextField(value = lastName, onValueChange = { lastName = it }, label = "Nom", primaryColor)
                    CustomTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                        },
                        label = "Email",
                        primaryColor = primaryColor,
                        keyboardType = KeyboardType.Email
                    )
                    if(emailError){
                        Text("Adresse e-mail invalide", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                    CustomTextField(
                        value = birthDate,
                        onValueChange = { newValue ->
                            if (newValue.length <= 10) {birthDate = formatDate(newValue)}
                        },
                        label = "Date de naissance",
                        primaryColor = primaryColor,
                        keyboardType = KeyboardType.Number
                    )
                    CustomTextField(value = username, onValueChange = { username = it }, label = "Nom d'utilisateur", primaryColor)
                    CustomTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = "Code secret",
                        primaryColor = primaryColor,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouton d'inscription
                    Button(
                        onClick = { scope.launch {
                            val user = User(firstName, lastName, email, code)
                            val success = FirebaseService.addUser(user)
                            if (success){
                                navController.navigate("home"){popUpTo("signup"){inclusive = true} }
                            } else {
                                errorMessage = "Erreur lors de l'inscription"
                            }
                        } },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("S'inscrire", color = textColor)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bouton de navigation vers connexion
                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Déjà un compte ? Connectez-vous", color = primaryColor)
                    }
                }
            }
        }
    }
}

fun formatDate(input: String): String {
    val cleanedInput = input.filter { it.isDigit() } // Garde uniquement les chiffres
    val builder = StringBuilder()
    var cursorPosition = input.length

    for (i in cleanedInput.indices) {
        builder.append(cleanedInput[i])
        if ((i == 1 || i == 3) && i < cleanedInput.length - 1) {
            builder.append('/') // Ajoute un '/' après le jour et après le mois
        }
    }

    return builder.toString()
}


// Composable réutilisable pour les champs de texte
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    primaryColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.Gray,
            cursorColor = primaryColor
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}


