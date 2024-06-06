package com.example.drawappcompose

import androidx.compose.runtime.Composable
import androidx.navigation.NavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.drawappcompose.detail.DetailViewModel
import com.example.drawappcompose.home.Home
import com.example.drawappcompose.home.HomeViewModel
import com.example.drawappcompose.login.LoginScreen
import com.example.drawappcompose.login.LoginViewModel
import com.example.drawappcompose.login.SignUpScreen
import com.example.drawappcompose.detail.DetailScreen
import com.example.drawappcompose.repository.StorageRepository
import com.example.drawappcompose.ui.SplashScreen

enum class LoginRoutes {
    SignUp,
    SignIn
}

enum class HomeRoutes {
    Home,
    Detail
}

enum class NestedRoutes {
    Main,
    Login
}

enum class AppRoutes {
    Splash
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel,
    storageRepository: StorageRepository
) {
    NavHost(
        navController = navController,
        //startDestination = NestedRoutes.Main.name
        startDestination = AppRoutes.Splash.name

    ) {
        splashGraph(navController, storageRepository)
        authGraph(navController, loginViewModel)
        homeGraph(navController = navController, detailViewModel, homeViewModel)
    }

}

fun NavGraphBuilder.splashGraph(
    navController: NavHostController,
    repository: StorageRepository
) {
    composable(route = AppRoutes.Splash.name) {
        SplashScreen(
            onTimeout = {
                navController.navigate(NestedRoutes.Login.name) {
                    popUpTo(AppRoutes.Splash.name) { inclusive = true }
                }
            },
            onUserLoggedIn = {
                navController.navigate(NestedRoutes.Main.name) {
                    popUpTo(AppRoutes.Splash.name) { inclusive = true }
                }
            },
            repository = repository
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        launchSingleTop = true
                        popUpTo(route = LoginRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignUp.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.SignUp.name) {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        popUpTo(LoginRoutes.SignUp.name) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }

    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        composable(HomeRoutes.Home.name) {
            Home(
                homeViewModel = homeViewModel,
                onDrawClick = { drawId ->
                    navController.navigate(
                        HomeRoutes.Detail.name + "?id=$drawId"
                    ) {
                        launchSingleTop = true
                    }

                },
                navToDetailScreen = {
                    navController.navigate(HomeRoutes.Detail.name)
                }) {
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }

            }
        }

        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            DetailScreen(
                detailViewModel = detailViewModel,
                drawId = entry.arguments?.getString("id") as String
            ) {
                navController.navigateUp()
            }

        }
    }

}