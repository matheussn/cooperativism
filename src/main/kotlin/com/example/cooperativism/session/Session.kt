package com.example.cooperativism.session

import java.math.BigInteger
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Session(
    @Id
    val id: String,
    val createdAt: Timestamp,
    val endsAt: Timestamp,
    val duration: BigInteger,
    val agendaId: String,
)