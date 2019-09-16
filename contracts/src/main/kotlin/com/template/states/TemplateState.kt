package com.template.states

import com.template.contracts.TemplateContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(TemplateContract::class)
class IOUState(val value: Int,
               val lender: Party,
               val borrower: Party) : ContractState {
    override val participants: List<AbstractParty>
        get() = listOf(lender, borrower)
}
