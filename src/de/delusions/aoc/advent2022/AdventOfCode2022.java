package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.List;

public class AdventOfCode2022 {
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent =
            List.of( new Day1(), new Day2(), new Day3(), new Day4(), new Day5(), new Day6(), new Day7(), new Day8(), new Day9(), new Day10(),
                     new Day11(), new Day12(), new Day13(), new Day14(), new Day15(), new Day16(), new Day17() , new Day18());
        System.out.println( "Days solved: " + daysOfAdvent.size() );
        Day<?> today = new Day20();
        today.run( false, true );

        //part20325733607
        //    15307383014733
    }

}
