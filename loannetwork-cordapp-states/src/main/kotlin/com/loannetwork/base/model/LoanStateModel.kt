package com.loannetwork.base.model

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant
import java.util.*
@CordaSerializable
data class LoanStateModel(val applicationNumber:Int,val status: String, val applicant:String, val builderName:String, val propertyName:String, val address:String, val loanAmount:Float,  val appliedDate: Date?, val updatedDate: Date?)
