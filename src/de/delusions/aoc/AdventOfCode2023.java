package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.List;

public class AdventOfCode2023 {
    public static void main( String[] args ) {

        List<Day<?>> daysOfAdvent = List.of( //
                                             new Day01( 142L, 281L, 54561L, 54076L ),
                                             new Day02( 8L, 2286L, 2600L, 86036L ),
                                             new Day03( 4361, 467835, 556057, 82824352 ),
                                             new Day04( 13, 30, 22488, 7013204 ),
                                             new Day05( 35L, 46L, 265018614L, 63179500L ),
                                             new Day06( 288L, 71503L, 2374848L, 39132886L ),
                                             new Day07( 6440L, 5905L, 251806792L, 252113488L ),
                                             new Day08( "6", "62", "16531", "24035773251517" ),
                                             new Day09( 114, 2, 1647269739, 864 ),
                                             new Day10( 8, 10, 6806, 449 ),
                                             new Day11( 374L, 8410L, 9543156L, 625243292686L ),
                                             new Day12( 21, 525152, 7622, 0 ),
                                             new Day13( 405, 400, 31739, 31539 ),
                                             new Day14( 136, 64, 108759, 89089 ),
                                             new Day15( 1320, 145, 503487, 261505 ),
                                             new Day16( 46, 51, 6921, 0 ), new Day17( 102, 0, 0, 0 ) );
        System.out.println( "Days: " + daysOfAdvent.size() );
        runAllVariants( daysOfAdvent.getLast() );

    }

    private static void runAllVariants( Day<?> today ) {
        today.run( true, 0 );
        //today.run( false, 0 );//710 too high
        //today.run( true, 1 );
        //today.run( false, 1 );
    }
}
