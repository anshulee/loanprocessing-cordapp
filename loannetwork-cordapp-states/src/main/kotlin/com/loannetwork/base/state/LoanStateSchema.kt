package com.loannetwork.base.state

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object LoanStateSchema

object LoanStateSchemaV1: MappedSchema(LoanStateSchema.javaClass,1, listOf(LoanState::class.java)) {
@Entity
@Table(name="loan_states")
class PersistentLoanState
(
        @Column(name="status")
        var status:String="",
        @Column(name="applicant")
        var applicant:String="",
        @Column(name="builderName")
        var builderName:String="",
        @Column(name="propertyName")
        var propertyName:String="",
        @Column(name="address")
        var address:String="",
        @Column(name="loanAmount")
        var loanAmount:Float=0F,
        @Column(name="appliedDate")
        var appliedDate: Date =  Date.from(Instant.now()),
        @Column(name="updatedDate")
        var updatedDate: Date =  Date.from(Instant.now()),
        @Column(name="isAuthorizationReq")
        var isAuthorizationRequired:Boolean?=null,
        @Column(name = "linear_id")
        var linearId: UUID
): PersistentState()
{
    constructor():this("","","","","",0F,Date.from(Instant.now()),Date.from(Instant.now()),null, UUID.randomUUID())
}



}
