package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 extends Day<String> {
    public Day1(String... expected) {
        super(1, "tag", expected);
    }

    public Day1(){
        super(1, "tag");
    }

    @Override
    public String part0(Stream<String> input) {
        return input.collect(Collectors.joining());
    }

    @Override
    public String part1(Stream<String> input) {
        return input.collect(Collectors.joining());
    }
}
