package com.ncf.seguros.indico.v2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ncf.seguros.indico.v2.ui.home.HomeScreen
import com.ncf.seguros.indico.v2.ui.login.LoginScreen
import com.ncf.seguros.indico.v2.ui.policy.PolicyListScreen
import com.ncf.seguros.indico.v2.ui.profile.ProfileScreen

/** Rotas principais do aplicativo */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object PolicyList : Screen("policies")
    data object Profile : Screen("profile")
    data object Admin : Screen("admin")
    data object AdminUsers : Screen("admin/users")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                    onNavigateToPolicies = { navController.navigate(Screen.PolicyList.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.PolicyList.route) { PolicyListScreen() }

        composable(Screen.Profile.route) { ProfileScreen() }

        composable(Screen.Admin.route) {
            AdminScreen(onNavigateToUsers = { navController.navigate(Screen.AdminUsers.route) })
        }

        composable(Screen.AdminUsers.route) {
            AdminUsersScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
