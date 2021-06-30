package com.example.cooperativism.agenda.service.impl

import com.example.cooperativism.CooperativismMessage
import com.example.cooperativism.agenda.Agenda
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.agenda.controller.response.CreateAgendaResponse
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.agenda.repository.AgendaRepository
import com.example.cooperativism.agenda.service.AgendaService
import com.example.cooperativism.agenda.service.converters.toEntity
import com.example.cooperativism.agenda.service.converters.toResponse
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.exceptions.NotFoundException
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.validcpf.ValidCpfResource
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.VoteService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class AgendaServiceImpl(
    private val agendaRepository: AgendaRepository,
    private val voteService: VoteService,
    private val sessionService: SessionService,
    private val validCpfResource: ValidCpfResource
) : AgendaService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(AgendaServiceImpl::class.java)
    }

    override fun createAgenda(request: CreateAgendaRequest): CreateAgendaResponse {
        val agenda = request.toEntity()
        log.info("[AGENDA] Pauta a ser criada $agenda")
        return agendaRepository.save(agenda).toResponse()
    }

    override fun createSession(agendaId: String, request: CreateSessionRequest): CreateSessionResponse {
        log.info("[AGENDA] Iniciando criação de sessão para a pauta $agendaId")
        return getAgendaById(agendaId)
            .let { sessionService.createSession(it.id, request) }
    }

    override fun computeVote(agendaId: String, request: ComputeVoteRequest): ComputeVoteResponse {
        log.info("[AGENDA] Validando cpf para realizar o voto")
        validCpfResource.validCpf(request.cpf)
            .let {
                if (!it.isValid) throw BusinessException(
                    CooperativismMessage.USER_IS_UNABLE_TO_VOTE,
                    listOf(request.cpf)
                )
            }

        log.info("[AGENDA] Computando voto para pauta $agendaId Request $request")

        return getAgendaById(agendaId)
            .let { agenda ->
                sessionService.validSessionToVote(agenda.id)
                    .let { voteService.computeVoteToSession(agenda.id, request) }
            }
    }

    override fun computeResult(agendaId: String): ResultResponse {
        log.info("[AGENDA] Computando resultado da pauta $agendaId")
        return getAgendaById(agendaId)
            .let { voteService.getResult(it.id) }
    }

    private fun getAgendaById(agendaId: String): Agenda =
        agendaRepository.findById(agendaId)
            .also(::validIfExists)
            .get()
            .also { log.info("[AGENDA] Pauta encontrada: $it") }

    private fun validIfExists(agenda: Optional<Agenda>) {
        if (agenda.isEmpty)
            throw NotFoundException(CooperativismMessage.AGENDA_NOT_FOUND)
    }
}