package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.InvoiceItem
import com.example.myapplication.domain.model.Money
import com.example.myapplication.domain.model.TaxRate
import com.example.myapplication.domain.model.applyBasisPoints


class CalculateInvoiceTotalsUseCase {

    /**
     * Berechnet Netto-, Steuer- und Bruttobeträge für alle Positionen
     * sowie die Gesamtsummen einer Rechnung.
     *
     * @param items Die Liste der Rechnungspositionen.
     * @param isSmallBusiness Bei true (§ 19 UStG) wird für jede Position 0 % Steuer erzwungen,
     *   unabhängig vom auf der Position hinterlegten Steuersatz.
     */
    operator fun invoke(
        items: List<InvoiceItem>,
        isSmallBusiness: Boolean = false
    ): CalculatedInvoiceTotals {
        var totalNet = Money.ZERO
        var totalTax = Money.ZERO
        var totalGross = Money.ZERO

        val calculatedItems = items.map { item ->
            // 1. Grundpreis Netto: Menge ist in Tausendsteln gespeichert (2,5 Stk = 2500),
            //    Preis in Cent -> (qty_tausendstel * preis_cent) / 1000 = betrag_cent
            val rawNetMinorUnits = (item.quantityThousandths * item.unitPriceNet.minorUnits) / 1000L
            val baseNet = Money(rawNetMinorUnits)

            // 2. Rabatt anwenden (Basispunkte, 1000 = 10 %)
            val discountAmount = baseNet.applyBasisPoints(item.discountBasisPoints)
            val itemNet = baseNet - discountAmount

            // 3. Steuer berechnen (Kleinunternehmer: immer 0 %)
            val effectiveTaxRate = if (isSmallBusiness) TaxRate.ZERO else item.taxRate
            val itemTax = itemNet.applyBasisPoints(effectiveTaxRate.basisPoints)

            // 4. Brutto der Position
            val itemGross = itemNet + itemTax

            totalNet += itemNet
            totalTax += itemTax
            totalGross += itemGross

            item.copy(
                taxRate = effectiveTaxRate,
                netAmount = itemNet,
                taxAmount = itemTax,
                grossAmount = itemGross
            )
        }

        return CalculatedInvoiceTotals(
            items = calculatedItems,
            totalNet = totalNet,
            totalTax = totalTax,
            totalGross = totalGross
        )
    }
}