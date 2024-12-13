package de.delusions.aoc.advent2022;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Trying to find the cycles.
 */
public class Day17 extends Day<Long> {

    static final int STARTING_Y = 3; //3 above highest reaching rock

    static final Integer WALL = Integer.parseInt( "100000001", 2 );

    static final Integer FLOOR = Integer.parseInt( "111111111", 2 );

    static final int SIGNATURE_LENGTH = 50;

    static final int START_CYCLE_DETECTION = 1000; //~ rocks * commands

    private final Set<Long> states = new HashSet<>();

    private long rocksToDrop;

    private List<Integer> chute;

    private long periodHeight = 0;

    private long periodLength = 0;

    private long heightAtCycle = 0;

    private long iterationsSkipped = 0;

    private String signature;

    private boolean cycleFound;

    Day17() {
        super( 17, "Pyroclastic Flow" );
    }

    @Override
    public Long part0( Stream<String> input ) {
        rocksToDrop = 2022;
        cycleFound = false;
        return calculateResult( solve( input ) );
    }
    //                                1514285726382
    //Test input solutions 1:3068 , 2:1514285714288

    @Override
    public Long part1( Stream<String> input ) {
        rocksToDrop = 1000000000000L;
        cycleFound = false;
        return calculateResult( solve( input ) );
    }

    /**
     * Calculates the height of the chute after a full simulation from the collected data
     *
     * @param chute the current chute (which might be shorter than the result if a cycle was found
     * @return the result of the simulation
     */
    private Long calculateResult( List<Integer> chute ) {
        if ( periodHeight != 0 ) {
            return iterationsSkipped * periodHeight + ( chute.size() - 1 );
        }
        return (long) chute.size() - 1;

    }

    /**
     * The main loop of the puzzle that solves the input.
     *
     * @param input a string of commands (air jets that move the rocks)
     * @return the chute after all the rocks have fallen
     */
    List<Integer> solve( Stream<String> input ) {
        //init lots of stuffs
        Shape.shapeCounter.set( 0 );
        final String commands = input.reduce( "", ( a, b ) -> a + b );
        chute = new ArrayList<>( List.of( FLOOR ) );
        AtomicInteger position = new AtomicInteger( STARTING_Y + chute.size() );
        final List<Integer> currentShape = new ArrayList<>( Shape.getNext( rocksToDrop ) );

        //the big loop
        while ( true ) {
            //commands repeat forever until rocks have fallen
            for ( int cIndex = 0; cIndex < commands.length(); cIndex++ ) {
                if ( getNewShapeAndCheckCycles( position, currentShape, cIndex ) ) {
                    return chute;
                }

                //position of rock against chute
                int currentPosition = position.getAndDecrement(); //moving down
                doCommand( currentShape, commands.charAt( cIndex ), currentPosition );
                moveDown( currentShape, currentPosition );
            }
        }
    }

    /**
     * Fetches a new shape when the old one has stopped moving and tries to detect cycles
     *
     * @param position       the position of the rock against the chute
     * @param currentShape   the numbers representing the current falling rock
     * @param commandCounter current command index
     * @return true if next shape is empty and calculations are done
     */
    boolean getNewShapeAndCheckCycles( AtomicInteger position, List<Integer> currentShape, int commandCounter ) {
        if ( currentShape.isEmpty() ) {

            //try to get a new shape
            List<Integer> next = Shape.getNext( rocksToDrop );
            if ( next.isEmpty() ) {
                return true; //no more new shapes
            }
            else {
                currentShape.addAll( next );
                position.set( chute.size() + STARTING_Y );
            }

            if ( Shape.shapeCounter.get() == START_CYCLE_DETECTION ) {
                signature = getSignature();
                heightAtCycle = chute.size();
            }
            //test for cycles if none have been found yet
            if ( Shape.shapeCounter.get() > START_CYCLE_DETECTION ) {
                if ( !cycleFound && signature.equals( getSignature() ) ) {
                    //yay
                    cycleFound = true;
                    System.out.println( "Signature :" + signature );
                    long cycleDetectedAt = Shape.shapeCounter.get();
                    long cyclesLeftToProcess = rocksToDrop - cycleDetectedAt;
                    periodLength = cycleDetectedAt - START_CYCLE_DETECTION;
                    periodHeight = chute.size() - heightAtCycle;
                    Shape.shapeCounter.set( rocksToDrop - cyclesLeftToProcess % periodLength );//process the rest now
                    iterationsSkipped = cyclesLeftToProcess / periodLength;
                    System.out.println( "Period detected at " + periodLength + " with height=" + periodHeight );
                }
            }
        }
        return false;
    }

