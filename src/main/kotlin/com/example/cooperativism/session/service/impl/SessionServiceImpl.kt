package com.example.cooperativism.session.service.impl

import com.example.cooperativism.exceptions.BadRequestException
import com.example.cooperativism.session.Session
import com.example.cooperativism.session.controller.representation.ComputeVoteRequest
import com.example.cooperativism.session.controller.representation.CreateSessionRequest
import com.example.cooperativism.session.controller.representation.CreateSessionResponse
import com.example.cooperativism.session.repository.SessionRepository
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.vote.Vote
import com.example.cooperativism.vote.repository.VoteRepository
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class SessionServiceImpl(
    private val sessionRepository: SessionRepository,
    private val voteRepository: VoteRepository
) : SessionService {
    override fun createSession(request: CreateSessionRequest): CreateSessionResponse {
        return sessionRepository.save(request.toEntity()).toResponse()
    }

    override fun computeVote(sessionId: String, request: ComputeVoteRequest) {
        sessionRepository.findById(sessionId)
            .also(::validSession)
            .get()
            .let {
                /* Chamar função do VoteService para salvar o voto.
                *  Considerar a validação do voto já existir para aquela sessão */
                voteRepository.save(request.toEntity(it.id))
            }
    }

    private fun validSession(session: Optional<Session>) {
        if (session.isEmpty)
            throw BadRequestException("Sessão não encontrada")
    }

    private fun ComputeVoteRequest.toEntity(sessionId: String) =
        Vote(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            cpf = this.cpf,
            vote = this.vote
        )

    private fun getEndTime(duration: BigInteger?): Timestamp =
        LocalDateTime.now()
            .plusMinutes(duration?.toLong() ?: 1)
            .let { Timestamp.valueOf(it) }

    private fun Session.toResponse() =
        CreateSessionResponse(
            id = this.id,
            name = this.name,
            description = this.description,
            duration = this.duration,
            endsAt = this.endsAt
        )

    private fun CreateSessionRequest.toEntity() =
        Session(
            id = UUID.randomUUID().toString(),
            name = this.name,
            description = this.description,
            createdAt = Timestamp.from(Instant.now()),
            endsAt = getEndTime(this.duration),
            duration = duration ?: BigInteger.ONE
        )
}