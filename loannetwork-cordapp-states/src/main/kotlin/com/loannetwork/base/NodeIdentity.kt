package com.loannetwork.base
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub

class NodeIdentity
{
    companion object {
        val Oracle = CordaX500Name("Bank", "London","GB")
        val Payer = CordaX500Name("Builder", "New York","US")
        val Notary = CordaX500Name("Notary", "London","GB")


        fun getNotary(serviceHub : ServiceHub) : Party? {
            //return serviceHub.networkMapCache.getNotary(NodeIdentity.Notary)
            return serviceHub.networkMapCache.notaryIdentities[0]
        }

        fun getPartyFromNetwork(serviceHub : ServiceHub, cordaX500Name : CordaX500Name) : Party {

            val party = serviceHub.networkMapCache.getNodeByLegalName(cordaX500Name)?.legalIdentities?.first()
                    ?: throw IllegalArgumentException("Requested party node $cordaX500Name not found on network.")

            return party
        }
    }
}