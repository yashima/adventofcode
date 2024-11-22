package de.delusions.aoc;

import de.delusions.aoc.days.Day01;
import de.delusions.util.Day;

import java.util.List;

public class AdventOfCode {
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent = List.of( //
                                             new Day01()
                                             );
        System.out.println( "Days: " + daysOfAdvent.size() );
        runAllVariants( daysOfAdvent.getLast() );

    }

    private static void runAllVariants( Day<?> today ) {
        today.run( true, 0 );
        today.run( false, 0 );
        today.run( true, 1 );
        today.run( false, 1 );
    }
}
