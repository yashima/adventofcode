package de.delusions.aoc.advent2022;

import de.delusions.util.Day;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day01 extends Day<Integer> {

    Day01() {
        super( "Calorie Counting" );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return solve( input, 10000 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return solve( input, 3 );
    }

    int solve( Stream<String> input, int numberOfElves ) {
        AtomicInteger current = new AtomicInteger( 0 );
        return input.map( line -> line.isEmpty() ? "skip" + current.incrementAndGet() : current.get() + "-" + line )//
                    .filter( l -> l.contains( "-" ) ) //
                    .collect( Collectors.toMap( l -> l.split( "-" )[0], l -> Integer.parseInt( l.split( "-" )[1] ), Integer::sum ) )//
                    .values().stream().sorted( Comparator.reverseOrder() ) //
                    .limit( numberOfElves ) //
                    .reduce( 0, Integer::sum );
    }
}
