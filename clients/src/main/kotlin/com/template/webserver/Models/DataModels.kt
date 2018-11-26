package com.template.webserver.Models

import com.loannetwork.base.state.LoanState
import net.corda.core.contracts.StateAndRef

// Data Entities
data class LoanRecordResults(val totalRecords:Long,val records: List<StateAndRef<LoanState>>)
data class RecordCount(val status: String, val count:Long)
data class ConsolidatedRecordCount(val recordCounts: List<RecordCount>)