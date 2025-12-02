package de.delusions.aoc.advent2025;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testng.annotations.AfterClass;


@Slf4j
public class Day02Test {


    static Long sum = 0L;

    @ParameterizedTest
    @ValueSource(longs = {11,22,99,111,999,1010,1188511885,222222,446446,38593859,565656,824824824,2121212121})
    public void testFunnyNumbers(Long productId ){
        sum += productId;
        Assertions.assertThat(Day02.isFunnyProductId(productId)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "11,22,33",
            "95,115,210",
            "998,1012,2009",
            "1188511880,1188511890,1188511885"
            })
    public void testRange(Long min, Long max, int expected){
        Assertions.assertThat(new Day02.Range(min,max).sumOfFunnyProducts()).isEqualTo(expected);
    }

    @AfterAll
    public static void printSum(){
        log.debug("Sum of funny numbers: {}", sum);
    }

}