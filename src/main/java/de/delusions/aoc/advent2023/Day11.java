package de.delusions.aoc.advent2023;

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

    public static final Character STAR = '#';

    public Day11( Long... expected ) {
        super( 11, "Cosmic Expansion", expected );
    }


    @Override
    public Long part0( Stream<String> input ) {
        int expansionRate = 2;
        return calculateDistancesBetweenStars( input, expansionRate );
    }

    @Override
    public Long part1( Stream<String> input ) {
        long expansionRate = isTestMode() ? 100 : 1000000;
        return calculateDistancesBetweenStars( input, expansionRate );
    }

    /**
     * First time this year I can recycle the whole thing with just 1 added input parameter. Note the expansionRate is for the amount of rows or
     * columns the empty space gets replaced with so 1 row becomes 2 rows for expansion rate 2. But the added distance is just 1. So for an expansion
     * rate of 100, the added distance per empty row/column is 99.
     *
     * @param input         the test or production input as a stream of strings aka lines
     * @param expansionRate the expansion rate for this run, differs between parts and test/prod
     * @return the distance between all pairs of stars taking expansionRate into account
     */
    private Long calculateDistancesBetweenStars( Stream<String> input, long expansionRate ) {
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
                distance += emptyRows.stream().filter( r -> isBetween( r, star.x, other.x ) ).count() * ( expansionRate - 1 );
                distance += emptyColumns.stream().filter( r -> isBetween( r, star.y, other.y ) ).count() * ( expansionRate - 1 );
            }
        }
        return distance;
    }

    /**
     * Calculates the empty space for either rows or columns and returns a list of indices where the cosmos contains such empty space.
     *
     * @param cosmosStream a stream of either rows or columns
     * @return a list of indices for rows or columns that have no stars on them
     */
    private static List<Integer> getEmptySpace( Stream<int[]> cosmosStream ) {
        AtomicInteger time = new AtomicInteger( 0 );
        List<Integer> emptySpace = new ArrayList<>();
        cosmosStream.forEach( space -> {
            boolean empty = Arrays.stream( space ).noneMatch( s -> s == STAR );
            int t = time.getAndIncrement();
            if ( empty ) {
                emptySpace.add( t );
            }
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
