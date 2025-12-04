package de.delusions.aoc.advent2025;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
public class Day03Test {

    static int sum = 0;

    @ParameterizedTest
    @CsvSource({
            "987654321111111,98",
            "811111111111119,89",
            "234234234234278,78",
            "818181911112111,92"})
    void testExampleInput(String input,int expected){
        Assertions.assertThat(Day03.BatteryBank.parse(input).largest(2)).isEqualTo(expected);
        sum += Day03.BatteryBank.parse(input).largest(2);
    }

    @ParameterizedTest
    @CsvSource({
            "987654321111111,987654321111",
            "811111111111119,811111111119",
            "234234234234278,434234234278",
            "818181911112111,888911112111"})
    void testExampleInput2(String input,long expected){
        Assertions.assertThat(Day03.BatteryBank.parse(input).largest(12)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "987654321111111,9876",
            "811111111111119,8119",
            "234234234234278,4478",
            "818181911112111,9211"})
    void testExampleInpu32(String input,long expected){
        Assertions.assertThat(Day03.BatteryBank.parse(input).largest(4)).isEqualTo(expected);
    }

    @AfterAll
    public static void printSum(){
        log.debug("Total output joltage: {}", sum);
    }
}