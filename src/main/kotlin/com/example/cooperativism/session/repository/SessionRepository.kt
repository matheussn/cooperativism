package com.example.cooperativism.session.repository

import com.example.cooperativism.session.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SessionRepository : JpaRepository<Session, String> {
    fun findByAgendaId(agendaId: String): Optional<Session>
}
