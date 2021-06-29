package com.example.cooperativism.result

import com.example.cooperativism.vote.VoteEnum
import java.math.BigInteger
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class VoteResult(
    @Id
    val id: String,
    @Enumerated(EnumType.STRING)
    val vote: VoteEnum,
    val total: BigInteger
)