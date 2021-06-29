package com.example.cooperativism.vote.service

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse

interface VoteService {
    fun computeVoteToSession(agendaId: String, vote: ComputeVoteRequest): ComputeVoteResponse
}