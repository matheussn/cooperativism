package com.example.cooperativism

import capital.scalable.restdocs.AutoDocumentation
import capital.scalable.restdocs.jackson.JacksonResultHandlers
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.cli.CliDocumentation
import org.springframework.restdocs.http.HttpDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@TestPropertySource(locations = ["classpath:application-test-integration.properties"])
@Extensions(ExtendWith(SpringExtension::class), ExtendWith(RestDocumentationExtension::class))
@SpringBootTest
abstract class BaseTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentationContextProvider: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo<DefaultMockMvcBuilder>(JacksonResultHandlers.prepareJackson(objectMapper))
            .alwaysDo<DefaultMockMvcBuilder>(
                document(
                    "{class-name}/{method-name}",
                    Preprocessors.preprocessRequest(
                        ResponseModifyingPreprocessors.replaceBinaryContent(),
                        ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                        Preprocessors.prettyPrint()
                    ),
                    Preprocessors.preprocessResponse(
                        ResponseModifyingPreprocessors.replaceBinaryContent(),
                        ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                        Preprocessors.prettyPrint()
                    )
                )
            )
            .apply<DefaultMockMvcBuilder>(
                documentationConfiguration(restDocumentationContextProvider)
                    .uris()
                    .withScheme("http")
                    .withHost("localhost")
                    .withPort(8080)
                    .and().snippets()
                    .withDefaults(
                        CliDocumentation.curlRequest(),
                        HttpDocumentation.httpRequest(),
                        HttpDocumentation.httpResponse(),
                        AutoDocumentation.requestFields(),
                        AutoDocumentation.responseFields(),
                        AutoDocumentation.pathParameters(),
                        AutoDocumentation.requestParameters(),
                        AutoDocumentation.description(),
                        AutoDocumentation.methodAndPath(),
                        AutoDocumentation.section()
                    )
            )
            .build()
    }
}
