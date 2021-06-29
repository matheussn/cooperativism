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
    @Enumerated(EnumType.STRING)
    val vote: VoteEnum,
    val id: String,
    val total: BigInteger
)