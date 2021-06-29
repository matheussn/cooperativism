package com.example.cooperativism.agenda.service

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.agenda.controller.response.CreateAgendaResponse
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.vote.service.ResultResponse

interface AgendaService {
    fun createAgenda(request: CreateAgendaRequest): CreateAgendaResponse
    fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse
    fun computeVote(agendaId: String, request: ComputeVoteRequest): ComputeVoteResponse
    fun computeResult(agendaId: String): ResultResponse
}