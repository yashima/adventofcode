package de.delusions.aoc.advent2025;

import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
public class Day06Test {

    static String inputAsString  = "123 328  51 64 \n 45 64  387 23 \n  6 98  215 314\n*   +   *   +  ";

    static int problemId = 0;

    static List<Day06.Problem> part1;
    static List<Day06.Problem> part2;

    @BeforeAll
    public static void init(){
        part1 = Matrix.createFromStream(Arrays.stream(inputAsString.split("\n")),Day06::parseLine).columns().map(Day06.Problem::create).toList();
        part2 = Day06.createTransposedProblems(Arrays.stream(inputAsString.split("\n")).toList());
    }

    @ParameterizedTest
    @CsvSource({
            "0,4,431,623 ",
            "1,175,581,32",
            "2,8,248,369 ",
            "3,356,24,1  ",
    })
    void testTranformation(int problemId,int expectedX,int expectedY,int expectedZ){
        List<Long> operands = part2.get(problemId).operands();
        Assertions.assertThat(operands.get(0)).isEqualTo(expectedX);
        Assertions.assertThat(operands.get(1)).isEqualTo(expectedY);
        Assertions.assertThat(operands.get(2)).isEqualTo(expectedZ);
    }

    @ParameterizedTest
    @ValueSource(ints = {33210,490,4243455,401})
    void testExampleInput(int expected){
        Day06.Problem problem = part1.get(problemId);
        Assertions.assertThat(problem.solve()).isEqualTo(expected);
        problemId++;
    }


    @ParameterizedTest
    @ValueSource(strings = {"542","1+","22","3","52*"})
    public void testRegexPart2(String in){
        Matcher matcher = Day06.lineParserRegex2.matcher(in);
        Assertions.assertThat(matcher.matches()).isTrue();
        Assertions.assertThat(Long.parseLong(matcher.group(1))).isGreaterThan(0);
        if(!matcher.group(2).isBlank()){
            Assertions.assertThat(matcher.group(2).charAt(0)).isIn('+','*');
        }
    }

    @Test
    void testAlternativePart2(){
        List<Day06.Problem> problems = Day06.createTransposedProblems(Arrays.stream(inputAsString.split("\n")).toList());
    }

}