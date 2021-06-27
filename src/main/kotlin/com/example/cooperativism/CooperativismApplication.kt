package com.example.cooperativism

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.batch.BatchProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class CooperativismApplication

fun main(args: Array<String>) {
    runApplication<CooperativismApplication>(*args)
}