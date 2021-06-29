package com.example.cooperativism.vote.service.impl

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.result.VoteResultRepository
import com.example.cooperativism.vote.Vote
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.repository.VoteRepository
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.VoteService
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
    override fun computeVoteToSession(agendaId: String, vote: ComputeVoteRequest): ComputeVoteResponse {
        voteRepository.findByCpfAndAgendaId(vote.cpf, agendaId)
            .also { validateVote(it, agendaId) }

        return voteRepository.save(vote.toEntity(agendaId)).toResponse()
    }

    override fun getResult(agendaId: String): ResultResponse {
        return voteResultRepository.getVoteResult(agendaId)
            .let { results ->
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
            throw BusinessException("O cpf ${vote.cpf} já votou na pauta $agendaId")
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