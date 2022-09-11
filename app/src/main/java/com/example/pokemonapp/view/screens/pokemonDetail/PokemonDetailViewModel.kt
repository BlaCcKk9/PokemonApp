package com.example.pokemonapp.view.screens.pokemonDetail

import androidx.lifecycle.ViewModel
import com.example.pokemonapp.data.remote.responses.Pokemon
import com.example.pokemonapp.repository.PokemonRepository
import com.example.pokemonapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> =
        repository.getPokemonInfo(pokemonName)

}