package com.example.cooperativism.agenda.controller.response

import java.math.BigInteger
import java.sql.Timestamp

data class CreateSessionResponse(
    val id: String,
    val agendaId: String,
    val duration: BigInteger,
    val endsAt: Timestamp
)
