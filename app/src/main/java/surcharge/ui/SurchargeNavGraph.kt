package surcharge.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import surcharge.data.AppContainer
import surcharge.ui.account.AccountScreen
import surcharge.ui.home.HomeScreen
import surcharge.ui.manage.EditMenu
import surcharge.ui.pointOfSale.SalesMenu
import surcharge.ui.review.AnalyticsScreen
import surcharge.ui.review.ReviewScreen
import surcharge.ui.settings.DevScreen
import surcharge.ui.settings.SettingsScreen

@Composable
fun SurchargeNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SurchargeDestinations.HOME_ROUTE
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = SurchargeDestinations.HOME_ROUTE
        ) {
            HomeScreen(
                app = appContainer,
                onNavigateToAccount = { navController.navigate(SurchargeDestinations.ACCOUNT_ROUTE) },
                onNavigateToManage = { navController.navigate(SurchargeDestinations.MANAGE_SHOP_ROUTE) },
                onNavigateToShop = { navController.navigate(SurchargeDestinations.SHOP_ROUTE) },
                onNavigateToSales = { navController.navigate(SurchargeDestinations.REVIEW_ROUTE) },
                onNavigateToSettings = { navController.navigate(SurchargeDestinations.SETTINGS_ROUTE) }
            )
        }
        composable(
            route = SurchargeDestinations.ACCOUNT_ROUTE
        ) {
            AccountScreen(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.MANAGE_SHOP_ROUTE
        ) {
            EditMenu(
                data = appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SHOP_ROUTE
        ) {
            SalesMenu(
                data = appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.REVIEW_ROUTE
        ) {
            ReviewScreen(
                data = appContainer.data,
                onNavigateToAnalytics = { navController.navigate(SurchargeDestinations.ANALYTICS_ROUTE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.ANALYTICS_ROUTE
        ) {
            AnalyticsScreen(
                data = appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SETTINGS_ROUTE
        ) {
            SettingsScreen(
                app = appContainer,
                onNavigateToDev = { navController.navigate(SurchargeDestinations.DEV_ROUTE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.DEV_ROUTE
        ) {
            DevScreen(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
    }
}