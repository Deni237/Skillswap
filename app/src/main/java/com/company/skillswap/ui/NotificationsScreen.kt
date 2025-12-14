package com.company.skillswap.ui


import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.company.skillswap.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, notificationViewModel: NotificationViewModel = viewModel()) {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val notifications by notificationViewModel.notifications.collectAsState()

    // Charger les notifications pour l'utilisateur connecté
    LaunchedEffect(currentUserId) {
        notificationViewModel.loadNotifications(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune notification pour le moment")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                        onClick = {
                            when(notification.type) {
                                "new_request" -> {
                                    navController.navigate("request/${notification.requestId}")
                                }
                                "request_accepted" -> {
                                    navController.navigate("chat/${notification.senderId}")
                                }
                                else -> {
                                    // Pour les autres types, rien ou gérer selon besoin
                                }
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            when (notification.type) {

                                "new_request" -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Nouvelle demande de : ")

                                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                            append(notification.senderName)
                                            pop()
                                        },
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                "request_accepted" -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Votre demande a été acceptée par : ")

                                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                            append(notification.senderName)
                                            pop()

                                            append(", Faites un coucou")
                                        },
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                else -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Notification de : ")

                                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                            append(notification.senderName)
                                            pop()
                                        },
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        NotificationsScreen(navController)
    }
}