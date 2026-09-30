package com.example.myapplication.presentation.presentation.viewmodel.uistate

import com.example.myapplication.domain.model.Money

data class DashboardUiState(
    val isLoading: Boolean = true,
    val draftCount: Int = 0,
    val openCount: Int = 0,
    val overdueCount: Int = 0,
    val paidCount: Int = 0,
    val revenueThisMonth: Money = Money.ZERO,
    val errorMessage: String? = null
)