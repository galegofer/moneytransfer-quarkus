package org.galegofer.moneytransfer.domain.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class AccountPayload(
    val accountId: @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$") String,
    val currency: @NotBlank @Pattern(regexp = "^[a-zA-Z\\s]*$") String,
    val balance: Double
)
