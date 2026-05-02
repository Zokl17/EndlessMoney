package com.example.endlessmoney.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.endlessmoney.ui.screens.AddTransactionScreen
import com.example.endlessmoney.ui.screens.HomeScreen
import com.example.endlessmoney.ui.screens.TransactionsScreen
import com.example.endlessmoney.ui.screens.SettingsScreen
object Routes {
    const val HOME = "home"
    const val ADD_TRANSACTION = "add_transaction"
    const val TRANSACTIONS = "transactions"

    const val SETTINGS = "settings"
}
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddExpenseClick = {
                    navController.navigate(Routes.ADD_TRANSACTION)
                },
                onOpenTransactionsClick = {
                    navController.navigate(Routes.TRANSACTIONS)
                },
                onOpenSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.ADD_TRANSACTION) {
            AddTransactionScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.TRANSACTIONS) {
            TransactionsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}