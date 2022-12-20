package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.List;
import java.util.stream.Stream;

public class Day20 extends Day<Integer> {

    List<Integer> numbers;

    public Day20( ) {
        super( 20, "Grove Positioning System" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        numbers = parse( input );
        return null;
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    List<Integer> parse(Stream<String> input){
        return input.map( line -> Integer.parseInt( line ) ).toList();
    }
}
