package com.company.skillswap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.company.skillswap.viewmodel.RequestViewModel
import com.company.skillswap.viewmodel.SkillViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    navController: NavController,
    requestId: String,
    requestViewModel: RequestViewModel = viewModel(),
    skillViewModel: SkillViewModel = viewModel()
) {
    BackHandler(true) {
        navController.popBackStack()
    }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val senderUser by skillViewModel.user.collectAsState()
    val request by requestViewModel.getRequest(requestId).collectAsState(initial = null)

    // Déterminer si la demande a déjà été traitée
    val isResponded = !request?.status.isNullOrEmpty()

    val isAccepted = request?.status == "Accepter"

    LaunchedEffect(request) {
        request?.senderId?.let { skillViewModel.loadUserSkill(it) }
        request?.let { requestViewModel.markAsRead(requestId) }
    }





    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demande") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nom + prénom
            Text(
                text = "${senderUser?.firstName ?: ""} ${senderUser?.lastName ?: ""}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            // Informations personnelles
            if (!senderUser?.email.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.primary)
                    Text(text = senderUser?.email ?: "", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (!senderUser?.location.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Ville", tint = MaterialTheme.colorScheme.primary)
                    Text(text = senderUser?.location ?: "", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (!senderUser?.offeredSkills.isNullOrEmpty()) {
                Text("Compétences offertes", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                senderUser!!.offeredSkills.forEach { skill ->
                    Text("• $skill", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (!senderUser?.description.isNullOrEmpty()) {
                Text("Bio", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Text(senderUser!!.description, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Boutons Accepter / Refuser
            // Bouton conditionnel
            if (isAccepted) {
                Button(
                    onClick = {
                        // Naviguer vers le chat avec l'utilisateur qui a envoyé la demande
                        navController.navigate("chat/${request?.senderId}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Envoyer un message")
                }
            } else {
                // Afficher les boutons Accepter / Refuser
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { requestViewModel.respondToRequest(requestId, true) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Accepter") }

                    Button(
                        onClick = { requestViewModel.respondToRequest(requestId, false) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) { Text("Refuser") }
                }
            }

        }
    }
}
