package com.ncf.seguros.indico.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ncf.seguros.indico.ui.screens.*
import com.ncf.seguros.indico.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Verificar se o usuário é administrador
    val isAdmin = currentUser?.email?.endsWith("@ncfseguros.com.br") ?: false
    
    NavHost(
        navController = navController,
        startDestination = when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                if (isAdmin) Screen.AdminIndications.route else Screen.Dashboard.route
            }
            else -> Screen.Login.route
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    if (isAdmin) {
                        navController.navigate(Screen.AdminIndications.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCondominiums = {
                    navController.navigate(Screen.Condominiums.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToInsurances = { condominiumId, condominiumName ->
                    navController.navigate("${Screen.Insurances.route}/$condominiumId/$condominiumName")
                },
                onNavigateToInsurance = { insuranceId, condominiumId ->
                    navController.navigate("${Screen.EditInsurance.route}/$insuranceId/$condominiumId")
                },
                onNavigateToMyIndications = {
                    navController.navigate(Screen.MyIndications.route)
                },
                onNavigateToAddIndication = {
                    navController.navigate(Screen.AddIndication.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Condominiums.route) {
            CondominiumsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddCondominium = {
                    navController.navigate(Screen.AddCondominium.route)
                },
                onNavigateToEditCondominium = { condominiumId ->
                    navController.navigate("${Screen.EditCondominium.route}/$condominiumId")
                },
                onNavigateToInsurances = { condominiumId, condominiumName ->
                    navController.navigate("${Screen.Insurances.route}/$condominiumId/$condominiumName")
                }
            )
        }
        
        composable(Screen.AddCondominium.route) {
            AddCondominiumScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCondominiumAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "${Screen.EditCondominium.route}/{condominiumId}",
            arguments = listOf(
                navArgument("condominiumId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val condominiumId = backStackEntry.arguments?.getString("condominiumId") ?: ""
            EditCondominiumScreen(
                condominiumId = condominiumId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCondominiumUpdated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "${Screen.Insurances.route}/{condominiumId}/{condominiumName}",
            arguments = listOf(
                navArgument("condominiumId") { type = NavType.StringType },
                navArgument("condominiumName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val condominiumId = backStackEntry.arguments?.getString("condominiumId") ?: ""
            val condominiumName = backStackEntry.arguments?.getString("condominiumName") ?: ""
            InsurancesScreen(
                condominiumId = condominiumId,
                condominiumName = condominiumName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddInsurance = {
                    navController.navigate("${Screen.AddInsurance.route}/$condominiumId")
                },
                onNavigateToEditInsurance = { insuranceId ->
                    navController.navigate("${Screen.EditInsurance.route}/$insuranceId/$condominiumId")
                }
            )
        }
        
        composable(
            route = "${Screen.AddInsurance.route}/{condominiumId}",
            arguments = listOf(
                navArgument("condominiumId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val condominiumId = backStackEntry.arguments?.getString("condominiumId") ?: ""
            AddInsuranceScreen(
                condominiumId = condominiumId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onInsuranceAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "${Screen.EditInsurance.route}/{insuranceId}/{condominiumId}",
            arguments = listOf(
                navArgument("insuranceId") { type = NavType.StringType },
                navArgument("condominiumId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val insuranceId = backStackEntry.arguments?.getString("insuranceId") ?: ""
            val condominiumId = backStackEntry.arguments?.getString("condominiumId") ?: ""
            EditInsuranceScreen(
                insuranceId = insuranceId,
                condominiumId = condominiumId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onInsuranceUpdated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.MyIndications.route) {
            MyIndicationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddIndication = {
                    navController.navigate(Screen.AddIndication.route)
                }
            )
        }
        
        composable(Screen.AddIndication.route) {
            AddIndicationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onIndicationAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AdminIndications.route) {
            AdminIndicationsScreen(
                onNavigateBack = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminIndications.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object Condominiums : Screen("condominiums")
    object AddCondominium : Screen("add_condominium")
    object EditCondominium : Screen("edit_condominium")
    object Insurances : Screen("insurances")
    object AddInsurance : Screen("add_insurance")
    object EditInsurance : Screen("edit_insurance")
    object MyIndications : Screen("my_indications")
    object AddIndication : Screen("add_indication")
    object AdminIndications : Screen("admin_indications")
} 