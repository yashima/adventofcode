package de.delusions.aoc.advent2025;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class Day08Test {

    @ParameterizedTest
    @CsvSource({
            "01,02"
            })
    void testExampleInput(String input,int expected){
        //Assertions.assertThat(Day08.XXX.parse(input).YYY()).isEqualTo(expected);

    }

}