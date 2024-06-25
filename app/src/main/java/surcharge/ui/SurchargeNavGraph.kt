package surcharge.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import surcharge.data.AppContainer
import surcharge.ui.account.AccountScreen
import surcharge.ui.home.HomeScreen
import surcharge.ui.login.LoginScreen
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
    startDestination: String = SurchargeDestinations.HOME_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(
            route = SurchargeDestinations.LOGIN_ROUTE,
        ) {
            LoginScreen(
                app = appContainer,
                onNavigateToHome = { navController.navigate(SurchargeDestinations.HOME_ROUTE) }
            )
        }
        composable(
            route = SurchargeDestinations.HOME_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        10000, easing = LinearEasing
                    )
                )
            },
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
            route = SurchargeDestinations.ACCOUNT_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideInVertically(
                    animationSpec = tween(300, easing = EaseInOut),
                    initialOffsetY = { it / 2 }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(300, easing = EaseOut),
                    targetOffsetY = { it / 2 }
                )
            }
        ) {
            AccountScreen(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.MANAGE_SHOP_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            EditMenu(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SHOP_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            SalesMenu(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.REVIEW_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(300, easing = EaseOut),
                    targetOffsetX = { it / 2 }
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(300, easing = EaseIn),
                    initialOffsetX = { -it / 2 }
                )
            }
        ) {
            ReviewScreen(
                app = appContainer,
                onNavigateToAnalytics = { navController.navigate(SurchargeDestinations.ANALYTICS_ROUTE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.ANALYTICS_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(300, easing = EaseOut),
                    targetOffsetX = { it / 2 }
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(300, easing = EaseIn),
                    initialOffsetX = { -it / 2 }
                )
            }
        ) {
            AnalyticsScreen(
                data = appContainer.data,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.SETTINGS_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideInHorizontally(
                    animationSpec = tween(300, easing = EaseIn),
                    initialOffsetX = { it / 2 }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(300, easing = EaseOut),
                    targetOffsetX = { -it / 2 }
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(300, easing = EaseIn),
                    initialOffsetX = { -it / 2 }
                )
            }
        ) {
            SettingsScreen(
                app = appContainer,
                onNavigateToDev = { navController.navigate(SurchargeDestinations.DEV_ROUTE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SurchargeDestinations.DEV_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            DevScreen(
                app = appContainer,
                onBack = { navController.popBackStack() }
            )
        }
    }
}