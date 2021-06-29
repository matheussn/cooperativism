package com.example.cooperativism.session.service.impl

import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.session.Session
import com.example.cooperativism.session.repository.SessionRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

internal class SessionServiceImplTest {
    private val sessionRepository: SessionRepository = mock()

    private val sessionService = SessionServiceImpl(
        sessionRepository = sessionRepository
    )

    @Test
    fun `should throw a BusinessException when session already exist`() {
        val agendaId = UUID.randomUUID().toString()
        val session = Session(
            id = UUID.randomUUID().toString(),
            createdAt = Timestamp.from(Instant.now()),
            endsAt = Timestamp.from(Instant.now()),
            duration = BigInteger.ONE,
            agendaId = agendaId
        )

        val request = CreateSessionRequest(duration = null)
        whenever(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(session))

        assertThrows<BusinessException> { sessionService.createSession(agendaId, request) }
    }

    @Test
    fun `should call save function to create Session`() {
        val agendaId = UUID.randomUUID().toString()
        val request = CreateSessionRequest(duration = null)
        val session = buildSession(agendaId)
        whenever(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty())
        whenever(sessionRepository.save(any<Session>())).thenReturn(session)

        val result = sessionService.createSession(agendaId, request)

        assert(result.id === session.id)
        assert(result.agendaId === session.agendaId)
        assert(result.duration === session.duration)
        assert(result.endsAt === session.endsAt)
        verify(sessionRepository, times(1)).save(any())
    }

    @Test
    fun `should validate the qualified session to receive votes`() {
        val agendaId = UUID.randomUUID().toString()
        val session = buildSession(agendaId)
            .copy(endsAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)))
        whenever(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(session))

        assertDoesNotThrow { sessionService.validSessionToVote(agendaId) }
    }

    @Test
    fun `should validate that the session is not eligible to receive votes, as there are no sessions for that agenda`() {
        val agendaId = UUID.randomUUID().toString()
        whenever(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty())

        assertThrows<BusinessException> { sessionService.validSessionToVote(agendaId) }
    }

    @Test
    fun `should validate that the session is not eligible to receive votes, as the session has already ended`() {
        val agendaId = UUID.randomUUID().toString()
        val session = buildSession(agendaId)
            .copy(endsAt = Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)))
        whenever(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(session))

        assertThrows<BusinessException> { sessionService.validSessionToVote(agendaId) }
    }

    private fun buildSession(agendaId: String) =
        Session(
            id = UUID.randomUUID().toString(),
            createdAt = Timestamp.from(Instant.now()),
            endsAt = Timestamp.from(Instant.now()),
            duration = BigInteger.ONE,
            agendaId = agendaId
        )
}
