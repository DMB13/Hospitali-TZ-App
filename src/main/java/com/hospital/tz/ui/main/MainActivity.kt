package com.hospital.tz.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hospital.tz.R
import com.hospital.tz.ui.navigation.NavGraph
import com.hospital.tz.ui.navigation.Screen
import com.hospital.tz.ui.theme.HospitalTZTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("user_id") ?: ""

        setContent {
            HospitalTZTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        userId = userId,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Map,
        Screen.Learn,
        Screen.Messages,
        Screen.Appointments,
        Screen.Profile
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = getIconForScreen(screen)),
                        contentDescription = screen.route
                    )
                },
                label = { Text(getLabelForScreen(screen)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun getIconForScreen(screen: Screen): Int {
    return when (screen) {
        Screen.Home -> R.drawable.ic_home
        Screen.Map -> R.drawable.ic_map
        Screen.Learn -> R.drawable.ic_learn
        Screen.Messages -> R.drawable.ic_messages
        Screen.Appointments -> R.drawable.ic_appointments
        Screen.Profile -> R.drawable.ic_profile
    }
}

private fun getLabelForScreen(screen: Screen): String {
    return when (screen) {
        Screen.Home -> "Home"
        Screen.Map -> "Map"
        Screen.Learn -> "Learn"
        Screen.Messages -> "Messages"
        Screen.Appointments -> "Appointments"
        Screen.Profile -> "Profile"
    }
}
