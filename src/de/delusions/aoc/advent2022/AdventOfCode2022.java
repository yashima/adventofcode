package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.List;

public class AdventOfCode2022 {
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent =
            List.of( new Day01(), new Day02(), new Day03(), new Day04(), new Day05(), new Day06(), new Day07(), new Day08(), new Day09(), new Day10(),
                     new Day11(), new Day12(), new Day13(), new Day14(), new Day15(), new Day16(), new Day17(), new Day18(), new Day19(), new Day20(),
                     new Day21() );
        System.out.println( "Days solved: " + daysOfAdvent.size() );
        Day<?> today = new Day23();
        today.run( true, true );
    }

}
