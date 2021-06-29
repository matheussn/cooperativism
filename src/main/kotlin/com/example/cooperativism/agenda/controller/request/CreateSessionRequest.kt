package com.example.cooperativism.agenda.controller.request

import java.math.BigInteger

data class CreateSessionRequest(
    val duration: BigInteger? = null
)