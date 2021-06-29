package com.example.cooperativism.vote.service.impl

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.exceptions.VoteForThisSessionAlreadyComputed
import com.example.cooperativism.vote.Vote
import com.example.cooperativism.vote.repository.VoteRepository
import com.example.cooperativism.vote.service.VoteService
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
class VoteServiceImpl(
    private val voteRepository: VoteRepository
) : VoteService {
    override fun computeVoteToSession(agendaId: String, vote: ComputeVoteRequest): ComputeVoteResponse {
        voteRepository.findByCpfAndAgendaId(vote.cpf, agendaId)
            .also { validateVote(it, agendaId) }

        return voteRepository.save(vote.toEntity(agendaId)).toResponse()
    }

    private fun validateVote(vote: Vote?, agendaId: String) {
        if (vote != null)
            throw VoteForThisSessionAlreadyComputed("O cpf ${vote.cpf} já votou na pauta $agendaId")
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
        ComputeVoteResponse(cpf = cpf, vote = vote, sessionId = "sessionId")
}