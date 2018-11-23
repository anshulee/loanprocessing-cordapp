package com.template.webserver

import com.loannetwork.bank.RecieveApplication
import com.loannetwork.base.model.LoanStateModel
import net.corda.core.internal.randomOrNull
import net.corda.core.messaging.startFlow
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.ws.rs.core.Response


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
    private fun generateApplicationData():Response  {
        generateData()
      for( record in loanApplicationsList)
      {
            proxy.startFlow( ::RecieveApplication, record )
        }
        return Response.ok().build()
    }
}