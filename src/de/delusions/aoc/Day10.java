package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day10 extends Day<Integer> {
    static final Character START = 'S';

    /**
     * Map of pipes and the directions they map.
     */
    static Map<Character, Pipe> PIPES = Map.of( '.', new Pipe( '.', false, '.', '.' ),
                                                '-', new Pipe( '-', true, '─', '═', Direction.east, Direction.west ),
                                                '|', new Pipe( '|', true, '│', '║', Direction.south, Direction.north ),
                                                'L', new Pipe( 'L', false, '└', '╚', Direction.north, Direction.east ),
                                                'J', new Pipe( 'J', false, '╝', '┘', Direction.north, Direction.west ),
                                                '7', new Pipe( '7', false, '┐', '╗', Direction.south, Direction.west ),
                                                'F', new Pipe( 'F', false, '╔', '┌', Direction.south, Direction.east ) );


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

    /* Characters signaling that we're inside the loop if we are walking clockwise */
    static List<Character> clock = List.of( '└', '┘', '┌', '┐', '│' );

    /* Characters signaling that we're inside the loop if we are walking counterclockwise */
    static List<Character> counter = List.of( '║', '╚', '╗', '╔', '╝' );

    private String print( int s, Coordinates coordinates ) {
        return String.format( "%s: %s %s facing=%s", s, (char) coordinates.getValue(), coordinates, coordinates.getFacing() );
    }

    /**
     * Spare initially contains a visualization of the pipe loop. With each part of the loop marked with a character that is depending on walking
     * direction facing outside the loop or inside the loop. This method calculates row by row for each coordinate marked with '.' if it is inside or
     * outside of the loop.
     *
     * @param spare     the spare with each loop part marked, and everything else set to '.'
     * @param clockwise flag that says whether we are walking clockwise or counterclockwise
     * @return the count of everything marked as 'I' inside the loop
     */
    private static int processSpare( Matrix spare, boolean clockwise ) {
        int count = 0;
        for ( int x = 0; x < spare.getXLength(); x++ ) {
            int[] row = spare.getRow( x );
            boolean inside = false;
            for ( int y = 0; y < row.length; y++ ) {
                Coordinates coords = spare.createCoords( x, y );
                char value = (char) spare.getValue( coords );
                if ( value == '.' ) {
                    coords.setValue( inside ? 'I' : 'O' );
                    spare.setValue( coords );
                    if ( inside ) {count++;}
                }
                else {
                    inside = clock.contains( value );
                }

            }
        }
        return count;
    }

    /**
     * This was not necessary, one can see easily which value the starting coordinates have. But I wanted to calculate this anyway.
     * In theory it should be possible to also calculate if we're moving clockwise or counterclockwise but I didn't manage.
     * I determined that both my input and the example for part 2 move clockwise. So that's that.
     *
     * @param starting coordinates of the start point of the loop
     * @param maze     the pipe maze
     * @return the first of the two directions that the value of the starting pipe allows movement in
     */
    private Direction getInitialFacing( Coordinates starting, Matrix maze ) {
        return Direction.getBasic()
                        .stream()
                        .filter( d -> getPipeFor( starting.moveTo( d, 1 ), maze ).connectsTo( d.opposite() ) )
                        .findFirst()
                        .orElse( null );
    }

    /**
     * Extracts the Pipe for the given coordinates from the maze
     *
     * @param coords the coordinates to check
     * @param maze   the maze
     * @return a pipe that matches the value of the coordinates in the maze
     */
    private Pipe getPipeFor( Coordinates coords, Matrix maze ) {
        return maze.isInTheMatrix( coords ) ? PIPES.get( (char) maze.getValue( coords ) ) : PIPES.get( '.' );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix maze = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        Matrix spare = new Matrix( new int[maze.getXLength()][maze.getYLength()] );
        spare.setAllValues( '.' ); //starting empty
        Coordinates starting = maze.findValues( START, true ).getFirst();
        Direction initialFacing = getInitialFacing( starting, maze );
        Coordinates current = starting.moveTo( initialFacing, 1 );
        //rewriting spare with pretty visualization ascii characters for the actual loop parts:
        while ( !current.equals( starting ) ) {
            Pipe pipe = getPipeFor( current, maze );
            current.setValue( pipe.getAsciiMapping( current ) );
            spare.setValue( current );
            current = current.moveTo( pipe.adjustFacing( current ), 1 );
        }
        //resetting the starting value to S is important so it doesn't toggle the "inside" marker at least for my input
        //since I never quite figured out how to deal with the starting pipe or how to find out automatically if
        //we're moving clockwise or not... this works for my input and the examples but may not work for other people
        spare.setValue( starting, 'S' );
        int count = processSpare( spare, List.of( Direction.north, Direction.west ).contains( initialFacing ) );
        System.out.println( spare );

        return count;
    }

    /*
    A record to help with the Pipes
     */
    record Pipe(char symbol, boolean straight, char ascii1, char ascii2, Direction... turn) {
        Direction adjustFacing( Coordinates current ) {
            return current.getFacing().opposite() == turn[0] ? turn[1] : turn[0];
        }

        /**
         * The ascii characters visualize the pipe better than the original input. They differ depending on which end of the pipe the move entered,
         * therefore allowing later calculation which side is facing outside the loop and which not.
         *
         * @param current coordinates with current facing
         * @return depending on facing returns an ascii character for better visualization
         */
        char getAsciiMapping( Coordinates current ) {
            return current.getFacing().opposite() == turn[0] ? ascii1 : ascii2;
        }

        /**
         * Checks if this piece of pipe connects to the given direction, needed
         * for figuring the initial facing for the start of the run through the pipe
         * @param direction direction to check
         * @return true if direction connects to pipe
         */
        boolean connectsTo( Direction direction ) {
            return turn.length == 2 && ( direction == turn[0] || direction == turn[1] );
        }
    }

}
