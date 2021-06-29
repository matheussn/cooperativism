package com.example.cooperativism.vote.service

import com.example.cooperativism.vote.VoteEnum
import java.math.BigInteger

data class ResultResponse(
    val agendaId: String,
    val votes: Map<VoteEnum, BigInteger>,
    val result: ResultEnum
)

enum class ResultEnum {
    APROVADO, REPROVADO
}
