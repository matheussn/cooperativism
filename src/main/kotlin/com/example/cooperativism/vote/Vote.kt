package com.example.cooperativism.vote

import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class Vote(
    @Id
    val id: String,
    val agendaId: String,
    val cpf: String,
    @Enumerated(EnumType.STRING)
    val vote: VoteEnum,
    val createdAt: Timestamp
)

enum class VoteEnum { SIM, NAO }
