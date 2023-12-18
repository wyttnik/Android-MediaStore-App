package com.example.mediastoreapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mediastoreapp.ui.chooser.ChooserDestination
import com.example.mediastoreapp.ui.chooser.ImageChooserScreen
import com.example.mediastoreapp.ui.edit.EditDestination
import com.example.mediastoreapp.ui.edit.EditScreen
import com.example.mediastoreapp.ui.home.HomeDestination
import com.example.mediastoreapp.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToImageChooser = { navController.navigate(ChooserDestination.route) },
                navigateToTagEdit = {navController.navigate(EditDestination.route)}
//                navigateToItemUpdate = {
//                    navController.navigate("${ItemDetailsDestination.route}/${it}")
//                }
            )
        }
        composable(route = ChooserDestination.route) {
            ImageChooserScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(route = EditDestination.route) {
            EditScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}
