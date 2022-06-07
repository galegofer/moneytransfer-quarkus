package org.galegofer.moneytransfer.mapper

import org.galegofer.moneytransfer.domain.MoneyTransfer
import org.galegofer.moneytransfer.domain.payload.MoneyTransferRequestPayload
import org.mapstruct.Mapper

@Mapper(componentModel = "cdi")
interface MoneyTransferMapper {
    fun payloadToEntity(payload: MoneyTransferRequestPayload): MoneyTransfer
    fun entityToPayload(entity: MoneyTransfer): MoneyTransferRequestPayload
}
