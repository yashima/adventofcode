package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.stream.Stream;

public class Day06 extends Day<Integer> {

    Day06() {
        super( 6, "uning Trouble" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        String line = input.reduce( "", ( a, b ) -> a + b );
        int magicNumber = 14;
        for ( int idx = 0; idx < line.length() - magicNumber; idx++ ) {
            if ( line.substring( idx, idx + magicNumber ).chars().distinct().count() == magicNumber ) {
                return idx + magicNumber;
            }
        }
        return 0;
    }

    @Override
    public Integer part2( Stream<String> input ) {
        //TODO part 2 is probably up there
        return null;
    }
}
