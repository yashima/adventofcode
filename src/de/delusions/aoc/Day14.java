package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Day14 extends Day<Long> {
    public Day14( Long... expected ) {
        super( 14, "", expected );
    }

    @Override
    public Long part0( Stream<String> input ) {
        Matrix dish = Matrix.createFromStream( input ).transpose();
        return calculateStressNoChange( dish );
    }

    private long calculateStressNoChange( Matrix dish ) {
        AtomicLong result = new AtomicLong( 0 );
        System.out.println( dish );
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
        return null;
    }
}
