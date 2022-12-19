package de.delusions.aoc.advent2022;

import de.delusions.aoc.Day;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 extends Day<Integer> {

    Day1() {
        super( 1, "Calorie Counting" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return solve( input, 10000 );
    }

    @Override
    public Integer part2( Stream<String> input ) {
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
