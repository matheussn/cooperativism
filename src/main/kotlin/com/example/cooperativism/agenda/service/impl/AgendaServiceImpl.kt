package com.example.cooperativism.agenda.service.impl

import com.example.cooperativism.agenda.Agenda
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.agenda.controller.response.CreateAgendaResponse
import com.example.cooperativism.agenda.repository.AgendaRepository
import com.example.cooperativism.agenda.service.AgendaService
import com.example.cooperativism.agenda.service.converters.toEntity
import com.example.cooperativism.agenda.service.converters.toResponse
import com.example.cooperativism.session.Session
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.session.repository.SessionRepository
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.VoteService
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.Optional

@Service
class AgendaServiceImpl(
    private val agendaRepository: AgendaRepository,
    private val voteService: VoteService,
    private val sessionRepository: SessionRepository,
    private val sessionService: SessionService
) : AgendaService {
    override fun createAgenda(request: CreateAgendaRequest): CreateAgendaResponse {
        return agendaRepository.save(request.toEntity()).toResponse()
    }

    override fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse {
        return getAgendaById(agendaId)
            .let { sessionService.createSession(it.id, request) }
    }

    override fun computeVote(agendaId: String, request: ComputeVoteRequest): ComputeVoteResponse {
        return sessionRepository.findByAgendaId(agendaId)
            .also(::validateIfSessionIsAbleToVote)
            .let { voteService.computeVoteToSession(agendaId, request) }
    }

    override fun computeResult(agendaId: String): ResultResponse {
        return getAgendaById(agendaId)
            .let { voteService.getResult(it.id) }
    }

    private fun getAgendaById(agendaId: String): Agenda =
        agendaRepository.findById(agendaId)
            .also(::validIfExists)
            .get()

    private fun validIfExists(agenda: Optional<Agenda>) {
        if (agenda.isEmpty)
            throw BusinessException("Pauta não encontrada")
    }

    private fun validateIfSessionIsAbleToVote(session: Optional<Session>) {
        if (session.isEmpty)
            throw BusinessException("A agenda ainda não possui uma sessão de votos criada")
        session.get()
            .let {
                if (it.endsAt < Timestamp.from(Instant.now()))
                    throw BusinessException("A sessão finalizou às ${it.endsAt}")
            }
    }
}