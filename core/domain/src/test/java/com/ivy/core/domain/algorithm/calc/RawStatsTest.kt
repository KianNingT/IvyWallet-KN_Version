package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

internal class RawStatsTest {

    private lateinit var transactionTypeList: List<TransactionType>

    @BeforeEach
    fun setUp() {
        transactionTypeList = listOf(
            TransactionType.Income,
            TransactionType.Expense,
        )
    }


    @Test
    fun `Test transaction type income to form RawStat object`() {
        val transactionType = transactionTypeList[0]
        assertThat(transactionType).isEqualTo(TransactionType.Income)
    }

    @Test
    fun `Test creating raw stats from transactions`() {
        val tenSecondsAgo = Instant.now().minusSeconds(10)
        val fiveSecondsAgo = Instant.now().minusSeconds(5)
        val threeSecondsAgo = Instant.now().minusSeconds(3)

        val stats  = rawStats (
            listOf(
                CalcTrn(
                    10.0,
                    "USD",
                    TransactionType.Income,
                    tenSecondsAgo),
                CalcTrn(
                    10.0,
                    "EUR",
                    TransactionType.Expense,
                    fiveSecondsAgo),
                CalcTrn(
                    10.0,
                    "USD",
                    TransactionType.Income,
                    threeSecondsAgo),
            )
        )


        assertThat(stats.expensesCount).isEqualTo(1)
        assertThat(stats.newestTrnTime).isEqualTo(threeSecondsAgo)
        assertThat(stats.expenses["EUR"]).isEqualTo(10.0)

        assertThat(stats.incomesCount).isEqualTo(2)
        assertThat(stats.incomes["USD"]).isEqualTo(20.0)
    }

}