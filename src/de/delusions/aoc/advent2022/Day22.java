package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Coordinates;
import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Direction;
import de.delusions.aoc.util.Matrix;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.advent2022.Day22.Square.*;
import static de.delusions.aoc.util.Direction.*;

public class Day22 extends Day<Integer> {

    static final int WALL = 1;

    static final int FLOOR = 2;

    static final int VOID = 0;

    static final Map<Character, Integer> characterMapping = Map.of( '#', WALL, '.', FLOOR, ' ', VOID, '\n', VOID );

    static final Pattern COMMAND_REGEX = Pattern.compile( "([0-9]+)([RL])?" );

    static final String mapRegex = "[#. ]*";

    static List<Edge> edges = List.of( new Edge( ONE, north, TWO, south, false, true ), new Edge( ONE, west, THREE, south, true, false ),
                                       new Edge( ONE, east, SIX, west, false, true ), new Edge( TWO, north, ONE, south, false, true ),
                                       new Edge( TWO, west, SIX, north, true, true ), new Edge( TWO, south, FIVE, north, false, true ),
                                       new Edge( THREE, north, ONE, east, true, false ), new Edge( THREE, south, FIVE, east, true, true ),
                                       new Edge( FOUR, east, SIX, south, true, true ), new Edge( FIVE, west, THREE, north, true, true ),
                                       new Edge( FIVE, south, TWO, north, false, true ), new Edge( SIX, north, FOUR, west, true, true ),
                                       new Edge( SIX, east, ONE, west, false, true ), new Edge( SIX, south, TWO, east, true, true ) );

    AtomicReference<String> commands = new AtomicReference<>();

    Matrix map;

    Coordinates currentPosition;

    Direction facing = east;

    int edgeSize = 0;

    public Day22() {
        super( 22, "Monkey Map" );
    }

    static int getFaceValue( Direction direction ) {
        return switch ( direction ) {
            case south -> 1;
            case west -> 2;
            case north -> 3;
            case east -> 0;
            default -> throw new IllegalStateException( "Unexpected value: " + direction );
        };
    }

    @Override
    public Integer part1( Stream<String> input ) {
        readInput( input );
        return solve( this::adjustPosition );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        readInput( input );
        try {
            return solve( this::adjustPositionByEdge );
        }
        catch ( RuntimeException e ) {
            System.err.println( map );
            System.err.println( "Exception: " + e.getMessage() );
            throw e;
        }
    }

    void readInput( Stream<String> input ) {
        map = parse( input );
        map.setPrintMap(
            Map.of( WALL, "#", FLOOR, ".", VOID, " ", getFaceValue( south ) + FLOOR + 1, south.getSymbol(), getFaceValue( north ) + FLOOR + 1,
                    north.getSymbol(), getFaceValue( east ) + FLOOR + 1, east.getSymbol(), getFaceValue( west ) + FLOOR + 1, west.getSymbol() ) );
        edgeSize = map.cleanup() / 4;
        currentPosition = findStart();
    }

    int solve( Supplier<Boolean> supplier ) {
        System.out.println( " start:" + currentPosition );
        Matcher matcher = COMMAND_REGEX.matcher( commands.get() );
        while ( matcher.find() ) {
            executeMoveAndTurn( Integer.parseInt( matcher.group( 1 ) ), matcher.group( 2 ), supplier );
        }
        System.out.println( map + "Final Coordinates=" + currentPosition );
        return 1000 * ( 1 + currentPosition.getX() ) + 4 * ( 1 + currentPosition.getY() ) + getFaceValue( facing );
    }

    void executeMoveAndTurn( int move, String turn, Supplier<Boolean> wrapAroundFunction ) {
        for ( int step = 0; step < move; step++ ) {
            //for pretty printing purposes:
            currentPosition.setValue( getFaceValue( facing ) + 3 );
            map.setValue( currentPosition );

            Coordinates nextPosition = currentPosition.moveDay22( facing );
            int nextValue = map.isInTheMatrix( nextPosition ) ? map.getValue( nextPosition ) : VOID;
            if ( nextValue > WALL ) {
                currentPosition = nextPosition;
            }
            else if ( nextValue == WALL ) {
                break;
            }
            else if ( wrapAroundFunction.get() ) {
                break;
            }
        }
        if ( turn != null ) {
            facing = turn.equals( "L" ) ? facing.turnLeft() : facing.turnRight();
        }
    }

    boolean adjustPosition() {
        //look opposite and move as far as you can until you reach the void again then turn back around
        Direction tempFacing = facing.turnRight( 180 );
        Coordinates tempCoordinates = currentPosition;
        Coordinates tempMove = currentPosition;
        while ( map.isInTheMatrix( tempMove ) && WALL <= map.getValue( tempMove ) ) { //not void
            tempCoordinates = tempMove;
            tempMove = tempCoordinates.moveDay22( tempFacing );
        }
        if ( map.getValue( tempCoordinates ) == WALL ) {
            return true;
        }
        else {
            currentPosition = tempCoordinates;
        }
        return false;
    }

    boolean adjustPositionByEdge() {
        Edge edge = null;
        for ( Square square : Square.values() ) {
            if ( square.squareX == currentPosition.x / edgeSize && square.squareY == currentPosition.y / edgeSize ) {
                for ( Edge e : edges ) {
                    if ( e.fromSquare == square && e.fromEdge == facing ) {
                        edge = e;
                        break;
                    }
                }
                break;
            }
        }
        if ( edge == null ) {
            throw new IllegalStateException( "Pos=" + currentPosition + " facing=" + facing );
        }
        int x = currentPosition.x % edgeSize;
        int y = currentPosition.y % edgeSize;
        int newX = edge.toSquare.squareY * edgeSize;
        ;
        int newY = edge.toSquare.squareX * edgeSize;
        ;
        if ( List.of( north, south ).contains( edge.newFacing ) ) {
            newX = newX + ( edge.switch0N ? edgeSize - x - 1 : x );
        }
        else {
            newY = newY + ( edge.switch0N ? edgeSize - y - 1 : y );
        }
        Coordinates nextPosition = map.createCoords( edge.switchXY ? newY : newX, edge.switchXY ? newX : newY );
        if ( map.getValue( nextPosition ) == WALL ) {
            return true;
        }
        currentPosition = nextPosition;
        facing = edge.newFacing;
        return false;
    }

    Matrix parse( Stream<String> input ) {
        return new Matrix( input.map( this::mapLine ).filter( Objects::nonNull ).toArray( int[][]::new ) );
    }

    enum Square {//coordinates are all wrongly implemented so what...
        ONE( 2, 0 ), TWO( 0, 1 ), THREE( 1, 1 ), FOUR( 2, 1 ), FIVE( 2, 2 ), SIX( 3, 2 );

        final int squareX;

        final int squareY;

        Square( int squareY, int squareX ) {
            this.squareX = squareX;
            this.squareY = squareY;
        }

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

    record Edge(Square fromSquare, Direction fromEdge, Square toSquare, Direction newFacing, boolean switchXY, boolean switch0N) {}
}
