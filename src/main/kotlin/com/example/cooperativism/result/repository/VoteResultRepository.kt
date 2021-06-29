package com.example.cooperativism.result.repository

import com.example.cooperativism.result.VoteResult
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.repository.Repository as ReadOnlyRepository

@Repository
interface VoteResultRepository : ReadOnlyRepository<VoteResult, String> {
    @Query(
        value = """
            select v.vote as vote, v.agenda_id as id,count(*) as total
            from vote v
            where v.agenda_id = :agenda_id
            group by v.vote, v.agenda_id
        """,
        nativeQuery = true
    )
    fun getVoteResult(@Param("agenda_id") agendaId: String): List<VoteResult>
}