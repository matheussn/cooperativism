package com.example.cooperativism.vote.repository

import com.example.cooperativism.vote.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VoteRepository : JpaRepository<Vote, String> {
    fun findByCpfAndAgendaId(cpf: String, agendaId: String): Optional<Vote>
}
