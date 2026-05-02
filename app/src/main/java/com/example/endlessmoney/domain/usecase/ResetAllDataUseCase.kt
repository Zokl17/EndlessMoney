package com.example.endlessmoney.domain.usecase

import com.example.endlessmoney.domain.repository.FinanceRepository

class ResetAllDataUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke() = repository.resetAllData()
}