    /**
     * Tries to move the current shape down one position.
     *
     * @param currentShape    the numbers representing the current rock
     * @param currentPosition position against the row
     */
    void moveDown( List<Integer> currentShape, int currentPosition ) {
        List<Integer> movingDown = findCollision( currentShape, currentPosition - 1 );
        if ( movingDown.isEmpty() ) {  //stuck, so process current shape

            for ( int j = 0; j < currentShape.size(); j++ ) {
                int chuteIndex = currentPosition + j; //not executing the move we're using previous position
                int shapeValue = currentShape.get( j );
                if ( chuteIndex < chute.size() ) {
                    int element = shapeValue + chute.get( chuteIndex );
                    chute.set( chuteIndex, element );
                }
                else {
                    chute.add( shapeValue + WALL );
                }
            }
            currentShape.clear();
        }
    }

    /**
     * There are only 2 commands '>' or '<' As the shapes are represented by numbers the bitshift operators move the rocks left and right--but only if
     * there is no collision with walls or existing non-moving rocks
     *
     * @param currentShape    the current rock
     * @param command         the command to execute
     * @param currentPosition the position of the rock against the chute
     */
    void doCommand( List<Integer> currentShape, int command, int currentPosition ) {
        List<Integer> newShapePosition =
            findCollision( currentShape.stream().map( command == '<' ? c -> c << 1 : c -> c >> 1 ).toList(), currentPosition );
        if ( !newShapePosition.isEmpty() ) {
            //command is ok, so execute:
            currentShape.clear();
            currentShape.addAll( newShapePosition );
        }
    }

    /**
     * Check if the proposed new position for the shape collides with anything
     *
     * @param attemptedShape the shape to check for collisions
     * @param chutePosition  the current position against the chute
     * @return the attemptedShape list if no collision is found, an empty list if collision is found
     */
    List<Integer> findCollision( List<Integer> attemptedShape, int chutePosition ) {
        for ( int i = 0; i < attemptedShape.size(); i++ ) {
            int chuteValue = ( chutePosition + i < chute.size() ) ? chute.get( chutePosition + i ) : WALL;
            int shapeValue = attemptedShape.get( i );
            if ( ( chuteValue & shapeValue ) > 0 ) {
                return Collections.emptyList();
            }
        }
        return attemptedShape;
    }

    /**
     * Fetches the string representing the SIGNATURE_LENGTH of numbers at the end of the chute
     *
     * @return the signature string
     */
    String getSignature() {
        if ( SIGNATURE_LENGTH > chute.size() ) {

            throw new IllegalStateException( "Fnord" );
        }
        return numbersToString( chute.subList( Math.max( 0, chute.size() - SIGNATURE_LENGTH ), chute.size() - 1 ) );
    }

    /**
     * Converts a list of numbers into a string, in this case unicode is needed
     *
     * @param numbers the numbers to convert
     * @return the string representing the numbers
     */
    String numbersToString( List<Integer> numbers ) {
        return numbers.stream().map( c -> String.valueOf( (char) (int) c ) ).reduce( "", ( a, b ) -> a + b );
    }

    String printLevel( int level ) {
        if ( level == 511 ) {
            return "+-------+";
        }
        String levelString = Integer.toBinaryString( level );
        levelString = levelString.substring( 1, levelString.length() - 1 );
        levelString = levelString.replace( '0', '.' );
        levelString = levelString.replace( '1', '#' );
        return "|" + levelString + "|";
    }


    /**
     * Rock shapes are represented as binary strings that can be transformed into numbers
     */
    enum Shape {
        MINUSS( "000111100" ),//
        PLUSSS( "000010000", "000111000", "000010000" ),//
        LSHAPE( "000111000", "000001000", "000001000" ), //
        ISHAPE( "000100000", "000100000", "000100000", "000100000" ),//
        SQUARE( "000110000", "000110000" );

        static final AtomicLong shapeCounter = new AtomicLong( 0 );

        final List<Integer> nums;

        Shape( String... num ) {
            this.nums = Arrays.stream( num ).map( n -> Integer.parseInt( n, 2 ) ).toList();
        }

        static List<Integer> getNext( long cycles ) {
            if ( shapeCounter.get() == cycles ) {
                return Collections.emptyList();
            }
            Shape next = values()[(int) ( shapeCounter.getAndIncrement() % values().length )];
            return next.nums;
        }
    }

}
