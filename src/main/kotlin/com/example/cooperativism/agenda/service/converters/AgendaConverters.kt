package com.example.cooperativism.agenda.service.converters

import com.example.cooperativism.agenda.Agenda
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.response.CreateAgendaResponse
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

internal fun Agenda.toResponse() =
    CreateAgendaResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt
    )

internal fun CreateAgendaRequest.toEntity() =
    Agenda(
        id = UUID.randomUUID().toString(),
        createdAt = Timestamp.from(Instant.now()),
        description = this.description,
        name = this.name
    )