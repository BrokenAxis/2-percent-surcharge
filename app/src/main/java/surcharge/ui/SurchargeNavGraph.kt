package surcharge.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import surcharge.data.AppContainer
import surcharge.ui.home.HomeScreen
import surcharge.ui.manage.EditMenu
import surcharge.ui.pointOfSale.SalesMenu
import surcharge.ui.review.ReviewScreen

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
            route = SurchargeDestinations.HOME_ROUTE,

        ) {
            HomeScreen(
                onNavigateToManage = { navController.navigate(SurchargeDestinations.MANAGE_SHOP_ROUTE) },
                onNavigateToShop = { navController.navigate(SurchargeDestinations.SHOP_ROUTE) },
                onNavigateToSales = { navController.navigate(SurchargeDestinations.SALES_ROUTE) },
            )
        }
        composable(
            route = SurchargeDestinations.MANAGE_SHOP_ROUTE
        ) {
            EditMenu(
                appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SHOP_ROUTE
        ) {
            SalesMenu(
                appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SALES_ROUTE
        ) {
            ReviewScreen(
                appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
    }
}