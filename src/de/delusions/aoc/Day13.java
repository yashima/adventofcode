package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    /**
     * How characters in the input map to binary for bitwise comparisons
     */
    static Map<Character, Integer> BINARY = Map.of( '#', 1, '.', 0 );

    @Override
    public Integer part1( Stream<String> input ) {
        return findMirrorPositions( input, true );
    }

    static int findMirrorPositions( Stream<String> input, boolean withSmudge ) {
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

    /**
     * Calculates the position of the mirror in this pattern either with a smudge or not. First checks to find candidate row indices, then filters for
     * the first one (only one according to the puzzle) that is a "full mirror" match.
     *
     * @param pattern    the pattern to find the mirror in
     * @param withSmudge is the mirror smudged--exactly 1 bit has flipped from a perfect mirror
     * @return the index of the row before which the mirror is found or -1 if none is found
     */
    static int findMirrorPosition( Matrix pattern, boolean withSmudge ) {
        return getCandidates( pattern, withSmudge ).stream().filter( row -> isFullMirror( pattern, row, withSmudge ) ).findFirst().orElse( -1 );
    }

    /**
     * Collects pairs of equal successive rows in the matrix, returns a list of the indeces of rows where the row before this one is equal
     *
     * @param matrix
     * @return
     */
    static List<Integer> getCandidates( Matrix matrix, boolean withSmudge ) {
        List<Integer> candidates = new ArrayList<>();
        int smudgesAllowed = withSmudge ? 1 : 0;
        for ( int i = 1; i < matrix.getXLength(); i++ ) {
            int diff = diff( matrix, i, i - 1 );
            if ( diff <= smudgesAllowed ) {
                candidates.add( i );
            }
        }
        return candidates;
    }

    /**
     * Checks if for a given index that has a mirror line preceding it, all the lines around the pair are also equal until the edge of the matrix is
     * reached.
     *
     * @param matrix     the matrix to perform the check on
     * @param candidate  the index of the 2nd row of the pair--so the index is never 0
     * @param withSmudge in smudge mode 1 bit of 1 pair of surrounding lines has flipped, only if this is the case th index is match
     * @return true if the candidate represents a full match (until the edge) for a mirror location
     */
    static boolean isFullMirror( Matrix matrix, int candidate, boolean withSmudge ) {
        if ( candidate == 1 || candidate == matrix.getXLength() - 1 ) {
            return true;
        }
        int smudgesAllowed = withSmudge && diff( matrix, candidate, candidate - 1 ) == 0 ? 1 : 0;

        for ( int j = 1; j + candidate < matrix.getXLength(); j++ ) {
            int diff = diff( matrix, candidate + j, candidate - j - 1 );
            if ( diff > smudgesAllowed ) {
                return false;
            }
            if ( diff == 1 ) {
                smudgesAllowed = 0;
            }
        }
        return !withSmudge || smudgesAllowed == 0;
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


}
