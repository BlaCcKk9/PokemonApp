package com.example.pokemonapp.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Sprites(
    @SerializedName("front_default")
    val front_default: String,
)