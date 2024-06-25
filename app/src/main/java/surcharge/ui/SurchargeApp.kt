package surcharge.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import surcharge.data.AppContainer

@Composable
fun SurchargeApp(appContainer: AppContainer, startDestination: String? = null) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        SurchargeNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: SurchargeDestinations.HOME_ROUTE
    SurchargeNavGraph(
        appContainer = appContainer,
        navController = navController,
        startDestination = startDestination ?: currentRoute
    )
}