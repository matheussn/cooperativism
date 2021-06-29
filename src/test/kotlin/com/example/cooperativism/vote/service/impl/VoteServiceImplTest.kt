package com.example.cooperativism.vote.service.impl

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.result.VoteResult
import com.example.cooperativism.result.repository.VoteResultRepository
import com.example.cooperativism.vote.Vote
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.repository.VoteRepository
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.converters.toResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.Timestamp
import java.time.Instant
import java.util.*

internal class VoteServiceImplTest {
    private val voteRepository: VoteRepository = mock()
    private val voteResultRepository: VoteResultRepository = mock()

    private val voteService = VoteServiceImpl(
        voteRepository = voteRepository,
        voteResultRepository = voteResultRepository
    )

    @Test
    fun `should throw a BusinessException when vote already exists`() {
        val agendaId = UUID.randomUUID().toString()
        val request = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val vote = Vote(
            id = UUID.randomUUID().toString(),
            agendaId = agendaId,
            cpf = request.cpf,
            vote = request.vote,
            createdAt = Timestamp.from(Instant.now())
        )
        whenever(voteRepository.findByCpfAndAgendaId(request.cpf, agendaId)).thenReturn(Optional.of(vote))

        assertThrows<BusinessException> { voteService.computeVoteToSession(agendaId, request) }
    }

    @Test
    fun `should call save function to create vote`() {
        val agendaId = UUID.randomUUID().toString()
        val request = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val vote = Vote(
            id = UUID.randomUUID().toString(),
            agendaId = agendaId,
            cpf = request.cpf,
            vote = request.vote,
            createdAt = Timestamp.from(Instant.now())
        )
        whenever(voteRepository.findByCpfAndAgendaId(request.cpf, agendaId)).thenReturn(Optional.empty())
        whenever(voteRepository.save(any<Vote>())).thenReturn(vote)

        val result = voteService.computeVoteToSession(agendaId, request)

        assert(result == vote.toResponse())
        verify(voteRepository, times(1)).findByCpfAndAgendaId(request.cpf, agendaId)
        verify(voteRepository, times(1)).save(any())
    }

    @Test
    fun `should call the get Result function of a failed agenda`() {
        val agendaId = UUID.randomUUID().toString()
        val voteResult = buildFailAgendaResult(agendaId)

        whenever(voteResultRepository.getVoteResult(agendaId)).thenReturn(voteResult)

        val result = voteService.getResult(agendaId)

        verify(voteResultRepository, times(1)).getVoteResult(agendaId)
        assert(result.agendaId == agendaId)
        assert(result.votes[VoteEnum.NAO] == voteResult[0].total)
        assert(result.votes[VoteEnum.SIM] == voteResult[1].total)
        assert(result.result == ResultEnum.REPROVADO)
    }

    @Test
    fun `must call the get Result function of an approved agenda`() {
        val agendaId = UUID.randomUUID().toString()
        val voteResult = buildApprovedAgendaResult(agendaId)

        whenever(voteResultRepository.getVoteResult(agendaId)).thenReturn(voteResult)

        val result = voteService.getResult(agendaId)

        verify(voteResultRepository, times(1)).getVoteResult(agendaId)
        assert(result.agendaId == agendaId)
        assert(result.votes[VoteEnum.NAO] == voteResult[0].total)
        assert(result.votes[VoteEnum.SIM] == voteResult[1].total)
        assert(result.result == ResultEnum.APROVADO)
    }

    private fun buildFailAgendaResult(agendaId: String) =
        listOf(
            VoteResult(
                id = agendaId,
                vote = VoteEnum.NAO,
                total = 50.toBigInteger()
            ),
            VoteResult(
                id = agendaId,
                vote = VoteEnum.SIM,
                total = 49.toBigInteger()
            )
        )

    private fun buildApprovedAgendaResult(agendaId: String) =
        listOf(
            VoteResult(
                id = agendaId,
                vote = VoteEnum.NAO,
                total = 50.toBigInteger()
            ),
            VoteResult(
                id = agendaId,
                vote = VoteEnum.SIM,
                total = 51.toBigInteger()
            )
        )
}
