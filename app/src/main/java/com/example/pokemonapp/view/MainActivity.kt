package com.example.pokemonapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.theme.PokemonAppTheme
import com.example.pokemonapp.view.screens.pokemonDetail.PokemonDetailScreen
import com.example.pokemonapp.view.screens.pokemonList.PokemonListScreen
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {
                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController)
                    }

                    composable(
                        "pokemon_detail_screen/{pokemonName}",
                        arguments = listOf(navArgument("pokemonName"){
                            type = NavType.StringType
                        })
                    ) {
                        val pokemonName = remember{
                            it.arguments?.getString("pokemonName")
                        }

                        PokemonDetailScreen(pokemonName = pokemonName?.toLowerCase(Locale.ROOT) ?: "", navController)
                    }
                }
            }
        }
    }
}