package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day09 extends Day<Integer> {
    public Day09( Integer... expected ) {
        super( 9, "Mirage Maintenance", expected );
    }

    List<Integer> readLine( String line ) {
        return new ArrayList<>( Arrays.stream( line.split( " " ) ).map( p -> Integer.parseInt( p.trim() ) ).toList() );
    }

    List<Integer> calculateNext( List<Integer> sequence, boolean last ) {
        if ( sequence.stream().allMatch( n -> n == 0 ) ) {
            sequence.add( 0 );
            return sequence;
        }
        AtomicInteger previous = new AtomicInteger( sequence.getFirst() );
        List<Integer> diffs = sequence.stream().skip( 1 ).map( n -> {
            int p = previous.get();
            previous.set( n );
            return n - p;
        } ).toList();
        diffs = new ArrayList<>( diffs );
        List<Integer> nextSequence = calculateNext( diffs, last );
        if ( last ) {
            diffs.add( sequence.getLast() + nextSequence.getLast() );
        }
        else {
            diffs.addFirst( sequence.getFirst() - nextSequence.getFirst() );
        }
        return diffs;
    }


    @Override
    public Integer part0( Stream<String> input ) {
        List<List<Integer>> readings = input.map( this::readLine ).toList();
        return readings.stream().map( s -> calculateNext( s, true ) ).mapToInt( List::getLast ).sum();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        List<List<Integer>> readings = input.map( this::readLine ).toList();
        return readings.stream().map( s -> calculateNext( s, false ) ).mapToInt( List::getFirst ).sum();
    }
}
