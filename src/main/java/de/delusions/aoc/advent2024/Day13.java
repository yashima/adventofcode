package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day13 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day13.class);

    public Day13() {
        super("", 480, 0, 0, 0);
    }


    final Pattern INPUT = Pattern.compile("Button A: X.(\\d+), Y.(\\d+)Button B: X.(\\d+), Y.(\\d+)Prize: X.(\\d+), Y.(\\d+)");

    record ClawMachine(Coordinates buttonA, Coordinates buttonB, Coordinates price) {
    }


    @Override
    public Integer part0(Stream<String> input) {
        List<ClawMachine> machines = INPUT.matcher(input.collect(Collectors.joining())).results().map(m ->
                new ClawMachine(
                        new Coordinates(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                        new Coordinates(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))),
                        new Coordinates(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))))
        ).toList();

        return 0;
    }

    @Override
    public Integer part1(Stream<String> input) {
        return 0;
    }

}
