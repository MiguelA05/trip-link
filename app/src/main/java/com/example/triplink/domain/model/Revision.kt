package com.example.triplink.domain.model

data class Revision(
    val id: String,
    val rechazo: Boolean,
    //Se ha decidido cambiar el nombre de este campo por comentario para permitir revisiones positivas
    val comentario: String,
)
