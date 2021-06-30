package com.example.cooperativism

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CooperativismApplication

fun main(args: Array<String>) {
    runApplication<CooperativismApplication>(*args)
}