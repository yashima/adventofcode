package de.delusions.aoc.advent2025;

import de.delusions.util.Interval;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

@Slf4j
public class Day05Test {

    static List<Interval> ranges = List.of(Interval.from("3-5"),Interval.from("10-14"),Interval.from("16-20"),Interval.from("12-18"));

    static Day05 day;

    @BeforeAll
    public static void init(){
        day = new Day05();
        day.setIntervals(ranges);
    }

    @ParameterizedTest
    @CsvSource({
            "1,true",
            "5,false",
            "8,true",
            "11,false",
            "17,false",
            "32,true"
            })
    void testExampleInput(int input,boolean expected){
        Assertions.assertThat(day.isSpoiled(input)).isEqualTo(expected);
    }

}