package com.example.cooperativism.session.service

import com.example.cooperativism.session.controller.representation.ComputeVoteRequest
import com.example.cooperativism.session.controller.representation.CreateSessionRequest
import com.example.cooperativism.session.controller.representation.CreateSessionResponse

interface SessionService {
    fun createSession(request: CreateSessionRequest): CreateSessionResponse

    fun computeVote(sessionId: String, request: ComputeVoteRequest)
}