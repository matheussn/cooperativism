package com.example.cooperativism.session.controller.representation

import java.math.BigInteger

data class CreateSessionRequest(
    val name: String,
    val description: String,
    val duration: BigInteger? = null
)