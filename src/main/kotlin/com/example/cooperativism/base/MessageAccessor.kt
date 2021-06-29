package com.example.cooperativism.base

import com.example.cooperativism.CooperativismMessage
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageAccessor(
    private val messageSource: MessageSource
) {
    fun getMessage(code: String?, params: List<Any> = emptyList()): String {
        return try {
            buildMessage(code, params)
                .let {
                    if (it == code) {
                        return buildMessage(CooperativismMessage.UNEXPECTED_ERROR)
                    }
                    return it
                }
        } catch (e: NoSuchMessageException) {
            "Message: $code: $params"
        }
    }

    private fun buildMessage(code: String?, params: List<Any> = emptyList()): String {
        return messageSource.getMessage(
            code ?: "",
            parseParams(params),
            Locale("pt", "BR")
        )
    }

    private fun parseParams(params: List<Any>): Array<Any> {
        if (params.isEmpty()) {
            return emptyArray()
        }
        return Array(params.size) { params[it] }
    }
}