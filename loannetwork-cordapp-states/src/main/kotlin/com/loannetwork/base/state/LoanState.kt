package com.loannetwork.base.state

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import net.corda.core.serialization.SerializationWhitelist
import java.time.Instant
import java.util.*

@CordaSerializable
data class LoanState (val status: String, val parties:List<Party>, val applicant:String, val builderName:String, val propertyName:String, val address:String, val loanAmount:Float, override val linearId: UniqueIdentifier, val appliedDate: Date, val updatedDate: Date = Date.from(Instant.now()), val isAuthRequired:Boolean?=null) : LinearState, QueryableState {
    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(LoanStateSchemaV1)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        return when (schema) {
            is LoanStateSchemaV1 -> LoanStateSchemaV1.PersistentLoanState(
                    this.status, this.applicant, this.builderName, this.propertyName, this.address, this.loanAmount, this.appliedDate, this.updatedDate, this.isAuthRequired, this.linearId.id)

            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }

    }

    override val participants get() = parties
}


