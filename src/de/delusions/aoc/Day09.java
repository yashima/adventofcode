package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day09 extends Day<Integer> {
    public Day09( Integer... expected ) {
        super( 9, "", expected );
    }

    static int getLast( List<Integer> list ) {
        return list.get( list.size() - 1 );
    }

    List<Integer> readLine( String line ) {
        return new ArrayList<>( Arrays.stream( line.split( " " ) ).map( p -> Integer.parseInt( p.trim() ) ).toList() );
    }

    List<Integer> calculateNext( List<Integer> sequence ) {
        if ( sequence.stream().allMatch( n -> n == 0 ) ) {
            sequence.add( 0 );
            return sequence;
        }
        AtomicInteger previous = new AtomicInteger( sequence.get( 0 ) );
        List<Integer> diffs = sequence.stream().skip( 1 ).map( n -> {
            int p = previous.get();
            previous.set( n );
            return n - p;
        } ).toList();
        diffs = new ArrayList<>( diffs );
        diffs.add( previous.get() + getLast( calculateNext( diffs ) ) );
        return diffs;
    }

    @Override
    public Integer part0( Stream<String> input ) {
        List<List<Integer>> readings = input.map( this::readLine ).toList();
        return readings.stream().map( this::calculateNext ).mapToInt( Day09::getLast ).sum();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }
}
