package com.example.cooperativism.agenda.controller.response

import java.sql.Timestamp

data class CreateAgendaResponse(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Timestamp
)
