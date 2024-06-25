package surcharge.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object SurchargeDestinations {
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home"
    const val ACCOUNT_ROUTE = "account"
    const val MANAGE_SHOP_ROUTE = "manage"
    const val SHOP_ROUTE = "shop"
    const val REVIEW_ROUTE = "review"
    const val ANALYTICS_ROUTE = "review/analytics"
    const val CART = "cart/{a}"
    const val SETTINGS_ROUTE = "settings"
    const val DEV_ROUTE = "settings/dev"
}

class SurchargeNavigationActions(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(SurchargeDestinations.LOGIN_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    val navigateToHome: () -> Unit = {
        navController.navigate(SurchargeDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAccount: () -> Unit = {
        navController.navigate(SurchargeDestinations.ACCOUNT_ROUTE) {
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

    val navigateToReview: () -> Unit = {
        navController.navigate(SurchargeDestinations.REVIEW_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAnalytics: () -> Unit = {
        navController.navigate(SurchargeDestinations.ANALYTICS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(SurchargeDestinations.SETTINGS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDev: () -> Unit = {
        navController.navigate(SurchargeDestinations.DEV_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}