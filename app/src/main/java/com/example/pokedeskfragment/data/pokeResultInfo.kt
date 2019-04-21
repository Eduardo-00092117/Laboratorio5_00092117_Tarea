package com.example.pokedeskfragment.data

data class pokeResultInfo (
    var count : Int,
    var next : String,
    var previous : String,
    var results : ArrayList<pokemonResul>
)