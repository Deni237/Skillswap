package com.company.skillswap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.skillswap.R
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.company.skillswap.viewmodel.DashViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashScreen(navController: NavController, dashViewModel: DashViewModel = viewModel()) {

    BackHandler(true) {}


    var skillQuery by remember { mutableStateOf("") }
    var cityQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val skills = dashViewModel.skills.collectAsState()
    val favorites by dashViewModel.favorites.collectAsState()


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
                        // Logo
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )

                        // Icône notification
                        IconButton(onClick = { navController.navigate(AppRoutes.NOTIFICATIONS){
                            launchSingleTop = true
                            popUpTo(AppRoutes.NOTIFICATIONS)
                        } }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                    selected = true,
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = skillQuery,
                        onValueChange = {skillQuery = it},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rechercher une compétence") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Recherche")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                dashViewModel.filterSkills(skillQuery, cityQuery)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Champ pour la ville
                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = { cityQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rechercher par ville") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                dashViewModel.filterSkills(skillQuery, cityQuery) // Filtrage dans le ViewModel
                            }
                        )
                    )



            }


            Spacer(modifier = Modifier.height(24.dp))

            // Titre
            Text(
                text = "Compétences disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des compétences (en colonne)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(skills.value) { userSkill ->
                    val isFavorite = favorites.contains(userSkill.userId)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate("skill_detail/${userSkill.userId}")
                        }
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
                                    text = userSkill.competences.joinToString("/ "),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                IconButton(onClick = {
                                    dashViewModel.toggleFavoriteProfile(userSkill.userId)
                                }) {

                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favori",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Nom en majuscule
                            Text(
                                text = "${userSkill.firstName} " + "${userSkill.lastName}".uppercase(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Ville
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
fun DashScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        DashScreen(navController)
    }
}