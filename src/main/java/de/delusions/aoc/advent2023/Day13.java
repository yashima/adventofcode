package de.delusions.aoc.advent2023;

import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day13 extends Day<Integer> {
    public Day13( Integer... expected ) {
        super( 13, "Point of Incidence", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return findMirrorPositions( input, 0 );
    }


    /**
     * How characters in the input map to binary for bitwise comparisons
     */
    static final Map<Character, Integer> BINARY = Map.of( '#', 1, '.', 0 );

    @Override
    public Integer part1( Stream<String> input ) {
        return findMirrorPositions( input, 1 );
    }

    static int findMirrorPositions( Stream<String> input, int maxDiff ) {
        List<Matrix> patterns = parseInput( input );
        AtomicInteger result = new AtomicInteger( 0 );
        patterns.forEach( pattern -> {
            int mirrorPosition = findByDiff( pattern, maxDiff );
            if ( mirrorPosition < 0 ) {
                mirrorPosition = findByDiff( pattern.transposeRight(), maxDiff );
                if ( mirrorPosition < 0 ) {
                    System.err.println( pattern );
                    throw new IllegalStateException( "Bug Alarm: Every matrix is mirrored" );
                }
                result.addAndGet( mirrorPosition );
            }
            else {
                result.addAndGet( mirrorPosition * 100 );
            }
        } );
        return result.get();
    }

    /**
     * Reads the input. Patterns are separated by blank lines. I have not figured out a way to do this in a stream that splits the input through blank
     * lines.
     *
     * @param input the full input
     * @return a list of Matrices representing the patterns in the input
     */
    private static List<Matrix> parseInput( Stream<String> input ) {
        List<List<String>> all = new ArrayList<>();
        final List<String> current = new ArrayList<>();
        input.forEach( line -> {
            if ( line.isBlank() ) {
                all.add( new ArrayList<>( current ) );
                current.clear();
            }
            else {
                current.add( line );
            }
        } );
        all.add( new ArrayList<>( current ) );
        return all.stream().map( list -> Matrix.createFromStream( list.stream() ) ).toList();
    }

    static int findByDiff( Matrix matrix, int maxDiff ) {
        return IntStream.range( 1, matrix.getXLength() ).filter( row -> fullDiff( matrix, row ) == maxDiff ).boxed().findFirst().orElse( -1 );
    }

    /**
     * Checks if two rows in the matrix are the same
     * @param matrix the matrix to check
     * @param row one row to compare
     * @param other the other row to compare*
     * @return number of differing bits
     */
    static int diff( Matrix matrix, int row, int other ) {
        int m1 = matrix.rowToBinary( row, BINARY );
        int m2 = matrix.rowToBinary( other, BINARY );
        return ( m1 < 0 || m2 < 0 ) ? 0 : Integer.bitCount( m1 ^ m2 );
    }

    static int fullDiff( Matrix matrix, int row ) {
        //row >=1
        int diff = 0;
        int max = Math.min( row, matrix.getXLength() );
        for ( int i = 0; i <= max; i++ ) {
            diff = diff + diff( matrix, row + i, row - i - 1 );
        }
        return diff;
    }




}
