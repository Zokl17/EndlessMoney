package com.example.endlessmoney.domain.usecase

import com.example.endlessmoney.domain.repository.FinanceRepository

class GetAccountUseCase(
    private val repository: FinanceRepository
) {
    operator fun invoke() = repository.getAccount()
}