package com.example.cooperativism.users.controller

import com.example.cooperativism.BaseTest
import com.example.cooperativism.users.controller.response.UserEnum
import com.example.cooperativism.users.controller.response.UsersResponse
import com.example.cooperativism.validcpf.ValidCpfResource
import com.example.cooperativism.validcpf.ValidCpfResponse
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

internal class UserControllerTest : BaseTest() {
    @MockBean
    private lateinit var validCpfResource: ValidCpfResource

    private val gson: Gson = Gson()

    @Test
    fun shouldCallValidCpfResourceWithAValidCpf() {
        val validCpf = "12611324670"
        whenever(validCpfResource.validCpf(validCpf)).thenReturn(
            ValidCpfResponse(cpf = validCpf, isValid = true)
        )

        val result = mockMvc.perform(get("/v1/users/$validCpf"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response
            .contentAsString

        val objResult = gson.fromJson(result, UsersResponse::class.java)

        assert(objResult.status == UserEnum.ABLE_TO_VOTE)
    }

    @Test
    fun shouldCallValidCpfResourceWithAInvalidCpf() {
        val validCpf = "12611324671"
        whenever(validCpfResource.validCpf(validCpf)).thenReturn(
            ValidCpfResponse(cpf = validCpf, isValid = false)
        )

        val result = mockMvc.perform(get("/v1/users/$validCpf"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response
            .contentAsString

        val objResult = gson.fromJson(result, UsersResponse::class.java)

        assert(objResult.status == UserEnum.UNABLE_TO_VOTE)
    }
}