package com.example.endlessmoney.data.mapper

import com.example.endlessmoney.data.local.entity.AccountEntity
import com.example.endlessmoney.domain.model.Account

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        balance = balance
    )
}