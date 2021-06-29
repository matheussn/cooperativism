package com.example.cooperativism.session.controller

import com.example.cooperativism.agenda.controller.request.CreateSessionRequest
import com.example.cooperativism.agenda.controller.response.CreateSessionResponse
import com.example.cooperativism.session.service.SessionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/session")
@RestController
class SessionController(
    private val sessionService: SessionService
) {
    @PostMapping
    fun createSession(@RequestBody request: CreateSessionRequest): CreateSessionResponse {
        return sessionService.createSession(request)
    }
}