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

public class Day22 extends Day<Integer> {

    static final int WALL = 2;

    static final int FLOOR = 1;

    static final int VOID = 0;

    static final int SOUTH = 1 + 3;

    static final int NORTH = 3 + 3;

    static final int EAST = 0 + 3;

    static final int WEST = 2 + 3;

    static final Map<Character, Integer> characterMapping = Map.of( '#', WALL, '.', FLOOR, ' ', VOID, '\n', VOID );

    static final Pattern COMMAND_REGEX = Pattern.compile( "([0-9]+)(R|L)?" );

    static final String mapRegex = "[#\\. ]*";

    AtomicReference<String> commands = new AtomicReference<>();

    public Day22() {
        super( 22, "Monkey Map" );
    }

    Matrix map;

    Coordinates currentPosition;

    Direction facing = Direction.east; //Direction was implemented for another day where I had coordinate trouble

    static List<EdgePair> pairs = List.of( new EdgePair( Square.ONE, Direction.north, Square.TWO, Direction.south, false ),
                                           new EdgePair( Square.ONE, Direction.west, Square.THREE, Direction.south, true ),
                                           new EdgePair( Square.ONE, Direction.east, Square.SIX, Direction.west, false ),
                                           new EdgePair( Square.TWO, Direction.north, Square.ONE, Direction.south, false ),
                                           new EdgePair( Square.TWO, Direction.west, Square.SIX, Direction.north, true ),
                                           new EdgePair( Square.TWO, Direction.south, Square.FIVE, Direction.north, false ),
                                           new EdgePair( Square.THREE, Direction.north, Square.ONE, Direction.east, true ),
                                           new EdgePair( Square.THREE, Direction.south, Square.FIVE, Direction.east, true ),
                                           new EdgePair( Square.FOUR, Direction.east, Square.SIX, Direction.south, true ),
                                           new EdgePair( Square.FIVE, Direction.west, Square.THREE, Direction.north, true ),
                                           new EdgePair( Square.FIVE, Direction.south, Square.TWO, Direction.north, false ),
                                           new EdgePair( Square.SIX, Direction.north, Square.FOUR, Direction.west, true ),
                                           new EdgePair( Square.SIX, Direction.east, Square.ONE, Direction.west, false ),
                                           new EdgePair( Square.SIX, Direction.south, Square.TWO, Direction.east, true ) );

    int diceLength = 0;

