package com.company.skillswap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.model.UserSkill
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.company.skillswap.viewmodel.DashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, dashViewModel: DashViewModel = viewModel()){


    val skills by dashViewModel.skills.collectAsState()
    val favorites by dashViewModel.favorites.collectAsState()
    val favoriteSkills = skills.filter { favorites.contains(it.userId) }

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
                            text = "Favoris",
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
                    selected = true,
                    onClick = {navController.navigate(AppRoutes.FAVORITES){
                        launchSingleTop = true
                        popUpTo(AppRoutes.FAVORITES)
                    } },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoris",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = false,
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
        if (favoriteSkills.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun favori pour le moment")
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(favoriteSkills) { userSkill ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                        onClick = {navController.navigate("skill_detail/${userSkill.userId}")}
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Ligne compétence + favoris
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = userSkill.competence,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                IconButton(onClick = {
                                    dashViewModel.toggleFavoriteProfile(userSkill.userId)
                                }) {
                                    Icon(
                                        imageVector = if (favorites.contains(userSkill.userId)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favori",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${userSkill.firstName} ${userSkill.lastName}".uppercase(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = userSkill.city,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        FavoritesScreen(navController)
    }
}