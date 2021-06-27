package com.example.cooperativism.exceptions

data class BadRequestException(
    private val msg: String
) : Exception(msg)