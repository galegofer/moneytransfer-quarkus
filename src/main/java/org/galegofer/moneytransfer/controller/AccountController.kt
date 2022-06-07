package org.galegofer.moneytransfer.controller

import io.smallrye.mutiny.Uni
import org.galegofer.moneytransfer.domain.payload.AccountPayload
import org.galegofer.moneytransfer.domain.payload.MoneyTransferRequestPayload
import org.galegofer.moneytransfer.mapper.AccountMapper
import org.galegofer.moneytransfer.mapper.MoneyTransferMapper
import org.galegofer.moneytransfer.service.AccountTransferService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountTransferService: AccountTransferService,
    private val moneyTransferMapper: MoneyTransferMapper,
    private val accountMapper: AccountMapper
) {
    private val log: Logger = LoggerFactory.getLogger(AccountController::class.java)

    @PostMapping("/transfer")
    fun transferFundsToAccount(@RequestBody payload: @Valid MoneyTransferRequestPayload): Uni<ResponseEntity<Void>> =
        log.info(
            "Received request to make transfer from account id: {} to account id: {} for amount: {}",
            payload.sourceAccount, payload.targetAccount, payload.amount
        ).let {
            accountTransferService.transferMoneyFromAccountToAnotherAccount(
                moneyTransferMapper.payloadToEntity(payload)
            ).map {
                ResponseEntity.status(OK)
                    .build()
            }
        }

    @GetMapping("/{id}")
    fun getAccountDetails(@PathVariable("id") id: @NotBlank @Size(max = 100) @Pattern(regexp = "^[a-zA-Z\\d\\s]*$") String): Uni<AccountPayload> =
        log.info("Received request to get details for account id: {}", id)
            .let {
                accountTransferService.getAccountDetailsByAccountId(id)
                    .map { accountMapper.entityToPayload(it) }
            }
}
