package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.Map;
import java.util.stream.Stream;

public class Day10 extends Day<Integer> {
    static final Character START = 'S';

    static Map<Character, Pipe> PIPES = Map.of( '.',
                                                new Pipe( '.', false ),
                                                '-',
                                                new Pipe( '-', true, Direction.east, Direction.west ),
                                                '|',
                                                new Pipe( '|', true, Direction.south, Direction.north ),
                                                'L',
                                                new Pipe( 'L', false, Direction.north, Direction.east ),
                                                'J',
                                                new Pipe( 'J', false, Direction.north, Direction.west ),
                                                '7',
                                                new Pipe( '7', false, Direction.south, Direction.west ),
                                                'F',
                                                new Pipe( 'F', false, Direction.south, Direction.east ) );


    public Day10( Integer... expected ) {
        super( 10, "Pipe Maze", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix maze = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        Coordinates starting = maze.findValues( START, true ).getFirst();
        int steps = 1;
        Coordinates current = starting.moveTo( getInitialFacing( starting, maze ), 1 );
        while ( !current.equals( starting ) ) {
            steps++;
            current = current.moveTo( getPipeFor( current, maze ).adjustFacing( current ), 1 );
        }
        return steps / 2;
    }

    private Direction getInitialFacing( Coordinates starting, Matrix maze ) {
        return Direction.getBasic()
                        .stream()
                        .filter( d -> getPipeFor( starting.moveTo( d, 1 ), maze ).connectsTo( d.opposite() ) )
                        .findFirst()
                        .orElse( null );
    }

    private Pipe getPipeFor( Coordinates coords, Matrix maze ) {
        return maze.isInTheMatrix( coords ) ? PIPES.get( (char) maze.getValue( coords ) ) : PIPES.get( '.' );
    }

    private String print( int s, Coordinates coordinates ) {
        return String.format( "%s: %s %s facing=%s", s, (char) coordinates.getValue(), coordinates, coordinates.getFacing() );
    }

    record Pipe(char symbol, boolean straight, Direction... turn) {
        Direction adjustFacing( Coordinates current ) {
            return current.getFacing().opposite() == turn[0] ? turn[1] : turn[0];
        }

        boolean connectsTo( Direction direction ) {
            return turn.length == 2 && ( direction == turn[0] || direction == turn[1] );
        }
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix maze = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        Matrix spare = new Matrix( new int[maze.getXLength()][maze.getYLength()] );
        spare.setAllValues( '.' );
        Coordinates starting = maze.findValues( START, true ).getFirst();
        Coordinates current = starting.moveTo( getInitialFacing( starting, maze ), 1 );
        while ( !current.equals( starting ) ) {
            spare.setValue( current, 'X' );
            current = current.moveTo( getPipeFor( current, maze ).adjustFacing( current ), 1 );
            current.setValue( 'X' );
        }
        System.out.println( spare );
        return 0;
    }
}
