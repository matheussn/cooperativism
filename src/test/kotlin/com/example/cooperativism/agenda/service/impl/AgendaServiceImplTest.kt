package com.example.cooperativism.agenda.service.impl

import com.example.cooperativism.agenda.Agenda
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.agenda.repository.AgendaRepository
import com.example.cooperativism.agenda.service.converters.toEntity
import com.example.cooperativism.agenda.service.converters.toResponse
import com.example.cooperativism.exceptions.NotFoundException
import com.example.cooperativism.session.service.SessionService
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.VoteService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.util.*


internal class AgendaServiceImplTest {
    private val agendaRepository: AgendaRepository = mock()
    private val sessionService: SessionService = mock()
    private val voteService: VoteService = mock()


    private val agendaService = AgendaServiceImpl(
        agendaRepository = agendaRepository,
        sessionService = sessionService,
        voteService = voteService
    )

    @Test
    fun `should create agenda with success`() {
        val request = CreateAgendaRequest(
            name = "Agenda Test 01",
            description = "Any Description"
        )
        val expectedEntity = request.toEntity()
        whenever(agendaRepository.save(any<Agenda>())).thenReturn(expectedEntity)

        val result = agendaService.createAgenda(request)

        assert(result == expectedEntity.toResponse())
        verify(agendaRepository, times(1)).save(any())
    }

    @Test
    fun `should call create Session function`() {
        val agendaId = UUID.randomUUID().toString()
        val request = CreateSessionRequest(
            duration = null
        )
        val expectedEntity = Agenda(
            id = agendaId,
            name = "Agenda Test 01",
            description = "Any Description",
            createdAt = Timestamp.from(Instant.now())
        )

        val expectedResponse = CreateSessionResponse(
            id = UUID.randomUUID().toString(),
            agendaId = agendaId,
            duration = BigInteger.ONE,
            endsAt = Timestamp.from(Instant.now())
        )
        whenever(agendaRepository.findById(any())).thenReturn(Optional.of(expectedEntity))
        whenever(sessionService.createSession(agendaId, request)).thenReturn(expectedResponse)

        val result = agendaService.createSession(agendaId, request)

        assert(result == expectedResponse)
    }

    @Test
    fun `should throw NotFoundException and do not call create Session function`() {
        val agendaId = UUID.randomUUID().toString()
        val request = CreateSessionRequest(duration = null)
        whenever(agendaRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { agendaService.createSession(agendaId, request) }
        verify(sessionService, never()).createSession(any(), any())
    }

    @Test
    fun `should call voteService function to compute Vote`() {
        val agendaId = UUID.randomUUID().toString()
        val request = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        whenever(voteService.computeVoteToSession(agendaId, request)).thenReturn(
            ComputeVoteResponse(
                cpf = request.cpf,
                vote = request.vote,
                agendaId = agendaId
            )
        )
        val agenda = Agenda(
            id = agendaId,
            name = "Agenda Test 01",
            description = "Any Description",
            createdAt = Timestamp.from(Instant.now())
        )
        whenever(agendaRepository.findById(any())).thenReturn(Optional.of(agenda))

        val result = agendaService.computeVote(agendaId, request)

        assert(result.agendaId == agendaId)
        assert(result.cpf == request.cpf)
        assert(result.vote == request.vote)
    }

    @Test
    fun `should throw NotFoundException and do not call get result function`() {
        val agendaId = UUID.randomUUID().toString()
        whenever(agendaRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { agendaService.computeResult(agendaId) }
        verify(voteService, never()).getResult(any())
    }

    @Test
    fun `should call get result function with success`() {
        val agendaId = UUID.randomUUID().toString()
        val entity = Agenda(
            id = agendaId,
            name = "Agenda Test 01",
            description = "Any Description",
            createdAt = Timestamp.from(Instant.now())
        )
        val expectedResult = ResultResponse(
            agendaId = agendaId,
            votes = mapOf(VoteEnum.SIM to BigInteger.ZERO, VoteEnum.NAO to BigInteger.TEN),
            result = ResultEnum.REPROVADO
        )
        whenever(agendaRepository.findById(any())).thenReturn(Optional.of(entity))
        whenever(voteService.getResult(agendaId)).thenReturn(expectedResult)

        val result = agendaService.computeResult(agendaId)

        assert(result === expectedResult)
    }
}
