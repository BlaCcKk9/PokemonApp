package com.example.pokemonapp.view.screens.pokemonDetail.helper

import com.example.pokemonapp.data.remote.responses.Pokemon
import kotlin.math.round

fun getWeightInKg(pokemon: Pokemon) : String {
    return round((pokemon.weight * 100f) / 1000f).toString() + "kg"
}

fun getHeightInM(pokemon: Pokemon): String{
    return round((pokemon.height * 100f) / 1000f).toString() + "m"
}