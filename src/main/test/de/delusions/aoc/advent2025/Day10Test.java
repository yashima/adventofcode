package de.delusions.aoc.advent2025;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
public class Day10Test {

    @ParameterizedTest
    @CsvSource({
            "0-1-2-5-7-8-9,10,1110010111,919",
            "0-1-2-3-6-7-8,10,1111001110,974",
            "2-3-4-5-6,10,0011111000,248",
            "3,4,0001,1",
            "1-3,4,0101,5",
            "2,4,0010,2",
            "2-3,4,0011,3",
            "0-2,4,1010,10",
            "0-1,4,1100,12"
    })
    void testExampleInput(String input, int size, String binary, int expected) {
        Assertions.assertThat(Integer.parseInt(binary, 2)).isEqualTo(expected);
        Assertions.assertThat(Day10.convertButtonsToBitmask(input, "-", size)).isEqualTo(expected);
    }

    @Test
    void testExampleMachine() {
        String line = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}";
        Day10.Machine machine = Day10.Machine.parse(line);
        Assertions.assertThat(machine.result()).isEqualTo(Integer.parseInt("0110", 2));
        Assertions.assertThat(machine.buttons()).containsExactly(1, 5, 2, 3, 10, 12);
    }

    @Test
    void testExampleMachine2() {
        String line = "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}";
        Day10.Machine machine = Day10.Machine.parse(line);
        Assertions.assertThat(machine.result()).isEqualTo(Integer.parseInt("00010", 2));
        Assertions.assertThat(machine.buttons()).containsExactly(23, 6, 17, 28, 15);

        Assertions.assertThat(machine.buttons().get(2) ^ machine.buttons().get(3) ^ machine.buttons().get(4)).isEqualTo(machine.result());
    }

    @Test
    void testExampleMachine2Bfs() {
        Day10.Machine machine = Day10.Machine.parse("[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}");
        Assertions.assertThat(machine.numberOfLightButtonPresses()).isEqualTo(3);
    }

    @Test
    void testXors() {
        Integer button1 = Integer.parseInt("10001", 2);
        Integer button2 = Integer.parseInt("11100", 2);
        Integer button3 = Integer.parseInt("01111", 2);
        int step1 = Integer.parseInt("01101", 2);
        int step2 = Integer.parseInt("00010", 2);
        Assertions.assertThat(button1 ^ button2).isEqualTo(step1);
        Assertions.assertThat(step1 ^ button3).isEqualTo(step2);
    }

    @Test
    void testButtonJoltage() {
        Day10.Machine machine = Day10.Machine.parse("[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}");
        Day10.Joltages j = new Day10.Joltages(machine.joltage(), 0);
        Assertions.assertThat(j.values()).containsExactly(2,7,12,5,7);
        Assertions.assertThat(j.pressButton(machine.buttons().get(1)).values()).containsExactly(2,7,11,4,7);
    }

}