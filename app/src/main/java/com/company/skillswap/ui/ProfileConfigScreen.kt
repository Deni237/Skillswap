package com.company.skillswap.ui
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.R
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.company.skillswap.model.User
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileConfigScreen(navController: NavController) {

    BackHandler(true) {}

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var offeredSkills by remember { mutableStateOf("") }
    var desiredSkills by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uploadedPhotoName by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("users")

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDate)


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuration du profil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))


            // Champs texte
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Prénom") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = offeredSkills,
                onValueChange = { offeredSkills = it },
                label = { Text("Compétences offertes") },
                placeholder = { Text("Ex: Programmation, Design") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desiredSkills,
                onValueChange = { desiredSkills = it },
                label = { Text("Compétences recherchées") },
                placeholder = { Text("Ex: Photographie, Marketing") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            val formattedDate = birthDate?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
            } ?: ""

            OutlinedTextField(
                value = formattedDate,
                onValueChange = { },
                label = { Text("Date de naissance") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Sélectionner date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
                )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ville") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DatePickerDialog Compose
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            birthDate = datePickerState.selectedDateMillis
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Boutons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {

                        val uid = auth.currentUser?.uid
                        val username = auth.currentUser?.displayName ?: ""

                        if (uid == null) {
                            Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Vérification minimale
                        if (firstName.isBlank() || lastName.isBlank()) {
                            Toast.makeText(context, "Veuillez remplir au moins le prénom et le nom", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Convertir les compétences en listes
                        val offeredSkillsList = offeredSkills
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        val desiredSkillsList = desiredSkills
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        val userProfileUpdates = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "offeredSkills" to offeredSkillsList,
                            "desiredSkills" to desiredSkillsList,
                            "birthDate" to birthDate,
                            "location" to location,
                            "description" to description,
                            "profileCompleted" to true
                        )


                        database.child(uid).updateChildren(userProfileUpdates)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
                                navController.navigate(AppRoutes.DASHBOARD) {
                                    popUpTo(AppRoutes.PROFILE_CONFIG) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erreur lors de la sauvegarde : ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Valider")
                }

                OutlinedButton(
                    onClick = { navController.navigate(AppRoutes.DASHBOARD){
                        popUpTo(AppRoutes.PROFILE_CONFIG) { inclusive = true }
                        launchSingleTop = true
                    } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ignorer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ProfileConfigScreenPreview() {
    var navController = rememberNavController()
    SkillSwapTheme(dynamicColor = false) {
        ProfileConfigScreen(navController)
    }
}