package com.example.cooperativism.base

import com.example.cooperativism.exceptions.BaseException
import com.example.cooperativism.exceptions.BusinessException
import com.example.cooperativism.exceptions.ErrorsDetails
import com.example.cooperativism.exceptions.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class BaseExceptionHandler(
    private val messageAccessor: MessageAccessor
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(BaseExceptionHandler::class.java)
    }

    @ExceptionHandler(value = [BusinessException::class])
    fun businessExceptionHandler(ex: BusinessException): ResponseEntity<ErrorsDetails> {
        return buildResponse(ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [NotFoundException::class])
    fun notFoundExceptionHandler(ex: NotFoundException): ResponseEntity<ErrorsDetails> {
        return buildResponse(ex, HttpStatus.NOT_FOUND)
    }

    private fun buildResponse(ex: BaseException, status: HttpStatus): ResponseEntity<ErrorsDetails> {
        val realMessage = getMessage(ex.message, ex.params)
        log.error("[EXCEPTION-HANDLER] Erro Mapeado: ${ex.message} Status Http: $status | Mensagem do erro $realMessage")
        return ResponseEntity(ErrorsDetails(Date(), ex.message, realMessage), status)
    }

    private fun getMessage(code: String?, params: List<Any> = emptyList()) = messageAccessor.getMessage(code, params)

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