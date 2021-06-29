package com.example.cooperativism.agenda.repository

import com.example.cooperativism.agenda.Agenda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AgendaRepository : JpaRepository<Agenda, String>