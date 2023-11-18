package surcharge.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object SurchargeDestinations {
    const val HOME_ROUTE = "home"
    const val MANAGE_SHOP_ROUTE = "manage"
    const val SHOP_ROUTE = "shop"
    const val SALES_ROUTE = "sales"
    const val CART = "cart/{a}"
}

class SurchargeNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(SurchargeDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToManageShop: () -> Unit = {
        navController.navigate(SurchargeDestinations.MANAGE_SHOP_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToShop: () -> Unit = {
        navController.navigate(SurchargeDestinations.SHOP_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSales: () -> Unit = {
        navController.navigate(SurchargeDestinations.SALES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}