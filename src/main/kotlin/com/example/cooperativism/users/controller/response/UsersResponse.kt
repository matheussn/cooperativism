package com.example.cooperativism.users.controller.response

data class UsersResponse(
    val status: UserEnum
)

enum class UserEnum { ABLE_TO_VOTE, UNABLE_TO_VOTE }
