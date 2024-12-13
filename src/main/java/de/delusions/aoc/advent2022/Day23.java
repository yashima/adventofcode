package de.delusions.aoc.advent2022;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.delusions.util.Direction.*;

public class Day23 extends Day<Integer> {

    Set<Coordinates> elves = new HashSet<>();

    public Day23() {
        super( 23, "Unstable Diffusion" );
    }

    final List<Direction> directionPriority = List.of( north, south, west, east );

    @Override
    public Integer part0( Stream<String> input ) {
        readElfPositions( input );

        for ( int cycle = 0; cycle < 10; cycle++ ) {
            moveElves( cycle );
        }

        int xMax = elves.stream().map( Coordinates::getX ).max( Integer::compareTo ).get();
        int xMin = elves.stream().map( Coordinates::getX ).min( Integer::compareTo ).get();
        int yMax = elves.stream().map( Coordinates::getY ).max( Integer::compareTo ).get();
        int yMin = elves.stream().map( Coordinates::getY ).min( Integer::compareTo ).get();

        for ( int x = xMin; x <= xMax; x++ ) {
            for ( int y = yMin; y <= yMax; y++ ) {
                if ( elves.contains( new Coordinates( x, y ) ) ) {
                    System.out.print( '#' );
                }
                else {
                    System.out.print( '.' );
                }
            }
            System.out.println( "" );
        }

        return ( xMax - xMin ) * ( yMax - yMin ) - elves.size();
    }

    private void moveElves(int cycle ) {
        Map<Coordinates, Coordinates> proposals = new HashMap<>();
        elves.forEach( elf -> {
            Coordinates proposal = proposeMove( cycle, elf, elves );
            if ( proposal != null && !proposals.containsKey( proposal ) ) {
                proposals.put( proposal, elf );
            }
            else {
                proposals.remove( proposal );
            }
        } );
        proposals.forEach( ( proposal, elf ) -> {
            elves.remove( elf );
            elves.add( proposal );
        } );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    private void readElfPositions( Stream<String> input ) {
        Matrix initialElves = new Matrix( input.map( line -> line.chars().map( c -> c == '.' ? 0 : 1 ).toArray() ).toArray( int[][]::new ) );
        elves = new HashSet<>();
        elves.addAll( initialElves.findValues( 1, false ) );
    }

    Map<Direction, Coordinates> checkDirections( Coordinates from, Set<Coordinates> elves ) {
        return Arrays.stream( Direction.values() )//
            .map( direction -> from.moveTo( direction, 0 ) )//
            .filter( coordinates -> !elves.contains( coordinates ) )//
            .collect( Collectors.toMap( Coordinates::getFacing, c -> c ) );
    }

    Coordinates proposeMove( int cycle, Coordinates from, Set<Coordinates> elves ) {
        Map<Direction, Coordinates> emptyFields = checkDirections( from, elves );
        Coordinates proposal = null;
        for ( int c = 0; c < directionPriority.size(); c++ ) {
            Direction direction = directionPriority.get( ( c + cycle ) % directionPriority.size() );
            List<Direction> side = checkDirection( direction );
            if ( side.stream().filter( d -> !emptyFields.keySet().contains( d ) ).findFirst().isEmpty() ) {
                return emptyFields.get( direction );
            }
        }
        return null;
    }


    List<Direction> checkDirection( Direction dir ) {
        return switch ( dir ) {
            case north -> List.of( north, northeast, northwest );
            case south -> List.of( south, southeast, southwest );
            case west -> List.of( west, southwest, northwest );
            case east -> List.of( east, southeast, northeast );
            default -> throw new IllegalStateException( "Unexpected value: " + dir );
        };
    }

}
