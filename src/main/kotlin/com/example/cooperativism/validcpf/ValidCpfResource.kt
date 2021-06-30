package com.example.cooperativism.validcpf

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

interface ValidCpfResource {
    @GetMapping("/v1/valid-cpf/{cpf}")
    fun validCpf(@PathVariable("cpf") cpf: String): ValidCpfResponse
}