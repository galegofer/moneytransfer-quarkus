package org.galegofer.moneytransfer.service

import io.smallrye.mutiny.Uni
import org.galegofer.moneytransfer.domain.Account
import org.galegofer.moneytransfer.domain.MoneyTransfer

interface AccountTransferService {
    fun transferMoneyFromAccountToAnotherAccount(moneyTransfer: MoneyTransfer): Uni<Int>
    fun getAccountDetailsByAccountId(accountId: String): Uni<Account>
}
