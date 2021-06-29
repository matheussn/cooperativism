package com.example.cooperativism.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*
import java.util.function.Consumer


@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [SessionToVoteNotFound::class])
    fun handlerNotfoundSessionToVote(ex: SessionToVoteNotFound): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(Date(), ex.detail)
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [SessionHasAlreadyEnded::class])
    fun handlerSessionHasAlreadyEnded(ex: SessionHasAlreadyEnded): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(Date(), ex.detail)
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [VoteForThisSessionAlreadyComputed::class])
    fun handlerSessionHasAlreadyEnded(ex: VoteForThisSessionAlreadyComputed): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(Date(), ex.detail)
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [AgendaWithoutSession::class])
    fun handlerAgendaWithoutSession(ex: AgendaWithoutSession): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(Date(), ex.detail)
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handler(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        })

        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }
}