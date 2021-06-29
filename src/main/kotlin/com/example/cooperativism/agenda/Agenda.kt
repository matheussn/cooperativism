package com.example.cooperativism.agenda

import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Agenda(
    @Id
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Timestamp
)
