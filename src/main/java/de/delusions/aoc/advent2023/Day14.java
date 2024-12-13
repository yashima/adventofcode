package main.java.de.delusions.aoc.advent2023;

import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.delusions.util.Direction.*;

public class Day14 extends Day<Integer> {

    public static final char ROLLING_STONE = 'O';

    static int SIGNATURE_LENGTH = 50;

    static int START_CYCLE_DETECTION = 1000;

    static int FULL_CYCLES = 1000000000;

    static int TEST_CYCLES = 10000;

    public Day14( Integer... expected ) {
        super( 14, "Parabolic Reflector Dish", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return calculateStressNoChange( Matrix.createFromStream( input ).transpose() );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix dish = Matrix.createFromStream( input );
        List<Integer> results = new ArrayList<>();
        String signature = null;
        for ( int tiltCycle = 0; tiltCycle < TEST_CYCLES; tiltCycle++ ) {
            if ( tiltCycle == START_CYCLE_DETECTION ) {
                signature = getSignature( results );
            }
            else if ( signature != null && signature.equals( getSignature( results ) ) ) {
                int cycleLength = tiltCycle - START_CYCLE_DETECTION;
                results = results.subList( results.size() - cycleLength - 1, results.size() - 1 );
                break;
            }
            processTiltCycle( dish );
            results.add( (int) calculateCurrentStress( dish ) );
        }
        return results.get( ( FULL_CYCLES - START_CYCLE_DETECTION ) % results.size() );
    }

    /**
     * Finding a repeating signature in the results, helps avoiding unnecessary calculationgs
     *
     * @param cycles the list of results to convert to a signature
     * @return a string representing the subset of results used for the signature
     */
    String getSignature( List<Integer> cycles ) {
        return numbersToString( cycles.subList( Math.max( 0, cycles.size() - SIGNATURE_LENGTH ), cycles.size() - 1 ) );
    }

    /**
     * Converts a list of numbers to a string representation via characters
     * @param numbers the numbers to convert to characters and then string
     * @return a string representing the given numbers
     */
    String numbersToString( List<Integer> numbers ) {
        return numbers.stream().map( c -> String.valueOf( (char) (int) c ) ).collect( Collectors.joining());
    }

    /**
     * Luckily, we're always calculating stress on the North side. So this calculation needs no complicated directions or transpose actions
     *
     * @param dish the current state of the dish
     * @return the current stress of the dish on the North side
     */
    long calculateCurrentStress( Matrix dish ) {
        AtomicInteger stressFactor = new AtomicInteger( dish.getXLength() );
        return dish.rows().mapToLong( r -> stress( r, stressFactor.getAndDecrement() ) ).sum();
    }

    /**
     * Executes a set of tilts in the right order (well, one should read the problem which states the order)
     *
     * @param dish the dish matrix is modified by the tilting
     */
    void processTiltCycle( Matrix dish ) {
        List.of( north, west, south, east ).forEach( tiltDirection -> processTiltDish( dish, tiltDirection ) );
    }

    /**
     * Changes each row as a side-effect in place.
     *
     * @param dish the dish to tilt
     */
    void processTiltDish( Matrix dish, Direction dir ) {
        for ( int index = 0; index < dish.getXLength(); index++ ) {
            setTiltable( index, dir, dish, tilt( getTiltable( index, dir, dish ) ) );
        }
    }

    /**
     * Calculates the stress a row generates for a given stress factor
     *
     * @param row          the row to check for 'O'
     * @param stressFactor the factor (inverse number of the row)
     * @return the product of number of rolling stones and stressfactor
     */
    long stress( int[] row, int stressFactor ) {
        return Arrays.stream( row ).filter( i -> i == ROLLING_STONE ).count() * stressFactor;
    }

    /**
     * Rewrites a freshly tilted row to the dish matrix in the correct  direction
     *
     * @param index    the index of the row
     * @param dir      the tilt direction
     * @param dish     the current state of the dish--will be modified by this operation
     * @param tiltable the tilted row itself
     */
    void setTiltable( int index, Direction dir, Matrix dish, int[] tiltable ) {
        switch ( dir ) {
            case north -> dish.setColumn( index, tiltable, true );
            case south -> dish.setColumn( index, tiltable, false );
            case west -> dish.setRow( index, tiltable );
            case east -> dish.setRowReverse( index, tiltable );
            default -> throw new IllegalStateException( "We don't do diagonals" );
        }
    }

    /**
     * tilts a single array to the left (the rewriting takes care of directions) Could probably be shortened somewhat. This was written early on in
     * the shenanigans when I was still trying to solve the problem by transposing the matrix.
     *
     * @param tiltable an array from the dish
     * @return a new array with all the rolling stones rolled to the left except those held up by #
     */
    int[] tilt( int[] tiltable ) {
        AtomicInteger nextSpot = new AtomicInteger( 0 );
        AtomicInteger index = new AtomicInteger( 0 );
        List<Integer> rollingStones = Arrays.stream( tiltable ).filter( i -> {
            int idx = index.getAndIncrement();
            if ( i == '#' ) {
                nextSpot.set( idx + 1 );
            }
            return i == 'O';
        } ).mapToObj( i -> nextSpot.getAndIncrement() ).toList();
        for ( int i = 0; i < tiltable.length; i++ ) {
            if ( rollingStones.contains( i ) ) {
                tiltable[i] = 'O';
            }
            else if ( tiltable[i] != '#' ) {
                tiltable[i] = '.';
            }
        }
        return tiltable;
    }

    //cycle detection

    /**
     * Gets an int array corresponding to a row and the current tiltDirection from the dish
     *
     * @param index         the index of the row
     * @param tiltDirection the direction to read the int array from
     * @param dish          the current state of the dish
     * @return the row to be tilted
     */
    int[] getTiltable( int index, Direction tiltDirection, Matrix dish ) {
        return switch ( tiltDirection ) {
            case north -> dish.getColumn( index, true );
            case south -> dish.getColumn( index, false );
            case west -> dish.getRow( index );
            case east -> dish.getRowReverse( index );
            default -> throw new IllegalStateException( "We don't do diagonals" );
        };
    }

    /**
     * My initial calculation for the first part of the puzzle was able to get the result without modifying the base matrix.
     *
     * @param dish the matrix read from the input
     * @return the stress on the north side (per rows) after a single tilt
     */
    private int calculateStressNoChange( Matrix dish ) {
        AtomicInteger result = new AtomicInteger( 0 );
        dish.rows().forEach( row -> {
            AtomicInteger stress = new AtomicInteger( row.length );
            AtomicInteger index = new AtomicInteger( 0 );
            int rowResult = Arrays.stream( row ).filter( i -> {
                int idx = index.getAndIncrement();
                if ( i == '#' ) {
                    stress.set( row.length - idx - 1 );
                }
                return i == 'O';
            } ).map( i -> stress.getAndDecrement() ).sum();
            result.getAndAdd( rowResult );
        } );
        return result.get();
    }


}
