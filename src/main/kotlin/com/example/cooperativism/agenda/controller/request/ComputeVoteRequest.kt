package com.example.cooperativism.agenda.controller.request

import com.example.cooperativism.vote.VoteEnum
import javax.validation.constraints.Pattern

data class ComputeVoteRequest(
    @field:Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 caracteres numéricos")
    val cpf: String,
    val vote: VoteEnum
)
