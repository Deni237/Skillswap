package com.company.skillswap.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.R
import com.company.skillswap.navigation.AppRoutes
import com.company.skillswap.ui.theme.SkillSwapTheme

@Composable
fun HomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo), // Remplace par ton logo
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Slogan
            Text(
                text = "Le meilleur service pour apprendre et partager",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Image centrale
            Image(
                painter = painterResource(id = R.drawable.ic_home_illustration), // Illustration centrale
                contentDescription = "Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton Inscription
            Button(
                onClick = {
                    navController.navigate(AppRoutes.SIGNUP)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("S'inscrire", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texte "Déjà un compte ?" + bouton S'identifier
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Déjà un compte ? ", fontSize = 16.sp)
                TextButton(onClick = { navController.navigate(AppRoutes.LOGIN) }) {
                    Text(text = "S'identifier", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SkillSwapTheme(dynamicColor = false) {
        HomeScreen(navController = rememberNavController())
    }
}