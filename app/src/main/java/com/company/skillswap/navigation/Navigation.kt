package com.company.skillswap.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.skillswap.ui.*



object AppRoutes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val DASHBOARD = "dashboard"
    const val PROFILE_CONFIG = "profile_config"
    const val SKILL_DETAIL = "skill_detail/{userId}"

    const val PROFILE = "profile"

    const val EDIT_PROFILE = "edit_profile"
    const val FAVORITES = "favorites"

    const val MESSAGES = "messages"

    const val NOTIFICATIONS = "notifications"



}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(AppRoutes.HOME) {
            HomeScreen(navController)
        }

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController)
        }

        composable(AppRoutes.SIGNUP) {
            SignUpScreen(navController)
        }

        composable(AppRoutes.DASHBOARD) {
            DashScreen(navController)
        }

        composable(AppRoutes.PROFILE_CONFIG) {
            ProfileConfigScreen(navController)
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(navController)
        }

        composable(AppRoutes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }

        composable(AppRoutes.MESSAGES) {
            MessagesScreen(navController)
        }

        composable(AppRoutes.NOTIFICATIONS) {
            NotificationsScreen(navController)
        }

        composable(AppRoutes.FAVORITES) {
            FavoritesScreen(navController)
        }

        composable(AppRoutes.SKILL_DETAIL) {
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            SkillScreen(navController = navController, userId = userId)
        }
    }
}
