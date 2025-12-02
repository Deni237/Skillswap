package com.company.skillswap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.R
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme
import com.company.skillswap.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel= viewModel()){
    BackHandler(true) {}

    val scrollState = rememberScrollState() // <-- scroll state
    val user = profileViewModel.currentUser.collectAsState().value
    val keyboardController = LocalSoftwareKeyboardController.current
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
    }


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
                            text = "Profil",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "menu",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Déconnexion") },
                                    onClick = {
                                        expanded = false
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate(AppRoutes.LOGIN) {
                                            popUpTo(AppRoutes.PROFILE) { inclusive = true } // supprime la stack pour éviter retour arrière
                                        }
                                    }
                                )

                            }
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
                    selected = false,
                    onClick = {navController.navigate(AppRoutes.MESSAGES){
                        launchSingleTop = true
                        popUpTo(AppRoutes.MESSAGES)
                    }},
                    icon = { Icon(Icons.Default.Email, contentDescription = "Messages",tint = MaterialTheme.colorScheme.primary) },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = true,
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
                .verticalScroll(scrollState)
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

            Button(
                onClick = {
                    navController.navigate(AppRoutes.EDIT_PROFILE)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Modifier profil",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Modifier le profil")
            }

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
                text = "Compétences désirées",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

            // Compétences
            if (!user?.desiredSkills.isNullOrEmpty()) {

                user!!.desiredSkills.forEach { skill ->
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


        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        var navController = rememberNavController()
        ProfileScreen(navController)
    }
}