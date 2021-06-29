package com.example.cooperativism.agenda.controller.response

import com.example.cooperativism.vote.VoteEnum

data class ComputeVoteResponse(
    val cpf: String,
    val vote: VoteEnum,
    val agendaId: String
)
