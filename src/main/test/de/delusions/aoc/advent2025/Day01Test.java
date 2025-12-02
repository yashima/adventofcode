package de.delusions.aoc.advent2025;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Day01Test {


    static Day01.Dial dial = new Day01.Dial(100,50);

    @ParameterizedTest()
    @CsvSource({"L68,1,82","L30,0,52","R48,1,0","L5,0,95","R60,1,55","L55,1,0","L1,0,99","L99,1,0","R14,0,14","L82,1,32","L300,3,32"})
    public void testExampleInput(String turnAsString, int zeroesExpected, int valueExpected){
        Day01.Turn turn = Day01.Turn.parse(turnAsString);
        int zeros = dial.turnAndCountZeroClicks(turn);
        Assertions.assertThat(dial.value).isEqualTo(valueExpected);
        Assertions.assertThat(zeros).isEqualTo(zeroesExpected);
    }


}