package com.example.cooperativism.session.controller

import com.example.cooperativism.session.controller.representation.ComputeVoteRequest
import com.example.cooperativism.session.controller.representation.CreateSessionRequest
import com.example.cooperativism.session.controller.representation.CreateSessionResponse
import com.example.cooperativism.session.service.SessionService
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/session")
@RestController
class SessionController(
    private val sessionService: SessionService
) {
    @PostMapping
    fun createSession(@RequestBody request: CreateSessionRequest): CreateSessionResponse {
        return sessionService.createSession(request)
    }

    @PostMapping("{sessionId}/vote")
    fun vote(@PathVariable sessionId: String, @RequestBody request: ComputeVoteRequest) {
        return sessionService.computeVote(sessionId, request)
    }
}