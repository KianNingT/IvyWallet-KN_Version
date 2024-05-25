package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.Parser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ExpressionParserTest {

    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setUp() {
        parser = expressionParser()
    }

    @ParameterizedTest
    @CsvSource(
        "0*(5+7), 0.00",
        "8+9/3, 11.00",
        "(9-8)-(2+2)*(7/7), -3.00"
    )
    fun `Test evaluating expression`(
        expression: String,
        answer: Double) {

        //GIVEN
        val result = parser(expression).first()

        //ACTION
        val actual = result.value.eval()

        //ASSERTION
        assertThat(actual).isEqualTo(answer)
    }


}