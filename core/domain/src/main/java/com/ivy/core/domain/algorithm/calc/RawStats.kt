package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import java.time.Instant

/**
 *
 */
fun rawStats(trns: List<CalcTrn>): RawStats {
    val incomes = mutableMapOf<CurrencyCode, Double>()
    val expenses = mutableMapOf<CurrencyCode, Double>()
    var incomesCount = 0
    var expensesCount = 0

    var newestTrnTime = Instant.MIN

    trns.forEach { trn ->
        when (trn.type) {
            TransactionType.Income -> {
                incomesCount++
//                val hey: Short = 44
//                incomes["USD"]?.plus(hey)
                incomes.aggregateTrn(trn)
            }
            TransactionType.Expense -> {
                expensesCount++
                expenses.aggregateTrn(trn)
            }
        }
        if (trn.time > newestTrnTime) {
            newestTrnTime = trn.time
        }
    } //for loop ends here *

    return RawStats(
        incomes = incomes,
        expenses = expenses,
        incomesCount = incomesCount,
        expensesCount = expensesCount,
        newestTrnTime = newestTrnTime,
    )
}

/**
 * Sums all values in two [RawStats] instances.
 *
 * @return RawStats picking the largest newestTrnTimeo
 *
 * Complexity:
 * **O(m+n) space-time**
 * where:
 * - m = Left's RawStats incomes & expenses maps size
 * - n = Right's RawStats incomes & expenses maps size
 */
infix operator fun RawStats.plus(other: RawStats): RawStats {
    fun sumMaps(
        map1: Map<CurrencyCode, Double>,
        map2: Map<CurrencyCode, Double>
    ): Map<CurrencyCode, Double> {
        val sum = mutableMapOf<CurrencyCode, Double>()
        map1.forEach(sum::aggregateWithCurrencyAndAmount)
        map2.forEach(sum::aggregateWithCurrencyAndAmount)
        return sum
    }

    return RawStats(
        incomes = sumMaps(incomes, other.incomes),
        expenses = sumMaps(expenses, other.expenses),
        incomesCount = incomesCount + other.incomesCount,
        expensesCount = expensesCount + other.expensesCount,
        newestTrnTime = maxOf(newestTrnTime, other.newestTrnTime)
    )
}

private fun MutableMap<CurrencyCode, Double>.aggregateTrn(
    trn: CalcTrn
) = aggregateWithCurrencyAndAmount(keyCurrencyCode = trn.currency, amount = trn.amount)

private fun MutableMap<CurrencyCode, Double>.aggregateWithCurrencyAndAmount(
    keyCurrencyCode: CurrencyCode,
    amount: Double
) {
    //_ is the parameter name for the key which is the currency
    //old value is the current value associated with the key
    compute(keyCurrencyCode) { _, oldValue ->
        (oldValue ?: 0.0) + amount
    }
}