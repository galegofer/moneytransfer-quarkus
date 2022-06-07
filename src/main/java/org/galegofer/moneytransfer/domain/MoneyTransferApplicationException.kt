package org.galegofer.moneytransfer.domain

class MoneyTransferApplicationException(message: String, val statusCode: Int) :
    RuntimeException(message)
