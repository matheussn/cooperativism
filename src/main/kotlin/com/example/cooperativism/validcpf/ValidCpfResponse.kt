package com.example.cooperativism.validcpf

data class ValidCpfResponse(
    val cpf: String,
    val isValid: Boolean
)
