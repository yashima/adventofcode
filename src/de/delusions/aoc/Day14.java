package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Day14 extends Day<Long> {
    public Day14( Long... expected ) {
        super( 14, "", expected );
    }

    private static final long CYCLES = 1000000000;

    @Override
    public Long part0( Stream<String> input ) {
        Matrix dish = Matrix.createFromStream( input ).transpose();
        return calculateStressNoChange( dish );
    }

    private long calculateStressNoChange( Matrix dish ) {
        AtomicLong result = new AtomicLong( 0 );
        dish.rows().forEach( row -> {
            AtomicLong stress = new AtomicLong( row.length );
            AtomicLong index = new AtomicLong( 0 );
            long rowResult = Arrays.stream( row ).filter( i -> {
                long idx = index.getAndIncrement();
                if ( i == '#' ) {
                    stress.set( row.length - idx - 1 );
                }
                return i == 'O';
            } ).mapToLong( i -> stress.getAndDecrement() ).sum();
            result.getAndAdd( rowResult );
        } );
        return result.get();
    }

    @Override
    public Long part1( Stream<String> input ) {
        Matrix dish = Matrix.createFromStream( input ).transpose();
        List<Long> results = new ArrayList<>();
        for ( int i = 0; i < 100; i++ ) {
            long cycleResult = 0;
            for ( int c = 0; c < 4; c++ ) {
                cycleResult = dish.rows().mapToLong( this::tiltRow ).sum();
                //System.out.println(dish);
                dish = dish.transpose();
            }
            results.add( cycleResult );
        }
        System.out.println( results );
        return results.getLast();
    }

    long tiltRow( int[] row ) {
        AtomicInteger nextSpot = new AtomicInteger( 0 );
        AtomicInteger index = new AtomicInteger( 0 );
        List<Integer> rollingStones = Arrays.stream( row ).filter( i -> {
            int idx = index.getAndIncrement();
            if ( i == '#' ) {
                nextSpot.set( idx + 1 );
            }
            return i == 'O';
        } ).mapToObj( i -> nextSpot.getAndIncrement() ).toList();
        long stress = 0;
        for ( int i = 0; i < row.length; i++ ) {
            if ( rollingStones.contains( i ) ) {
                row[i] = 'O';
                stress += row.length - i;
            }
            else if ( row[i] != '#' ) {
                row[i] = '.';
            }
        }
        return stress;
    }
}
