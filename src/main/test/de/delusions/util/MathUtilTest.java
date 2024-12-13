package de.delusions.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MathUtilTest {

    @Test
    public void testPrimeFactor() {
        assertThat(MathUtil.calculatePrimeFactors(12)).containsExactly(2, 2, 3);
        assertThat(MathUtil.calculatePrimeFactors(25)).containsExactly(5, 5);
        assertThat(MathUtil.calculatePrimeFactors(26)).containsExactly(2, 13);
    }

    @Test
    public void testMultiply() {
        assertThat(MathUtil.calculatePrimeFactors(144).stream().reduce((a, b) -> a * b).orElse(-1)).isEqualTo(144);
    }

    @Test
    public void testBigPrime() {
        System.out.println(MathUtil.calculateBiggerPrimeFactors(167409079868000L));
    }

}
