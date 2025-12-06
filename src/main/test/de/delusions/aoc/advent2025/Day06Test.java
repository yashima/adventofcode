package de.delusions.aoc.advent2025;

import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Day06Test {

    static String inputAsString  = """
            123 328  51 64
            45 64  387 23
            6 98  215 314
            *   +   *   +
            """;

    static int problemId = 0;

    static List<Day06.Problem> problems;

    @BeforeAll
    public static void init(){
        Stream<String> input = Arrays.stream(inputAsString.split("\n"));;
        problems = Matrix.createFromStream(input,Day06::parseLine).columns().map(Day06.Problem::create).toList();
    }


    @ParameterizedTest
    @ValueSource(ints = {33210,490,4243455,401})
    void testExampleInput(int expected){
        Day06.Problem problem = problems.get(problemId);
        Assertions.assertThat(problem.solve()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "0,356,24,1",
            "1,8,248,369",
            "2,175,581,32",
            "3,4,431,623"
    })
    void testTranformation(int problemId,int expectedX,int expectedY,int expectedZ){
        List<Long> transformed = problems.get(problemId).transformed();
        Assertions.assertThat(transformed.get(0)).isEqualTo(expectedX);
        Assertions.assertThat(transformed.get(1)).isEqualTo(expectedY);
        Assertions.assertThat(transformed.get(2)).isEqualTo(expectedZ);
    }

    @AfterEach
    public void updateId(){
        problemId++;
    }

}