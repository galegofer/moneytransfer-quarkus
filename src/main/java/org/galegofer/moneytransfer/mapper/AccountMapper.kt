package org.galegofer.moneytransfer.mapper

import org.galegofer.moneytransfer.domain.Account
import org.galegofer.moneytransfer.domain.payload.AccountPayload
import org.mapstruct.Mapper

@Mapper(componentModel = "cdi")
interface AccountMapper {
    fun payloadToEntity(payload: AccountPayload): Account
    fun entityToPayload(entity: Account): AccountPayload
}
