package com.example.cooperativism.session.service.impl

import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.session.repository.SessionRepository
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.session.service.converters.toEntity
import com.example.cooperativism.session.service.converters.toResponse
import org.springframework.stereotype.Service

@Service
class SessionServiceImpl(
    private val sessionRepository: SessionRepository
) : SessionService {
    override fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse {
        sessionRepository.findByAgendaId(agendaId)
            .let {
                if (it.isPresent)
                    throw BusinessException("")
            }
        return sessionRepository.save(request.toEntity(agendaId)).toResponse()
    }
}