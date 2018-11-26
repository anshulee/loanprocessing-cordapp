package com.template.webserver

import com.loannetwork.bank.RecieveApplication
import com.loannetwork.base.model.LoanStateModel
import com.loannetwork.base.state.LoanState
import net.corda.core.contracts.StateAndRef
import net.corda.core.internal.randomOrNull
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {
    private val loanApplicationsList= mutableListOf<LoanStateModel>()
    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    private fun generateData()
    {
        var recordCount=0
        while(recordCount<100) {
            recordCount= recordCount+1
            val record = LoanStateModel("RECIEVED", applicant = recordCount.toString(), builderName = "BB", propertyName = "CC", address = "DD", loanAmount = 8000F, appliedDate = null, updatedDate = null)
            loanApplicationsList.add(record)
        }
    }
    @GetMapping(value = "/generateData", produces = arrayOf("application/json"))
    private fun generateApplicationData(): ResponseEntity<String> {
        //dummy data generated for sample
        generateData()
      for( record in loanApplicationsList)
      {
            proxy.startFlow( ::RecieveApplication, record )
        }
       return ResponseEntity.ok("Generation Complete")
    }

    @GetMapping(value="/gethistory/{id}", produces= arrayOf("application/json"))
    private fun getApplicationData(@PathVariable("id") id:String ): ResponseEntity<List<StateAndRef<LoanState>>>
    {
        //External ID can be used to identify a record. Ideally have unique external IDs for your records
        // By default Linear State query will give only unconsumed state. Since we want the entire history set ALL
        val stateQuery: QueryCriteria.LinearStateQueryCriteria= QueryCriteria.LinearStateQueryCriteria(externalId = listOf(id),status = Vault.StateStatus.ALL)

        //If paging parameter is not passed it throws an exception if total records returned is more than the
        //default page size . Default page size=200.
        val  results= proxy.vaultQueryBy<LoanState>(stateQuery)
        val statesInVault= results.states
        return ResponseEntity.ok(statesInVault)
    }


}