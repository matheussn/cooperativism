package com.example.cooperativism.agenda.controller

import com.example.cooperativism.agenda.controller.request.ComputeVoteRequest
import com.example.cooperativism.agenda.controller.request.CreateAgendaRequest
import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.ComputeVoteResponse
import com.example.cooperativism.agenda.controller.response.CreateAgendaResponse
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.agenda.service.AgendaService
import com.example.cooperativism.vote.service.ResultResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/v1/agenda")
@RestController
class AgendaController(
    private val agendaService: AgendaService
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createAgenda(@RequestBody request: CreateAgendaRequest): CreateAgendaResponse {
        return agendaService.createAgenda(request)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{agendaId}/session")
    fun createSession(
        @PathVariable agendaId: String,
        @Valid @RequestBody(required = false) request: CreateSessionRequest?
    ): CreateSessionResponse {
        return agendaService.createSession(agendaId, request)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{agendaId}/vote")
    fun computeVote(
        @PathVariable agendaId: String,
        @Valid @RequestBody request: ComputeVoteRequest
    ): ComputeVoteResponse {
        return agendaService.computeVote(agendaId, request)
    }

    @GetMapping("{agendaId}/result")
    fun getResult(@PathVariable agendaId: String): ResultResponse {
        return agendaService.computeResult(agendaId)
    }
}
