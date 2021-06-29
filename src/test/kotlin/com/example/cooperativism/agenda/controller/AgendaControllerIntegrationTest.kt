package com.example.cooperativism.agenda.controller

import com.example.cooperativism.BaseTest
import com.example.cooperativism.CooperativismMessage
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.repository.AgendaRepository
import com.example.cooperativism.exceptions.ErrorsDetails
import com.example.cooperativism.session.repository.SessionRepository
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.repository.VoteRepository
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.ResultResponse
import com.google.gson.Gson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigInteger
import java.util.*

internal class AgendaControllerIntegrationTest : BaseTest() {

    @Autowired
    private lateinit var agendaRepository: AgendaRepository

    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @Autowired
    private lateinit var voteRepository: VoteRepository

    private val gson: Gson = Gson()

    @BeforeEach
    fun init() {
        voteRepository.deleteAllInBatch()
        sessionRepository.deleteAllInBatch()
        agendaRepository.deleteAllInBatch()
    }

    @Test
    fun shouldPassThroughTheEntireStreamSuccessfully() {
        val agendaRequest = CreateAgendaRequest(
            name = "Agenda 01",
            description = "some Description"
        )

        val json = gson.toJson(agendaRequest)

        mockMvc.perform(post("/v1/agenda").contentType(MediaType.APPLICATION_JSON).content(json))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        val agenda = agendaRepository.findAll()
            .also {
                assert(it.isNotEmpty())
                assert(it.size == 1)
                assert(it[0].name == agendaRequest.name)
                assert(it[0].description == agendaRequest.description)
            }.first()

        val sessionRequest = CreateSessionRequest()
        val sessionJson = gson.toJson(sessionRequest)
        mockMvc.perform(
            post("/v1/agenda/${agenda.id}/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sessionJson)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        sessionRepository.findAll()
            .also {
                assert(it.isNotEmpty())
                assert(it.size == 1)
                assert(it[0].duration == BigInteger.ONE)
                assert(it[0].agendaId == agenda.id)
                val expectedEndsAt = it[0].createdAt.toLocalDateTime().plusMinutes(1)
                assert(it[0].endsAt.toLocalDateTime().hour == expectedEndsAt.hour)
                assert(it[0].endsAt.toLocalDateTime().minute == expectedEndsAt.minute)
                assert(it[0].endsAt.toLocalDateTime().second == expectedEndsAt.second)
            }

        val voteRequest = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val voteJson = gson.toJson(voteRequest)
        mockMvc.perform(
            post("/v1/agenda/${agenda.id}/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(voteJson)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        voteRepository.findAll()
            .also {
                assert(it.isNotEmpty())
                assert(it.size == 1)
                assert(it[0].agendaId == agenda.id)
                assert(it[0].cpf == voteRequest.cpf)
                assert(it[0].vote == voteRequest.vote)
            }

        val result = mockMvc.perform(get("/v1/agenda/${agenda.id}/result"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response
            .contentAsString

        val objResult = gson.fromJson(result, ResultResponse::class.java)

        assert(objResult.agendaId == agenda.id)
        assert(objResult.votes.values.size == 2)
        assert(objResult.votes[VoteEnum.NAO] == BigInteger.ZERO)
        assert(objResult.votes[VoteEnum.SIM] == BigInteger.ONE)
        assert(objResult.result == ResultEnum.APROVADO)
    }

    @Test
    fun `should throw an exception when try create vote without agenda`() {
        val voteRequest = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val voteJson = gson.toJson(voteRequest)

        val result = mockMvc.perform(
            post("/v1/agenda/${UUID.randomUUID()}/vote").contentType(MediaType.APPLICATION_JSON).content(voteJson)
        )
            .andDo(print())
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response
            .contentAsString

        val exception = gson.fromJson(result, ErrorsDetails::class.java)
        assert(exception.code != null)
        assert(exception.code?.isNotBlank()!!)
        assert(exception.code == CooperativismMessage.AGENDA_NOT_FOUND)
        assert(exception.details.isNotBlank())

        agendaRepository.findAll()
            .also { assert(it.isEmpty()) }
        sessionRepository.findAll()
            .also { assert(it.isEmpty()) }
        voteRepository.findAll()
            .also { assert(it.isEmpty()) }
    }

    @Test
    fun `must throw an exception when trying to create vote in a sessionless agenda`() {
        val agendaRequest = CreateAgendaRequest(
            name = "Agenda 01",
            description = "some Description"
        )

        val json = gson.toJson(agendaRequest)

        mockMvc.perform(post("/v1/agenda").contentType(MediaType.APPLICATION_JSON).content(json))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        val agenda = agendaRepository.findAll()
            .also {
                assert(it.isNotEmpty())
                assert(it.size == 1)
                assert(it[0].name == agendaRequest.name)
                assert(it[0].description == agendaRequest.description)
            }.first()

        val voteRequest = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val voteJson = gson.toJson(voteRequest)

        val result = mockMvc.perform(
            post("/v1/agenda/${agenda.id}/vote").contentType(MediaType.APPLICATION_JSON).content(voteJson)
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response
            .contentAsString

        val exception = gson.fromJson(result, ErrorsDetails::class.java)
        assert(exception.code != null)
        assert(exception.code?.isNotBlank()!!)
        assert(exception.code == CooperativismMessage.AGENDA_DOES_NOT_HAVE_A_VOTING_SESSION)
        assert(exception.details.isNotBlank())

        agendaRepository.findAll()
            .also {
                assert(it.isNotEmpty())
                assert(it.size == 1)
            }
        sessionRepository.findAll()
            .also { assert(it.isEmpty()) }
        voteRepository.findAll()
            .also { assert(it.isEmpty()) }
    }
}
