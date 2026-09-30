package com.example.myapplication.domain.pdf

import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.model.TaxRate

object InvoiceTaxBreakdownCalculator {

    data class Row(
        val taxRate: TaxRate,
        val netAmount: Money,
        val taxAmount: Money
    )

    fun calculate(items: List<InvoiceItem>): List<Row> =
        items.groupBy { it.taxRate }
            .map { (rate, group) ->
                Row(
                    taxRate = rate,
                    netAmount = group.fold(Money.ZERO) { acc, item -> acc + item.netAmount },
                    taxAmount = group.fold(Money.ZERO) { acc, item -> acc + item.taxAmount }
                )
            }
            .sortedBy { it.taxRate.basisPoints }
}