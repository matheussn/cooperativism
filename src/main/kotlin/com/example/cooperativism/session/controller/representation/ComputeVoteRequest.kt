package com.example.cooperativism.session.controller.representation

import com.example.cooperativism.vote.VoteEnum

data class ComputeVoteRequest(
    val cpf: String,
    val vote: VoteEnum
)
