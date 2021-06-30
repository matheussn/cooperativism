package com.example.cooperativism.users.controller

import com.example.cooperativism.users.controller.response.UserEnum
import com.example.cooperativism.users.controller.response.UsersResponse
import com.example.cooperativism.validcpf.ValidCpfResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/users")
@RestController
class UserController(
    private val validCpfResource: ValidCpfResource
) {
    @GetMapping("/{cpf}")
    fun isAbleToVote(@PathVariable("cpf") cpf: String): UsersResponse =
        UsersResponse(
            status = if (validCpfResource.validCpf(cpf).isValid) UserEnum.ABLE_TO_VOTE else UserEnum.UNABLE_TO_VOTE
        )
}