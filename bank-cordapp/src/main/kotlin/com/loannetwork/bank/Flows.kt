package com.loannetwork.bank

import co.paralleluniverse.fibers.Suspendable
import com.loannetwork.base.LoanStateContract
import com.loannetwork.base.LoanStateContract.Companion.LoanStateContractID
import com.loannetwork.base.NodeIdentity
import com.loannetwork.base.model.LoanStateModel
import com.loannetwork.base.model.LoanStatus
import com.loannetwork.base.state.LoanState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.serialization.SerializationWhitelist
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant
import java.util.*

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class RecieveApplication(val loanApp: LoanStateModel) : FlowLogic<Unit>() {
    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new Loan App.")
        object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying model constraints.")
        object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
        object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }

        object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        )

    }
    override val progressTracker = tracker()

    @Suspendable
    override fun call() {
        val notary = NodeIdentity.getNotary(serviceHub)
        val txBuilder = TransactionBuilder(notary = notary)
        progressTracker.currentStep = GENERATING_TRANSACTION
        val uniqueIdentifier= UniqueIdentifier(loanApp.applicant)

        val outputState = LoanState(LoanStatus.RECIEVED.toString(), listOf(ourIdentity),loanApp.applicant, loanApp.builderName, loanApp.propertyName, loanApp.address, loanApp.loanAmount, uniqueIdentifier, loanApp.appliedDate?: Date.from(Instant.now()),loanApp.updatedDate?: Date.from(Instant.now())  )
        val outputContractAndState = StateAndContract(outputState, LoanStateContractID)
        val cmd = Command(LoanStateContract.RecieveApplication(), listOf(ourIdentity.owningKey))

        // We add the items to the builder.
        txBuilder.withItems(outputContractAndState, cmd)
        progressTracker.currentStep = VERIFYING_TRANSACTION
        // Verifying the transaction.
        txBuilder.verify(serviceHub)
        progressTracker.currentStep = SIGNING_TRANSACTION

        // Signing the transaction.
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        // progressTracker.currentStep = ScreenFlow.Companion.GATHERING_SIGS
        subFlow(FinalityFlow(signedTx))
    }
}
class SerializationWhiteList : SerializationWhitelist {

    override val whitelist: List<Class<*>> = listOf(java.sql.Date::class.java, java.util.Date::class.java,
            Instant::class.java)

}


@InitiatedBy(RecieveApplication::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}
