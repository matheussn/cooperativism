package com.example.cooperativism.session.service.converters

import com.example.cooperativism.session.Session
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

internal fun Session.toResponse() =
    CreateSessionResponse(
        id = this.id,
        agendaId = this.agendaId,
        duration = this.duration,
        endsAt = this.endsAt
    )

internal fun CreateSessionRequest.toEntity(agendaId: String) =
    Session(
        id = UUID.randomUUID().toString(),
        createdAt = Timestamp.from(Instant.now()),
        endsAt = getEndTime(this.duration),
        duration = duration ?: BigInteger.ONE,
        agendaId = agendaId
    )

private fun getEndTime(duration: BigInteger?): Timestamp =
    LocalDateTime.now()
        .plusMinutes(duration?.toLong() ?: 1)
        .let { Timestamp.valueOf(it) }