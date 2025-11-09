package com.example.lab_week_09.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lab_week_09.Home
import com.example.lab_week_09.ResultContent

@Composable
fun App (navController: NavHostController) {
    NavHost (
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { navController.navigate (
                "resultContent/?listData=$it")
            }
        }

        composable (
            "resultContent/{listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) {
            ResultContent (
                it.arguments?.getString("listData").orEmpty()
            )
        }
    }
}