package com.example.cooperativism.exceptions

import java.util.*

data class ErrorsDetails(
    val time: Date,
    val details: String
)

data class SessionToVoteNotFound(
    val detail: String
) : Exception()

data class SessionHasAlreadyEnded(
    val detail: String
) : Exception()

data class VoteForThisSessionAlreadyComputed(
    val detail: String
) : Exception()

data class AgendaWithoutSession(
    val detail: String
) : Exception()