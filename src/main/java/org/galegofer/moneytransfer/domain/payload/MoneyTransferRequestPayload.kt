package org.galegofer.moneytransfer.domain.payload

import org.galegofer.moneytransfer.domain.validator.ValidCurrencyCode
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class MoneyTransferRequestPayload(
    @ValidCurrencyCode
    @NotBlank @Pattern(regexp = "^[a-zA-Z\\s]*$")
    val currency: String,

    @Min(1)
    val amount: Double,

    @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$")
    val sourceAccount: String,

    @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$")
    val targetAccount: String
)