    @Override
    public Integer part1( Stream<String> input ) {
        map = parse( input );
        map.setPrintMap( Map.of( WALL, "#", FLOOR, ".", VOID, " ", SOUTH, "v", NORTH, "^", EAST, ">", WEST, "<" ) );
        diceLength = map.cleanup() / 4;
        currentPosition = findStart();
        return solve( this::adjustPosition );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        map = parse( input );
        map.setPrintMap( Map.of( WALL, "#", FLOOR, ".", VOID, " ", SOUTH, "v", NORTH, "^", EAST, ">", WEST, "<" ) );
        diceLength = map.cleanup() / 4;
        currentPosition = findStart();
        return solve( this::adjustPositionByEdge );
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

    int solve( Supplier<Boolean> supplier ) {
        System.out.println( " start:" + currentPosition );
        Matcher matcher = COMMAND_REGEX.matcher( commands.get() );
        while ( matcher.find() ) {
            executeMoveAndTurn( Integer.parseInt( matcher.group( 1 ) ), matcher.group( 2 ), supplier );
        }
        System.out.println( map + " " + currentPosition );
        return 1000 * ( 1 + currentPosition.getX() ) + 4 * ( 1 + currentPosition.getY() ) + getFaceValue();
    }

    void executeMoveAndTurn( int move, String turn, Supplier<Boolean> supplier ) {
        System.out.println( "move " + move + " then turn " + turn );
        for ( int step = 0; step < move; step++ ) {
            //for pretty printing purposes:
            currentPosition.setValue( getFaceValue() + 3 );
            map.setValue( currentPosition );

            Coordinates nextPosition = currentPosition.moveDay22( facing );
            int nextValue = map.isInTheMatrix( nextPosition ) ? map.getValue( nextPosition ) : VOID;
            if ( List.of( FLOOR, SOUTH, NORTH, WEST, EAST ).contains( nextValue ) ) {
                currentPosition = nextPosition;
            }
            else if ( nextValue == WALL ) {
                break;
            }
            else if ( nextValue == VOID ) {
                if ( supplier.get() ) {break;}
            }
        }
        if ( turn != null ) {
            facing = turn.equals( "L" ) ? facing.turnLeft() : facing.turnRight();
        }
        System.out.println( facing + " " + currentPosition );
    }

    boolean adjustPosition() {
        //look opposite and move as far as you can until you reach the void again then turn back around
        Direction tempFacing = facing.turnRight( 180 );
        Coordinates tempCoordinates = currentPosition;
        Coordinates tempMove = currentPosition;
        while ( map.isInTheMatrix( tempMove ) && List.of( WALL, FLOOR, SOUTH, WEST, EAST, NORTH ).contains( map.getValue( tempMove ) ) ) {
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

    private boolean adjustPositionByEdge() {
        EdgePair edge = null;
        for ( Square square : Square.values() ) {
            if ( square.mX == currentPosition.x / diceLength && square.mY == currentPosition.y / diceLength ) {
                for ( EdgePair e : pairs ) {
                    if ( e.fromSquare == square && e.fromEdge == facing ) {
                        edge = e;
                        break;
                    }
                }
                break;
            }
        }
        int x = currentPosition.x % diceLength;
        int y = currentPosition.y % diceLength;
        int newX = edge.switchXY ? edge.toSquare.mX * diceLength + y : ( edge.toSquare.mX + 1 ) * diceLength - x;
        int newY = edge.switchXY ? edge.toSquare.mY * diceLength + x : ( edge.toSquare.mY + 1 ) * diceLength - y;
        Coordinates nextPosition = map.createCoords( newX, newY );
        if ( map.getValue( nextPosition ) == WALL ) {
            return true;
        }
        currentPosition = nextPosition;
        facing = edge.newFacing;
        return false;
    }

    ;

    private Boolean adjustPositionPart2() {
        //detect which edge I am at
        Coordinates nextPosition;
        Direction nextFacing;
        int x = currentPosition.x;
        int y = currentPosition.y;
        if ( facing == Direction.north ) {
            if ( y == 0 ) { //TOP 1 -> TOP 2
                //-- top 1: facing N, y=0, x=2*E+x1 -> top 2
                nextPosition = map.createCoords( 2 * diceLength - ( x - diceLength ), diceLength );
                nextFacing = Direction.south;
            }
            else if ( y == diceLength && x < diceLength ) { //TOP 2 -> TOP 1
                //-- top 2: facing N, y=E, x=x1 -> top 1
                nextPosition = map.createCoords( 3 * diceLength - x, 0 );
                nextFacing = Direction.south;
            }
            else if ( y == diceLength && x < 2 * diceLength ) { //TOP 3 -> LEF 1
                //-- top 3: facing N, y=E, x=x1+E -> lef 1
                nextPosition = map.createCoords( diceLength * 2, x - diceLength );
                nextFacing = Direction.east;
            }
            else if ( y == 2 * diceLength && x < 3 * diceLength ) { //TOP 6 -> LEF 4
                //-- top 6: facing N, y=2*E, x=3*E+x1 -> lef 4
                nextPosition = map.createCoords( 3 * diceLength, diceLength + ( x - 3 * diceLength ) );
                nextFacing = Direction.west;
            }
            else {
                throw new IllegalStateException( "north" );
            }
        }
        else if ( facing == Direction.south ) {
            if ( y == 2 * diceLength && x < diceLength ) { //BOT 2 -> BOT 5
                //-- bot 2: facing S, y=2*E, x=x1 -> bot 5
                nextPosition = map.createCoords( 2 * diceLength - x, 3 * diceLength );
                nextFacing = Direction.north;
            }
            else if ( y == 2 * diceLength && x < 2 * diceLength ) { //BOT 3 -> LEF 5
                //-- bot 3: facing S, y=2*E, x=x1+E -> lef 5
                nextPosition = map.createCoords( 2 * diceLength, 2 * diceLength + ( x - diceLength ) );
                nextFacing = Direction.east;
            }
            else if ( y == 3 * diceLength && x < 3 * diceLength ) { //BOT 5 -> BOT 2
                //-- bot 5: facing S, y=3*E, x=2*E+x1 -> bot 2
                nextPosition = map.createCoords( diceLength - ( x - 2 * diceLength ), 2 * diceLength );
                nextFacing = Direction.north;
            }
            else if ( y == 3 * diceLength && x < 4 * diceLength ) { //BOT 6 -> LEF 2
                //-- bot 6: facing S, y=3*E, x=3*E+x1 -> lef 2
                nextPosition = map.createCoords( 0, x - 3 * diceLength );
                nextFacing = Direction.east;
            }
            else {
                throw new IllegalStateException( "south" );
            }
        }
        else if ( facing == Direction.east ) {
            if ( x == diceLength && y < diceLength ) { //LEF 1 -> TOP 3
                //-- lef 1: facing E, y=E+y1, x=E -> top 3
                nextPosition = map.createCoords( y + diceLength, diceLength );
                nextFacing = Direction.south;
            }
            else if ( x == 0 && y < 2 * diceLength ) { // LEF 2 -> BOT 6
                //-- lef 2: facing E, y=E+y1, x=0 -> bot 6
                nextPosition = map.createCoords( 3 * diceLength + ( y - diceLength ), 3 * diceLength );
                nextFacing = Direction.north;
            }
            else if ( x == 2 * diceLength && y < 3 * diceLength ) { //LEF 5 -> BOT 3
                //-- lef 5: facing E, y=2*E+y1, x=2*E -> bot 3
                nextPosition = map.createCoords( 0, 2 * diceLength );
                nextFacing = Direction.north;
            }
            else {
                throw new IllegalStateException( "east" );
            }
        }
        else if ( facing == Direction.west ) {
            if ( x == 2 * diceLength && y < 2 * diceLength ) { //RIG 1 -> RIG 6
                //-- rig 1: facing W, y=y1 x=2*E -> rig 6
                nextPosition = map.createCoords( 4 * diceLength, 3 * diceLength - y );
                nextFacing = Direction.east;
            }
            else if ( x == 3 * diceLength && y < diceLength ) { //RIG4  -> TOP6
                //-- rig 4: facing W, y=E+y1, x=3*E -> top 6
                nextPosition = map.createCoords( 3 * diceLength + x, 2 * diceLength );
                nextFacing = Direction.south;
            }
            else if ( x == 4 * diceLength && y < 3 * diceLength ) { //RIG 6 -> RIG 1
                //-- rig 6: facing W, y=2*E+y1, x=4*E -> rig 1
                nextPosition = map.createCoords( 3 * diceLength, diceLength - ( y - 2 * diceLength ) );
                nextFacing = Direction.east;
            }
            else {
                throw new IllegalStateException( "west" );
            }
        }
        else {
            throw new IllegalStateException( "whut?" );
        }
        if ( map.getValue( nextPosition ) == WALL ) {
            return true;
        }
        currentPosition = nextPosition;
        facing = nextFacing;
        return false;
    }

    enum Square {
        ONE( 2, 0 ), TWO( 0, 1 ), THREE( 1, 1 ), FOUR( 2, 1 ), FIVE( 2, 2 ), SIX( 3, 2 );

        final int mX;

        final int mY;

        Square( int my, int mx ) {
            this.mX = mx;
            this.mY = my;
        }
    }

    record EdgePair(Square fromSquare, Direction fromEdge, Square toSquare, Direction newFacing, boolean switchXY) {}

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
