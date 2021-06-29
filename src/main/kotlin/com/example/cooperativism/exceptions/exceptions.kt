package com.example.cooperativism.exceptions

import java.util.Date

data class ErrorsDetails(
    val time: Date,
    val code: String?,
    val details: String
)

open class BaseException(
    detail: String,
    val params: List<Any> = emptyList()
) : RuntimeException(detail)

class BusinessException(code: String, params: List<Any> = emptyList()) : BaseException(code, params)

class NotFoundException(code: String) : BaseException(code)