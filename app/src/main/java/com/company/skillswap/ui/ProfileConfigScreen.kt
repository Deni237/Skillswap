package com.company.skillswap.ui
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

@Composable
fun ProfileConfigScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var offeredSkills by remember { mutableStateOf("") }
    var desiredSkills by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uploadedPhotoName by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

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

            // Champ pour uploader la photo

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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desiredSkills,
                onValueChange = { desiredSkills = it },
                label = { Text("Compétences recherchées") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Date de naissance") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Localisation") },
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

            // Boutons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate(AppRoutes.DASHBOARD) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Valider")
                }

                OutlinedButton(
                    onClick = { navController.navigate(AppRoutes.DASHBOARD) },
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