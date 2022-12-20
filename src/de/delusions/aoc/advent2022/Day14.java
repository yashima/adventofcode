package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Coordinates;
import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Direction;
import de.delusions.aoc.util.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Day14 extends Day<Integer> {
    static final int SOURCE = 3;

    static final int SAND = 2;

    static final int WALL = 1;

    static final int WIDEN = 10000; //several attempts

    Day14() {
        super( 14, "Regolith Reservoir" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        List<List<Coordinates>> walls = new ArrayList<>(
            input.map( line -> Arrays.stream( line.split( "->" ) ).map( coord -> new Coordinates( coord, ",", WALL ) ).toList() ).toList() );
        walls.add( List.of( new Coordinates( 500, 0, SOURCE ) ) );
        int xMin = walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).min( Integer::compareTo ).get();
        int xMax = walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).max( Integer::compareTo ).get();
        int yMin = walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).min( Integer::compareTo ).get();
        int yMax = walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).max( Integer::compareTo ).get() + 2;
        System.out.println( walls );
        Matrix cave = new Matrix( ( xMax - xMin ) + WIDEN, yMax - yMin + 1, xMin - ( WIDEN / 2 ), yMin );
        cave.setAllValuesRow( yMax, WALL ); //add floor

        for ( List<Coordinates> wall : walls ) {
            Coordinates previous = null; //each wall is separate
            for ( Coordinates next : wall ) {
                cave.setValue( next );
                if ( previous != null ) {
                    Direction dir = previous.lookingTowards( next );
                    while ( !next.equals( previous ) ) {
                        previous = previous.moveTo( dir, WALL );
                        cave.setValue( previous );
                    }
                }
                previous = next;
            }
        }
        while ( true ) {
            try {
                //begin a new grain
                Coordinates last = null;
                Coordinates currentGrain = move( new Coordinates( 500, 0, SOURCE ), cave );
                while ( currentGrain != null ) {
                    last = currentGrain;
                    currentGrain = move( currentGrain, cave );
                }
                if ( last == null ) {
                    break;
                }
                cave.setValue( last );
            }
            catch ( ArrayIndexOutOfBoundsException e ) { //cheating but who cares
                break;
            }
        }
        return cave.findValues( SAND, false ).size() + 1;
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    Coordinates move( Coordinates previous, Matrix cave ) {
        return Stream.of( Direction.south, Direction.southwest, Direction.southeast ).map( d -> previous.moveTo( d, SAND ) ).filter(
            cave::isEmpty ).findFirst().orElse( null );
    }
}
