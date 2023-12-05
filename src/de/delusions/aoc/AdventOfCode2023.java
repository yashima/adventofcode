package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.List;

public class AdventOfCode2023 {
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent = List.of( //
                                             new Day01( 142L, 281L, 54561L, 54076L ), //
                                             new Day02( 8L, 2286L, 2600L, 86036L ), //
                                             new Day03( 4361, 467835, 556057, 82824352 ),//
                                             new Day04( 13, 30, 22488, 7013204 ), //
                                             new Day05( 35L, 265018614L, 0L, 0L ) );
        System.out.println( "Days: " + daysOfAdvent.size() );
        runAllVariants( daysOfAdvent.get( daysOfAdvent.size() - 1 ) );

    }

    private static void runAllVariants( Day<?> today ) {
        today.run( true, 0 );
        today.run( false, 0 );
//        today.run( true, 1 );
//        today.run( false, 1 );
    }
}
