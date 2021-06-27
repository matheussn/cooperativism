package com.example.cooperativism.session.controller.representation

import java.math.BigInteger
import java.sql.Timestamp

data class CreateSessionResponse(
    val id: String,
    val name: String,
    val description: String,
    val duration: BigInteger,
    val endsAt: Timestamp
)
