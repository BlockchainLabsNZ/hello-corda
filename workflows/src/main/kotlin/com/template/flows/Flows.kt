package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TemplateContract
import com.template.states.IOUState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
class IOUFlow(val iouValue: Int,
              val otherParty: Party) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        val outputState = IOUState(iouValue, ourIdentity, otherParty)
        val command = Command(TemplateContract.Commands.Action(), ourIdentity.owningKey)

        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, TemplateContract.ID)
                .addCommand(command)

        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        val otherPartySessions = initiateFlow(otherParty)

        subFlow(FinalityFlow(signedTx, otherPartySessions))
    }
}

@InitiatedBy(IOUFlow::class)
class IOUResponderFlow(private val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherPartySession))
    }
}
