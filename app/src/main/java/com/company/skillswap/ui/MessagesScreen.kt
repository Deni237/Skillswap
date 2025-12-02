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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navController: NavController// tu peux connecter ton ViewModel plus tard
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Messages",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )


                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                NavigationBarItem(
                    selected = false,
                    onClick = {navController.navigate(AppRoutes.DASHBOARD){
                        launchSingleTop = true
                        popUpTo(AppRoutes.DASHBOARD)
                    } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {navController.navigate(AppRoutes.FAVORITES){
                        launchSingleTop = true
                        popUpTo(AppRoutes.FAVORITES)
                    } },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoris",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {navController.navigate(AppRoutes.MESSAGES){
                        launchSingleTop = true
                        popUpTo(AppRoutes.MESSAGES)
                    }},
                    icon = { Icon(Icons.Default.Email, contentDescription = "Messages",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {navController.navigate(AppRoutes.PROFILE){
                        launchSingleTop = true
                        popUpTo(AppRoutes.PROFILE)
                    } },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Compte",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Compte") }
                )
            }
        }
    ) { innerPadding ->


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun message pour le moment")
            }

    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        MessagesScreen(navController)
    }
}