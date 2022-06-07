package org.galegofer.moneytransfer.domain

class MoneyTransfer(
    val currency: String,
    val amount: Double,
    val sourceAccount: String,
    val targetAccount: String
)
