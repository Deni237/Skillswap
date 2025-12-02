package com.company.skillswap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.company.skillswap.model.UserSkill
import com.company.skillswap.viewmodel.SkillViewModel
import java.util.Date
import java.text.SimpleDateFormat
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillScreen(navController: NavController,userId: String,skillViewModel: SkillViewModel = viewModel ()) {

    val user = skillViewModel.user.collectAsState().value
    var isRequestSent by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        skillViewModel.loadUserSkill(userId)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        skillViewModel.hasSentRequest(currentUserId, userId) { sent ->
            isRequestSent = sent
        }
    }

    BackHandler(true) {
        navController.popBackStack()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Nom + Prénom
            Text(
                text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Informations personnelles",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            // Ville
            if (!user?.location.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Ville",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = user?.location ?: "",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Email
            if (!user?.email.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = user?.email ?: "",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Date de naissance
            if (!user?.birthDate.isNullOrEmpty() && user.birthDate != "Non défini") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date de naissance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${user.birthDate}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Compétences",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            // Compétences
            if (!user?.offeredSkills.isNullOrEmpty()) {

                user!!.offeredSkills.forEach { skill ->
                    Text(
                        text = "• $skill",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bio",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            // Description
            if (!user?.description.isNullOrEmpty()) {
                Text(
                    text = user!!.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                    user?.let {
                        skillViewModel.sendRequest(
                            senderId = currentUserId,
                            receiverId = userId,
                        )
                        isRequestSent = true
                    }
                },
                enabled =!isRequestSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (isRequestSent) "Demande envoyée" else "Postulez", fontSize = 18.sp)
            }


        }
    }
}

