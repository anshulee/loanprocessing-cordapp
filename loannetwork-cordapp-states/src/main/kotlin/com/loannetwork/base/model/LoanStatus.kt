package com.loannetwork.base.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class LoanStatus{
    RECIEVED,APPROVED,PROCESSING,DENIED
}