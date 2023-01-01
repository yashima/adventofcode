package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Coordinates;
import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Direction;
import de.delusions.aoc.util.Matrix;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day22 extends Day<Integer> {

    static final int WALL = 2;

    static final int FLOOR = 1;

    static final int VOID = 0;

    static final int SOUTH = 1 + 3;

    static final int NORTH = 3 + 3;

    static final int EAST = 0 + 3;

    static final int WEST = 2 + 3;

    static final Map<Character, Integer> characterMapping = Map.of( '#', WALL, '.', FLOOR, ' ', VOID, '\n', VOID );

    static final Pattern COMMAND_REGEX = Pattern.compile( "([0-9]+)(R|L)" );

    static final String mapRegex = "[#\\. ]*";

    AtomicReference<String> commands = new AtomicReference<>();

    public Day22() {
        super( 22, "Monkey Map" );
    }

    Matrix map;

    Coordinates currentPosition;

    Direction facing = Direction.east; //Direction was implemented for another day where I had coordinate trouble

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        map = parse( input );
        map.setPrintMap( Map.of( WALL, "#", FLOOR, ".", VOID, " ", SOUTH, "v", NORTH, "^", EAST, ">", WEST, "<" ) );
        map.cleanup();
        currentPosition = findStart();
        return solve();
    }

    int solve() {
        System.out.println( commands.get() + " start:" + currentPosition );
        Matcher matcher = COMMAND_REGEX.matcher( commands.get() );
        while ( matcher.find() ) {
            executeMoveAndTurn( Integer.parseInt( matcher.group( 1 ) ), matcher.group( 2 ) );
        }
        System.out.println( map + " " + currentPosition );
        return 1000 * ( 1 + currentPosition.getX() ) + 4 * ( 1 + currentPosition.getY() ) + getFaceValue();
    }

    private int getFaceValue() {
        int faceValue = switch ( facing ) {
            case south -> 1;
            case west -> 2;
            case north -> 3;
            case east -> 0;
            default -> throw new IllegalStateException( "Unexpected value: " + facing );
        };
        return faceValue;
    }


    void executeMoveAndTurn( int move, String turn ) {
        System.out.println( "move " + move + " then turn " + turn );
        for ( int step = 0; step < move; step++ ) {
            if ( map.getValue( currentPosition ) == FLOOR ) {
                currentPosition.setValue( getFaceValue() + 3 );
                map.setValue( currentPosition );
            }

            Coordinates nextPosition = currentPosition.moveDay22( facing );
            int nextValue = map.isInTheMatrix( nextPosition ) ? map.getValue( nextPosition ) : VOID;
            if ( List.of( FLOOR, SOUTH, NORTH, WEST, EAST ).contains( nextValue ) ) {
                currentPosition = nextPosition;
            }
            else if ( nextValue == WALL ) {
                System.out.println( "Stopped by wall after " + step );
                break;
            }
            else if ( nextValue == VOID ) {
                System.out.println( "Ran into void after " + step );
                //look opposite and move as far as you can until you reach the void again then turn back around
                Direction tempFacing = facing.turnRight( 180 );
                Coordinates tempCoordinates = currentPosition;
                while ( map.isInTheMatrix( tempCoordinates ) &&
                    List.of( WALL, FLOOR, SOUTH, WEST, EAST, NORTH ).contains( map.getValue( tempCoordinates ) ) ) {
                    currentPosition = tempCoordinates;
                    tempCoordinates = tempCoordinates.moveDay22( tempFacing );
                }
            }
        }

        facing = turn.equals( "L" ) ? facing.turnLeft() : facing.turnRight();
        System.out.println( facing + " " + currentPosition );
    }

    Matrix parse( Stream<String> input ) {
        return new Matrix( input.map( line -> mapLine( line ) ).filter( Objects::nonNull ).toArray( int[][]::new ) );
    }

    Coordinates findStart() {
        Coordinates result = map.createCoords( 0, 0 );
        while ( map.getValue( result ) != FLOOR ) {
            result = result.moveDay22( facing );
        }
        return result;
    }

    int[] mapLine( String line ) {
        if ( line.matches( mapRegex ) ) {
            return line.chars().map( c -> characterMapping.get( (char) c ) ).toArray();
        }
        else if ( !line.isEmpty() ) {
            commands.set( line );
        }
        return null;
    }
}
