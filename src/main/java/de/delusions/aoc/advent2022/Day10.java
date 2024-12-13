package de.delusions.aoc.advent2022;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day10 extends Day<Integer> {
    static int WIDTH = 40;

    AtomicInteger clock = new AtomicInteger( 0 );

    AtomicInteger register = new AtomicInteger( 1 );

    List<String> crt = new ArrayList<>();

    Day10() {
        super( 10, "Cathode-Ray Tube" );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        AtomicInteger frequency = new AtomicInteger( 0 );
        input.forEach( line -> {
            frequency.addAndGet( computeCycle( 0 ) );
            if ( line.startsWith( "addx" ) ) {
                frequency.addAndGet( computeCycle( Integer.parseInt( line.substring( 5 ) ) ) );
            }
        } );
        crt.forEach( System.out::print );
        return frequency.get();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        //TODO solved but...
        return null;
    }

    int computeCycle( int delta ) {
        int cycle = clock.incrementAndGet();
        int X = register.addAndGet( delta );
        crt.add( ( Math.abs( X - ( cycle % WIDTH ) ) <= 1 ? "#" : "." ) + ( ( cycle ) % WIDTH == 0 ? "\n" : "" ) );
        return List.of( 20, 60, 100, 140, 180, 220 ).contains( cycle ) ? cycle * X : 0;
    }
}
