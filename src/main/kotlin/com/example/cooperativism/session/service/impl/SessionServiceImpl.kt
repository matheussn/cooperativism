package com.example.cooperativism.session.service.impl

import com.example.cooperativism.CooperativismMessage
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.agenda.service.impl.AgendaServiceImpl
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.session.Session
import com.example.cooperativism.session.repository.SessionRepository
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.session.service.converters.toEntity
import com.example.cooperativism.session.service.converters.toResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
class SessionServiceImpl(
    private val sessionRepository: SessionRepository
) : SessionService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(SessionServiceImpl::class.java)
    }

    override fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse {
        log.info("[SESSION] Criando sessão para a pauta $agendaId Request $request")
        sessionRepository.findByAgendaId(agendaId)
            .let {
                if (it.isPresent) {
                    log.error("[SESSION] Já existe uma sessão para a pauta: $agendaId")
                    throw BusinessException(CooperativismMessage.AGENDA_ALREADY_HAVE_A_SESSION)
                }
            }

        val session = request.toEntity(agendaId)
        log.info("[SESSION] A pauta $agendaId ainda não possui sessões. Sessão a ser criada $session")
        return sessionRepository.save(session).toResponse()
    }

    override fun validSessionToVote(agendaId: String) {
        log.info("[SESSION] Iniciando validação se a agenda possui uma sessão de votação valida")
        sessionRepository.findByAgendaId(agendaId)
            .also(::validateIfSessionIsAbleToVote)
    }

    private fun validateIfSessionIsAbleToVote(session: Optional<Session>) {
        if (session.isEmpty)
            throw BusinessException(CooperativismMessage.AGENDA_DOES_NOT_HAVE_A_VOTING_SESSION)
        session.get()
            .let {
                if (it.endsAt < Timestamp.from(Instant.now()))
                    throw BusinessException(CooperativismMessage.SESSION_HAS_ALREADY_ENDED, listOf(it.endsAt))
            }
        log.info("[SESSION] Validação finalizada. Sessão está hábil para receber votos")
    }
}