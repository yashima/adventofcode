package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.List;

public class AdventOfCode2023
{
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent = List.of(
            new Day01());
        System.out.println( "Days solved: " + daysOfAdvent.size() );
        Day<?> today = new Day01(142L, 281L,54561L, 0L);
        today.run( false, true , 1);

    }
}
