package com.example.pokemonapp.data.remote.responses

data class Pokemon(
    val abilities: List<Ability>,
    var base_experience: Int,
    val height: Int,
    val id: Int,
    val name: String,
    val species: Species,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Int
)