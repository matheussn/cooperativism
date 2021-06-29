package com.example.cooperativism.vote.service.converters

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.vote.Vote
import java.sql.Timestamp
import java.time.Instant
import java.util.*

internal fun ComputeVoteRequest.toEntity(agendaId: String) =
    Vote(
        id = UUID.randomUUID().toString(),
        createdAt = Timestamp.from(Instant.now()),
        cpf = this.cpf,
        vote = this.vote,
        agendaId = agendaId
    )

internal fun Vote.toResponse() =
    ComputeVoteResponse(cpf = this.cpf, vote = this.vote, agendaId = this.agendaId)
