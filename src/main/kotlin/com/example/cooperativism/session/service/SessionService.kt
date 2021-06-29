package com.example.cooperativism.session.service

import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse

interface SessionService {
    fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse
}