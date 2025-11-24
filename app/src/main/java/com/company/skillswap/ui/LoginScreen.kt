package com.company.skillswap.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.R
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun LoginScreen(navController: NavController) {

    BackHandler(true) {}

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth: FirebaseAuth = Firebase.auth
    val database = FirebaseDatabase.getInstance().getReference("users")


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo SkillSwap",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Slogan
            Text(
                text = "Connectez-vous pour apprendre et partager",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Champ email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Champ mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton Se connecter
            Button(
                onClick = {

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    database.child(uid).get().addOnSuccessListener { snapshot ->
                                        val profileCompleted = snapshot.child("profileCompleted").getValue(Boolean::class.java) ?: false
                                        if (profileCompleted) {
                                            navController.navigate(AppRoutes.DASHBOARD) {
                                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        } else {
                                            navController.navigate(AppRoutes.PROFILE_CONFIG) {
                                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                } else {
                                    // Erreur
                                    Toast.makeText(context, "Erreur de connexion: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    }
                          },

                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Se connecter", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texte "Pas de compte ?" + bouton s'inscrire
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pas de compte ? ", fontSize = 16.sp)
                TextButton(onClick = {navController.navigate(AppRoutes.SIGNUP)}) {
                    Text(text = "Inscrivez-vous", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

//fun checkUserProfile(uid: String, navController: NavController, context: android.content.Context) {
//    val database = FirebaseDatabase.getInstance().getReference("users")
//    database.child(uid).get().addOnSuccessListener { snapshot ->
//        if (snapshot.exists()) {
//            // Profil existant → Dashboard
//            navController.navigate(AppRoutes.DASHBOARD) {
//                popUpTo(AppRoutes.LOGIN) { inclusive = true }
//                launchSingleTop = true
//            }
//        } else {
//            // Pas de profil → Configurer profil
//            navController.navigate(AppRoutes.PROFILE_CONFIG) {
//                popUpTo(AppRoutes.LOGIN) { inclusive = true }
//                launchSingleTop = true
//            }
//        }
//    }.addOnFailureListener {
//        Toast.makeText(context, "Erreur lors de la vérification du profil", Toast.LENGTH_SHORT).show()
//    }
//}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        LoginScreen(navController)
    }
}