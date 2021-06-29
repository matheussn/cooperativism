package com.example.cooperativism.exceptions

import java.util.*

data class ErrorsDetails(
    val time: Date,
    val details: String
)

data class BusinessException(
    val detail: String
) : Exception()