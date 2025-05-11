package com.skryg.checkersbluetooth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardGameCoreFactory
import com.skryg.checkersbluetooth.game.ui.GameViewModelFactory
import com.skryg.checkersbluetooth.game.ui.local.LocalGameDestination
import com.skryg.checkersbluetooth.game.ui.local.LocalGameScreen
import com.skryg.checkersbluetooth.ui.screens.MainDestination
import com.skryg.checkersbluetooth.ui.screens.MainScreen
import com.skryg.checkersbluetooth.ui.screens.SavedGamesDestination
import com.skryg.checkersbluetooth.ui.screens.SavedGamesScreen
import com.skryg.checkersbluetooth.ui.screens.SettingsDestination
import com.skryg.checkersbluetooth.ui.screens.SettingsScreen
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeDestination
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeScreen
import com.skryg.checkersbluetooth.ui.utils.DefaultTopAppBar
import com.skryg.checkersbluetooth.ui.utils.MenuBottomBar
import kotlinx.coroutines.runBlocking

abstract class NavigationDestination(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val defaultTopBar: Boolean = true,
    val topBarContent:  @Composable () -> Unit = @Composable { },
    val defaultBottomBar: Boolean = true,
    val bottomBarContent:  @Composable () -> Unit = @Composable { },
    val icon: ImageVector? = null,
    val name: String = ""
)

val navigationDestinations = mapOf(
    MainDestination.route to MainDestination,
    SavedGamesDestination.route to SavedGamesDestination,
    SettingsDestination.route to SettingsDestination,
    ThemeChangeDestination.route to ThemeChangeDestination,
    LocalGameDestination.route to LocalGameDestination
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navDest = navigationDestinations[currentRoute]

    Scaffold(
        topBar = if(navDest?.defaultTopBar == false){
            navDest.topBarContent
        } else {
            { if(navDest != null) DefaultTopAppBar(navController = navController, navDest = navDest) }
        },
        bottomBar = if (navDest?.defaultBottomBar == false) {
            navDest.bottomBarContent
        } else {
            { MenuBottomBar(navController = navController) }
        }
    ){
        Column(modifier = Modifier.padding(it)){
            NavHost(navController = navController, startDestination = MainDestination.route){
                composable(MainDestination.route) {
                    val app = (LocalContext.current.applicationContext as CheckersApplication)
                    val gameController = app.container.gameController
                    val repository = app.container.gameRepository
                    val localGame = {
                        runBlocking {
                            val gameId = gameController.createGame(StandardGameCoreFactory(repository))
//                            repository.getGame
                            val route = LocalGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                                gameId.toString())
                            println("Route: $route")
                            navController.navigate(
                                route
                            )
                        }
                    }

                    MainScreen(localGame = localGame)
                }
                composable(SavedGamesDestination.route) {
                    SavedGamesScreen()
                }
                composable(SettingsDestination.route) {
                    SettingsScreen{
                        navController.navigate(ThemeChangeDestination.route)
                    }
                }
                composable(ThemeChangeDestination.route){
                    ThemeChangeScreen()
                }
                composable(LocalGameDestination.route,
                    arguments = LocalGameDestination.arguments){ backStackEntry ->
                    val gameId = backStackEntry.arguments?.getLong("game_id")
                    gameId?.let {
                        LocalGameScreen(navController, viewModel(factory = GameViewModelFactory(gameId)))
                    }
                }
            }
        }

    }
}