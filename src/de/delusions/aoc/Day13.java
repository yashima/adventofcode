package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day13 extends Day<Integer> {
    public Day13( Integer... expected ) {
        super( 13, "Point of Incidence", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return findMirrorPositions( input, false );
    }

    private static int findMirrorPositions( Stream<String> input, boolean withSmudge ) {
        List<Matrix> patterns = parseInput( input );
        AtomicInteger result = new AtomicInteger( 0 );
        patterns.forEach( m -> {
            int mirrorPosition = findMirrorPosition( m, withSmudge );
            if ( mirrorPosition < 0 ) {
                mirrorPosition = findMirrorPosition( m.transpose(), withSmudge );
                if ( mirrorPosition < 0 ) {
                    System.err.println( m );
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

    static int findMirrorPosition( Matrix matrix, boolean withSmudge ) {
        List<Integer> candidates = new ArrayList<>();
        for ( int i = 1; i < matrix.getXLength(); i++ ) {
            if ( matrix.rowToString( i ).equals( matrix.rowToString( i - 1 ) ) ) {
                candidates.add( i );
            }
        }
        return candidates.stream().filter( row -> isFullMirror( matrix, row, withSmudge ) ).findFirst().orElse( -1 );
    }

    static boolean isFullMirror( Matrix matrix, int rowIdx, boolean withSmudge ) {
        if ( !withSmudge && ( rowIdx == 1 || rowIdx == matrix.getXLength() - 1 ) ) {
            return true;
        }
        for ( int j = 1; j + rowIdx < matrix.getXLength(); j++ ) {
            String m1 = matrix.rowToString( rowIdx + j );
            String m2 = matrix.rowToString( rowIdx - j - 1 );
            //System.out.printf("State (%d,%d) -> (%d,%d) %s|%s\n",i,i-1,j+i,i-j-1,m1,m2);
            if ( !isMatch( m1, m2 ) ) {
                return false;
            }
        }
        return true;
    }

    private static boolean isMatch( String m1, String m2 ) {
        return m1 == null || m1.equals( m2 ) || m2 == null;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return findMirrorPositions( input, true );
    }


}
