package org.galegofer.moneytransfer.service.impl

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.converters.uni.UniReactorConverters
import org.galegofer.moneytransfer.domain.Account
import org.galegofer.moneytransfer.domain.MoneyTransfer
import org.galegofer.moneytransfer.domain.MoneyTransferApplicationException
import org.galegofer.moneytransfer.service.AccountTransferService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.function.Function
import javax.transaction.Transactional

@Service
class AccountTransferServiceImpl : AccountTransferService {

    private val log: Logger = LoggerFactory.getLogger(AccountTransferServiceImpl::class.java)

    @Transactional
    override fun transferMoneyFromAccountToAnotherAccount(moneyTransfer: MoneyTransfer): Uni<Int> {
        log.info(
            "Calling transfer money from account id: {} to account id: {} for amount: {}",
            moneyTransfer.sourceAccount, moneyTransfer.targetAccount, moneyTransfer.amount
        )

        // val sourceCall = Account.findByAccountId(moneyTransfer.sourceAccount)
        //     .onItem().ifNull().failWith {
        //         MoneyTransferApplicationException(
        //             message = String.format(
        //                 "Source account with id: %s, doesn't exist",
        //                 moneyTransfer.sourceAccount
        //             ),
        //             statusCode = NOT_FOUND.value()
        //         )
        //     }.toMulti().filter { it.balance >= moneyTransfer.amount }
        //     .toUni()
        //     .onItem().ifNull().failWith {
        //         MoneyTransferApplicationException(
        //             message = String.format(
        //                 "Insufficient funds at Source account with id: %s",
        //                 moneyTransfer.sourceAccount
        //             ),
        //             statusCode = BAD_REQUEST.value()
        //         )
        //     }.onItem().invoke { account: Account ->
        //         log.info(
        //             "Got source account with id: {} and balance: {}",
        //             account.accountId,
        //             account.balance
        //         )
        //     }
        //
        // val targetCall = Account.findByAccountId(moneyTransfer.targetAccount)
        //     .onItem().ifNull().failWith {
        //         MoneyTransferApplicationException(
        //             message = String.format(
        //                 "Target account with id: %s, doesn't exist",
        //                 moneyTransfer.targetAccount
        //             ),
        //             statusCode = NOT_FOUND.value()
        //         )
        //     }.onItem().invoke { account: Account ->
        //         log.info(
        //             "Got target account with id: {} and balance: {}",
        //             account.accountId,
        //             account.balance
        //         )
        //     }

        // Uni.combine().all().unis(sourceCall,targetCall)
        //     .asTuple()
        //     .map {  }
        //
        // accountRepository.getByAccountId(moneyTransfer.targetAccount)
        //     .switchIfEmpty(
        //         Mono.error(
        //             MoneyTransferApplicationException(
        //                 message = String.format(
        //                     "Target account with id: %s, doesn't exist",
        //                     moneyTransfer.targetAccount
        //                 ),
        //                 statusCode = NOT_FOUND.value()
        //             )
        //         )
        //     )
        //     .doOnSuccess {
        //         log.info(
        //             "Got target account with id: {} and balance: {}",
        //             it.accountId,
        //             it.balance
        //         )
        //     },

        val sourceCall = Mono.from(
            Account.findByAccountId(moneyTransfer.sourceAccount)
                .convert().with(UniReactorConverters.toMono())
        )

        return Uni.createFrom().publisher(
            sourceCall
                .switchIfEmpty(
                    Mono.error(
                        MoneyTransferApplicationException(
                            message = String.format(
                                "Source account with id: %s, doesn't exist",
                                moneyTransfer.sourceAccount
                            ),
                            statusCode = NOT_FOUND.value()
                        )
                    )
                )
                .filter { it.balance >= moneyTransfer.amount }
                .switchIfEmpty(
                    Mono.error(
                        MoneyTransferApplicationException(
                            message = String.format(
                                "Insufficient funds at Source account with id: %s",
                                moneyTransfer.sourceAccount
                            ),
                            statusCode = BAD_REQUEST.value()
                        )
                    )
                )
                .doOnSuccess {
                    log.info(
                        "Got source account with id: {} and balance: {}",
                        it.accountId,
                        it.balance
                    )
                }
                .zipWith(

                    Mono.from(
                        Account.findByAccountId(moneyTransfer.targetAccount)
                            .convert().with(UniReactorConverters.toMono())
                    )
                        .switchIfEmpty(
                            Mono.error(
                                MoneyTransferApplicationException(
                                    message = String.format(
                                        "Target account with id: %s, doesn't exist",
                                        moneyTransfer.targetAccount
                                    ),
                                    statusCode = NOT_FOUND.value()
                                )
                            )
                        )
                        .doOnSuccess {
                            log.info(
                                "Got target account with id: {} and balance: {}",
                                it.accountId,
                                it.balance
                            )
                        },
                    applyDiscountToSourceAccountAndAddToTarget(moneyTransfer)
                )
                .doOnSuccess { log.info("Updated all balances...") }
                .flatMap(Function.identity())
        )
    }

    override fun getAccountDetailsByAccountId(accountId: String): Uni<Account> =
        Uni.createFrom().publisher(
            Mono.from(
                Account.findByAccountId(accountId)
                    .convert().with(UniReactorConverters.toMono())
            )
                .switchIfEmpty(
                    Mono.error(
                        MoneyTransferApplicationException(
                            message = String.format("Account with id: %s, doesn't exist", accountId),
                            statusCode = NOT_FOUND.value()
                        )
                    )
                )
        )

    private fun applyDiscountToSourceAccountAndAddToTarget(moneyTransfer: MoneyTransfer): (Account, Account) -> Mono<Int> =
        { sourceAccount: Account, targetAccount: Account ->
            Mono.from(
                Account.updateAmount(sourceAccount.accountId, sourceAccount.balance - moneyTransfer.amount)
                    .convert().with(UniReactorConverters.toMono())
            )
                .doOnSuccess {
                    log.info(
                        "Subtracted amount: {} for source account id: {} and balance: {}",
                        moneyTransfer.amount,
                        sourceAccount.accountId,
                        sourceAccount.balance
                    )
                }
                .flatMap {
                    Account.updateAmount(targetAccount.accountId, targetAccount.balance + moneyTransfer.amount)
                        .convert().with(UniReactorConverters.toMono())
                        .doOnSuccess {
                            log.info(
                                "Added amount: {} for target account id: {}",
                                moneyTransfer.amount,
                                moneyTransfer.targetAccount
                            )
                        }
                }
        }
}
