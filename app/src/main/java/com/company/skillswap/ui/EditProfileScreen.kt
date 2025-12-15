package com.company.skillswap.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.model.User
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.company.skillswap.viewmodel.DashViewModel
import com.company.skillswap.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController,profileViewModel: ProfileViewModel=viewModel(),dashViewModel: DashViewModel=viewModel()){

    BackHandler(true) {}

    val userData = profileViewModel.currentUser.collectAsState().value

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var offeredSkills by remember { mutableStateOf("") }
    var desiredSkills by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDate)

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("users")

    // Pré-remplir les champs avec les données existantes
    LaunchedEffect(userData) {
        userData?.let {
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            offeredSkills = it.offeredSkills?.joinToString(", ") ?: ""
            desiredSkills = it.desiredSkills?.joinToString(", ") ?: ""
            location = it.location ?: ""
            description = it.description ?: ""
            birthDate = try {
                it.birthDate?.let { dateStr ->
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)?.time
                }
            } catch (e: Exception) { null }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier le profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Prénom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = offeredSkills,
                onValueChange = { offeredSkills = it },
                label = { Text("Compétences offertes") },
                placeholder = { Text("Ex: Programmation, Design") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desiredSkills,
                onValueChange = { desiredSkills = it },
                label = { Text("Compétences recherchées") },
                placeholder = { Text("Ex: Photographie, Marketing") },
                singleLine = true,
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
                singleLine = true,
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
                        TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val uid = auth.currentUser?.uid
                        if (uid == null) {
                            Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updates = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "offeredSkills" to offeredSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            "desiredSkills" to desiredSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            "birthDate" to birthDate,
                            "location" to location,
                            "description" to description
                        )

                        database.child(uid).updateChildren(updates)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Profil mis à jour", Toast.LENGTH_SHORT).show()
                                dashViewModel.reloadUserLocation()  // pour le dashboard
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enregistrer")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annuler")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        FavoritesScreen(navController)
    }
}