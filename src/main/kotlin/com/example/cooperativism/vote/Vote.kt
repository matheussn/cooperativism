package com.example.cooperativism.vote

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Vote(
    @Id
    val id: String,
    val sessionId: String,
    val cpf: String,
    val vote: VoteEnum
)

enum class VoteEnum {
    SIM, NAO
}
