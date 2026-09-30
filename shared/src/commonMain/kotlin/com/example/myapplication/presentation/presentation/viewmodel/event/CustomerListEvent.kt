package com.example.myapplication.presentation.presentation.viewmodel.event

import com.example.myapplication.domain.model.CustomerSortOrder

sealed interface CustomerListEvent {
    data class SearchQueryChanged(val query: String) : CustomerListEvent
    data class SortOrderChanged(val order: CustomerSortOrder) : CustomerListEvent
    data class ShowArchivedToggled(val showArchived: Boolean) : CustomerListEvent
    data class ArchiveRequested(val customerId: String) : CustomerListEvent
    data class RestoreRequested(val customerId: String) : CustomerListEvent
    data class DeleteRequested(val customerId: String) : CustomerListEvent
    data object DismissError : CustomerListEvent
}