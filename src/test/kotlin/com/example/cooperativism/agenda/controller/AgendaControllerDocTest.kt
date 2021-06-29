package com.example.cooperativism.agenda.controller

import com.example.cooperativism.BaseTest
import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.service.AgendaService
import com.example.cooperativism.agenda.service.converters.toEntity
import com.example.cooperativism.agenda.service.converters.toResponse
import com.example.cooperativism.session.service.converters.toEntity
import com.example.cooperativism.session.service.converters.toResponse
import com.example.cooperativism.vote.VoteEnum
import com.example.cooperativism.vote.service.ResultEnum
import com.example.cooperativism.vote.service.ResultResponse
import com.example.cooperativism.vote.service.converters.toEntity
import com.example.cooperativism.vote.service.converters.toResponse
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

internal class AgendaControllerDocTest : BaseTest() {
    @MockBean
    private lateinit var agendaService: AgendaService

    private val gson: Gson = Gson()

    companion object {
        private val agendaId = UUID.randomUUID().toString()
    }

    @Test
    fun docCreateAgenda() {
        val agendaRequest = CreateAgendaRequest(
            name = "Agenda 01",
            description = "some Description"
        )

        val json = gson.toJson(agendaRequest)
        whenever(agendaService.createAgenda(agendaRequest)).thenReturn(
            agendaRequest.toEntity().copy(id = agendaId).toResponse()
        )

        mockMvc.perform(post("/v1/agenda").contentType(MediaType.APPLICATION_JSON).content(json))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    @Test
    fun docCreateSession() {
        val sessionRequest = CreateSessionRequest()
        val sessionJson = gson.toJson(sessionRequest)

        whenever(agendaService.createSession(agendaId, sessionRequest)).thenReturn(
            sessionRequest.toEntity(agendaId).toResponse()
        )
        mockMvc.perform(
            post("/v1/agenda/$agendaId/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sessionJson)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }


    @Test
    fun docCreateVote() {
        val voteRequest = ComputeVoteRequest(cpf = "12611324670", vote = VoteEnum.SIM)
        val voteJson = gson.toJson(voteRequest)

        whenever(agendaService.computeVote(agendaId, voteRequest)).thenReturn(
            voteRequest.toEntity(agendaId).toResponse()
        )

        mockMvc.perform(
            post("/v1/agenda/$agendaId/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(voteJson)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    @Test
    fun docGetResult() {
        whenever(agendaService.computeResult(agendaId)).thenReturn(
            ResultResponse(
                agendaId = agendaId,
                votes = mapOf(VoteEnum.SIM to 51.toBigInteger(), VoteEnum.NAO to 49.toBigInteger()),
                result = ResultEnum.APROVADO
            )
        )
        mockMvc.perform(get("/v1/agenda/$agendaId/result"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }
}
