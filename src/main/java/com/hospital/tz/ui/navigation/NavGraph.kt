package com.hospital.tz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hospital.tz.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Map : Screen("map")
    object Learn : Screen("learn")
    object Messages : Screen("messages")
    object Profile : Screen("profile")
    object Appointments : Screen("appointments")
}

@Composable
fun NavGraph(navController: NavHostController, userId: String, userType: String) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(userId) }
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.Learn.route) { LearnScreen(userId) }
        composable(Screen.Messages.route) { MessagesScreen(userId) }
        composable(Screen.Profile.route) { ProfileScreen(userId) }
        composable(Screen.Appointments.route) { AppointmentsScreen(userId) }
        composable("patient_records") { PatientRecordsScreen(userId, userType) }
    }
}
