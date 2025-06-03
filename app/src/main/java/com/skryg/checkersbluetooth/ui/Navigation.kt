package com.skryg.checkersbluetooth.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.bluetooth.BluetoothUtils
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardGameCoreFactory
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.services.LocalGameProvider
import com.skryg.checkersbluetooth.game.ui.GameViewModelFactory
import com.skryg.checkersbluetooth.game.ui.view.BluetoothGameDestination
import com.skryg.checkersbluetooth.game.ui.view.BluetoothGameScreen
import com.skryg.checkersbluetooth.game.ui.view.LocalGameDestination
import com.skryg.checkersbluetooth.game.ui.view.LocalGameScreen
import com.skryg.checkersbluetooth.game.ui.view.SavedGameDestination
import com.skryg.checkersbluetooth.game.ui.view.ShowSavedGame
import com.skryg.checkersbluetooth.ui.screens.MainDestination
import com.skryg.checkersbluetooth.ui.screens.MainScreen
import com.skryg.checkersbluetooth.ui.screens.SavedGamesDestination
import com.skryg.checkersbluetooth.ui.screens.SavedGamesScreen
import com.skryg.checkersbluetooth.ui.screens.SettingsDestination
import com.skryg.checkersbluetooth.ui.screens.SettingsScreen
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeDestination
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeScreen
import com.skryg.checkersbluetooth.ui.screens.bluetooth.BluetoothHostDestination
import com.skryg.checkersbluetooth.ui.screens.bluetooth.BluetoothHostScreen
import com.skryg.checkersbluetooth.ui.screens.bluetooth.BluetoothScanDestination
import com.skryg.checkersbluetooth.ui.screens.bluetooth.BluetoothScanScreen
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
    LocalGameDestination.route to LocalGameDestination,
    SavedGameDestination.route to SavedGameDestination,
    BluetoothHostDestination.route to BluetoothHostDestination,
    BluetoothScanDestination.route to BluetoothScanDestination,
    BluetoothGameDestination.route to BluetoothGameDestination
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
    ){ it ->
        Column(modifier = Modifier.padding(it)){
            NavHost(navController = navController, startDestination = MainDestination.route){
                composable(MainDestination.route) {
                    val app = (LocalContext.current.applicationContext as CheckersApplication)
                    val gameController = app.container.gameController
                    val repository = app.container.gameRepository
                    val localGame = {
                        runBlocking {
                            val act = repository.getActiveLocalGames()
                            val gameId: Long
                            if(act.isNotEmpty()){
                                gameId = act[0].id
                            } else {
                                val connection = GameConnection.LOCAL
                                gameId = gameController.createGame(connection)
                            }
                            val gameProvider = LocalGameProvider(gameId, StandardGameCoreFactory(repository))
                            gameController.loadGame(gameProvider)

                            val route = LocalGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                                gameId.toString())
                            println("Route: $route")
                            navController.navigate(
                                route
                            )
                        }
                    }

                    val context = LocalContext.current
                    val navigateGameOngoing: (Long) -> Unit = { gameId ->
                        val route = BluetoothGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                            gameId.toString())
                        navController.navigate(route, navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                MainDestination.route,
                                inclusive = false
                            ).build())
                    }


                    val bluetoothHost = {
                        navController.navigate(BluetoothHostDestination.route)
                    }
                    val bluetoothScan = {
                        navController.navigate(BluetoothScanDestination.route)
                    }

                    MainScreen(
                        localGame = localGame,
                        bluetoothHost = bluetoothHost,
                        bluetoothScan = bluetoothScan,
                        navigateGame = navigateGameOngoing)
                }
                composable(SavedGamesDestination.route) {
                    SavedGamesScreen { game ->
                        val route = SavedGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                            game.id.toString())

                        navController.navigate(route)
                    }
                }
                composable(SettingsDestination.route) {
                    SettingsScreen{
                        navController.navigate(ThemeChangeDestination.route)
                    }
                }
                composable(ThemeChangeDestination.route){
                    ThemeChangeScreen()
                }
                composable(BluetoothGameDestination.route,
                    arguments = BluetoothGameDestination.arguments){ backStackEntry ->
                    val gameId = backStackEntry.arguments?.getLong("game_id")

                    gameId?.let{
                        BluetoothGameScreen(gameId) {
                            navController.navigateUp()
                        }
                    }


                }

                composable(LocalGameDestination.route,
                    arguments = LocalGameDestination.arguments){ backStackEntry ->
                    val app = (LocalContext.current.applicationContext as CheckersApplication)
                    val gameController = app.container.gameController
                    val repository = app.container.gameRepository

                    val gameId = backStackEntry.arguments?.getLong("game_id")

                    val goMenu: ()->Unit = {
                        navController.navigateUp()
                    }

                    val newGame: () -> Unit = {
                        runBlocking {
                            val gameConnection = GameConnection.LOCAL
                            val newGameId = gameController.createGame(gameConnection)
                            val provider = LocalGameProvider(newGameId, StandardGameCoreFactory(repository))

                            gameController.loadGame(provider)

                            navController.navigate(
                                LocalGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                                    newGameId.toString()),
                                navOptions = NavOptions.Builder()
                                    .setPopUpTo(
                                        MainDestination.route,
                                        inclusive = false
                                    ).build()
                            )
                        }
                    }

                    gameId?.let {
                        LocalGameScreen(gameId, newGame, goMenu)
                    }
                }
                composable(
                    SavedGameDestination.route,
                    arguments = SavedGameDestination.arguments){ backStackEntry ->
                    val gameId = backStackEntry.arguments?.getLong("game_id")
                    gameId?.let {
                        ShowSavedGame(gameId)
                    }
                }
                composable(BluetoothScanDestination.route){
                    val application = LocalContext.current.applicationContext as CheckersApplication
                    val viewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)

                    BluetoothScanScreen(viewModel(factory = viewModelFactory)) { gameId ->
                        val route = BluetoothGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                            gameId.toString())
                        navController.navigate(route, navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                MainDestination.route,
                                inclusive = false
                            ).build())
                    }
                }

                composable(BluetoothHostDestination.route){
                    val application = LocalContext.current.applicationContext as CheckersApplication
                    val viewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)

                    BluetoothHostScreen(viewModel(factory = viewModelFactory)) { gameId ->
                        val route = BluetoothGameDestination.route.replace(Regex("\\{[^}]*\\}"),
                            gameId.toString())
                        navController.navigate(route, navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                MainDestination.route,
                                inclusive = false
                            ).build())
                    }
                }


            }
        }

    }
}