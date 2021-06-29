package com.example.cooperativism.vote.service.impl

import com.example.cooperativism.CooperativismMessage
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.result.repository.VoteResultRepository
import com.example.cooperativism.vote.Vote
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.repository.VoteRepository
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.VoteService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
class VoteServiceImpl(
    private val voteRepository: VoteRepository,
    private val voteResultRepository: VoteResultRepository
) : VoteService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(VoteServiceImpl::class.java)
    }

    override fun computeVoteToSession(agendaId: String, vote: ComputeVoteRequest): ComputeVoteResponse {
        log.info("[VOTE] Iniciando função computeVoteToSession")
        voteRepository.findByCpfAndAgendaId(vote.cpf, agendaId)
            .also { validateVote(it, agendaId) }

        val voteToSave = vote.toEntity(agendaId)
        log.info("[VOTE] Computando voto do usuário ${vote.cpf} | $voteToSave")
        return voteRepository.save(voteToSave).toResponse()
    }

    override fun getResult(agendaId: String): ResultResponse {
        log.info("[VOTE] Buscando resultado da pauta $agendaId")
        return voteResultRepository.getVoteResult(agendaId)
            .let { results ->
                log.info("[VOTE] Resultado encontrado $results")
                val noVote: BigInteger = results.find { it.vote == VoteEnum.NAO }?.total ?: BigInteger.ZERO
                val yesVote = results.find { it.vote == VoteEnum.SIM }?.total ?: BigInteger.ZERO
                ResultResponse(
                    agendaId = agendaId,
                    votes = mapOf(
                        VoteEnum.NAO to noVote,
                        VoteEnum.SIM to yesVote
                    ),
                    result = if (yesVote > noVote) ResultEnum.APROVADO else ResultEnum.REPROVADO
                )
            }
    }

    private fun validateVote(vote: Vote?, agendaId: String) {
        if (vote != null)
            throw BusinessException(
                CooperativismMessage.USER_HAS_ALREADY_VOTED_FOR_THIS_AGENDA,
                listOf(vote.cpf, agendaId)
            )
        log.info("[VOTE] O Usuário ainda não votou nesta pauta")
    }

    private fun ComputeVoteRequest.toEntity(agendaId: String) =
        Vote(
            id = UUID.randomUUID().toString(),
            createdAt = Timestamp.from(Instant.now()),
            cpf = this.cpf,
            vote = this.vote,
            agendaId = agendaId
        )

    private fun Vote.toResponse() =
        ComputeVoteResponse(cpf = this.cpf, vote = this.vote, agendaId = this.agendaId)
}