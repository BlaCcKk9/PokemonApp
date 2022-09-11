package com.example.pokemonapp.view.screens.pokemonList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.model.PokedexListEntry
import com.example.pokemonapp.data.remote.responses.PokemonList
import com.example.pokemonapp.repository.PokemonRepository
import com.example.pokemonapp.util.Constants.PAGE_SIZE
import com.example.pokemonapp.util.Resource
import com.example.pokemonapp.data.remote.responses.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private var repository: PokemonRepository,
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    resultSuccess(result)
                }
                is Resource.Error -> {
                    resultError(result)
                }
                else -> {}
            }
        }
    }

    private fun resultSuccess(result: Resource<PokemonList>) {
        endReached.value = curPage * PAGE_SIZE >= result.data!!.count
        val pokedexEntries = getPokedexEntries(result.data)
        curPage++
        loadError.value = ""
        isLoading.value = false
        pokemonList.value += pokedexEntries
    }

    private fun resultError(result: Resource<PokemonList>) {
        loadError.value = result.message!!
        isLoading.value = false
    }

    private fun getPokedexEntries(pokemonList: PokemonList): List<PokedexListEntry> =
        pokemonList.results.mapIndexed { index, entry ->
            PokedexListEntry(entry.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }, getImageUrlById(entry))
        }

    private fun getImageUrlById(entry: Result): String {
        val id = if (entry.url.endsWith("/")) {
            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
        } else {
            entry.url.takeLastWhile { it.isDigit() }
        }
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
    }

}