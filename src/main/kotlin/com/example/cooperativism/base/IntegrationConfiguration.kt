package com.example.cooperativism.base

import com.example.cooperativism.validcpf.ValidCpfResource
import feign.Contract
import feign.Feign
import feign.Request
import feign.Retryer
import feign.codec.Decoder
import feign.codec.Encoder
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.concurrent.TimeUnit

@Configuration
@Import(FeignClientsConfiguration::class)
class IntegrationConfiguration(
    private val feignEncoder: Encoder,
    private val feignDecoder: Decoder,
    private val feignContract: Contract
) {
    @Bean
    fun validCpfResource(): ValidCpfResource =
        Feign.builder()
            .encoder(feignEncoder)
            .decoder(feignDecoder)
            .contract(feignContract)
            .retryer(Retryer.NEVER_RETRY)
            .options(Request.Options(2, TimeUnit.MINUTES, 2, TimeUnit.MINUTES, false))
            .target(ValidCpfResource::class.java, "https://cpf-validate.herokuapp.com/")
}