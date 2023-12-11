package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day11 extends Day<Long> {

    public static Character STAR = '#';

    public Day11( Long... expected ) {
        super( 11, "Cosmic Expansion", expected );
    }


    @Override
    public Long part0( Stream<String> input ) {
        int expansionFactor = 2;
        return calculateDistancesBetweenStars( input, expansionFactor );
    }

    @Override
    public Long part1( Stream<String> input ) {
        long expansionFactor = isTestMode() ? 100 : 1000000;
        return calculateDistancesBetweenStars( input, expansionFactor );
    }

    private Long calculateDistancesBetweenStars( Stream<String> input, long expansionFactor ) {
        Matrix cosmos = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        List<Coordinates> stars = cosmos.findValues( STAR, false );
        List<Integer> emptyRows = getEmptySpace( cosmos.rows() );
        List<Integer> emptyColumns = getEmptySpace( cosmos.columns() );
        long distance = 0;
        LinkedList<Coordinates> starStack = new LinkedList<>( stars );
        while ( !starStack.isEmpty() ) {
            Coordinates star = starStack.pop();
            for ( Coordinates other : starStack ) {
                distance += star.manhattanDistance( other );
                distance += emptyRows.stream().filter( r -> isBetween( r, star.x, other.x ) ).count() * ( expansionFactor - 1 );
                distance += emptyColumns.stream().filter( r -> isBetween( r, star.y, other.y ) ).count() * ( expansionFactor - 1 );

            }
        }
        return distance;
    }

    private static List<Integer> getEmptySpace( Stream<int[]> cosmos ) {
        AtomicInteger time = new AtomicInteger( 0 );
        List<Integer> emptySpace = new ArrayList<>();
        cosmos.forEach( space -> {
            boolean empty = Arrays.stream( space ).noneMatch( s -> s == STAR );
            int t = time.getAndIncrement();
            if ( empty ) {emptySpace.add( t );}
        } );
        return emptySpace;
    }

    /**
     * Checks if a value is between two others. Needed both for x and y coordinates to see if an empty row or column is between two stars
     *
     * @param emptySpace the location of the empty space
     * @param star       the coordinate (x or y) of one star
     * @param other      the coordinate (x or y) of the other star
     * @return true if the emptySpace is between the two numbers
     */
    private static boolean isBetween( int emptySpace, int star, int other ) {
        return Math.min( star, other ) < emptySpace && emptySpace < Math.max( star, other );
    }


}
