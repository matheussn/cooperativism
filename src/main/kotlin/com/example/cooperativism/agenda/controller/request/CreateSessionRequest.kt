package com.example.cooperativism.agenda.controller.request

import org.hibernate.validator.constraints.Range
import java.math.BigInteger

data class CreateSessionRequest(
    @field:[Range(min = 1, message = "Este campo deve ser maior do que 0")]
    val duration: BigInteger? = null
)