package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
public class Day17 extends Day<Long> {

    static final int STARTING_Y = 3; //3 above highest reaching rock

    //static BigInteger CYCLES =BigInteger.valueOf(  ).
    static final Integer WALL = Integer.parseInt( "100000001", 2 );

    static final Integer FLOOR = Integer.parseInt( "111111111", 2 );

    Day17() {
        super( 17, "Pyroclastic Flow" );
    }

    enum Shape {
        MINUSS( "000111100" ),//
        PLUSSS( "000010000", "000111000", "000010000" ),//
        LSHAPE( "000111000", "000001000", "000001000" ), //
        ISHAPE( "000100000", "000100000", "000100000", "000100000" ),//
        SQUARE( "000110000", "000110000" );

        List<Integer> nums;

        Shape( String... num ) {
            this.nums = Arrays.stream( num ).map( n -> Integer.parseInt( n, 2 ) ).toList();
        }

        static final AtomicLong shapeCounter = new AtomicLong( 0 );

        static List<Integer> getNext() {
            if ( shapeCounter.get() == CYCLES ) {
                return Collections.emptyList();
            }
            Shape next = values()[(int) ( shapeCounter.getAndIncrement() % values().length )];
            return next.nums;
        }
    }

    static Set<Long> states = new HashSet<>();

    long commandLength;

    long periodHeight = 0;

    long periodLength = 0;

    boolean detectCycle( long nextCommand ) { //only when the rock is 0
        if ( Shape.shapeCounter.get() % 5L == 0 ) {
            if ( states.contains( nextCommand ) ) {
                return true;
            }
            states.add( nextCommand );
        }
        return false;
    }

    long getCycle() {
        return Shape.shapeCounter.get();
    }

    static long CYCLES;

    @Override
    public Long part1( Stream<String> input ) {
        CYCLES = 2022;
        return calculateResult( solve( input ) );
    }

    @Override
   public Long part2( Stream<String> input ) {
        CYCLES = 1000000000000L;
        if ( periodHeight > 0 ) {
            Shape.shapeCounter.set( CYCLES - CYCLES % periodLength );
        }
        return calculateResult( solve( input ) );
    }

    //3068 , 1514285714288
    Long calculateResult( List<Integer> chute ) {
        //reduce iterations by 1 because we need to initially find period but not on second attempt
        long iterations = CYCLES / periodLength + ( CYCLES == 2022 ? -1 : 0 );
        //reduce chute/rest size by 1 to remove floor
        return periodHeight == 0 ? chute.size() - 1 : iterations * periodHeight + ( chute.size() - 1 );
    }

    List<Integer> solve( Stream<String> input ) {
        String commands = input.reduce( "", ( a, b ) -> a + b );
        commandLength = commands.length();
        System.out.println( "commands " + commands.length() );
        List<Integer> chute = new ArrayList<>( List.of( FLOOR ) );
        AtomicInteger position = new AtomicInteger( STARTING_Y + chute.size() );
        List<Integer> currentShape = new ArrayList<>( Shape.getNext() );
        AtomicLong commandCounter = new AtomicLong( 0 );
        while ( !currentShape.isEmpty() ) {
            commandCounter.set( 0L );
            commands.chars().forEach( command -> { //keep cycling commands
                if ( currentShape.isEmpty() ) {
                    List<Integer> next = Shape.getNext();
                    if ( next.isEmpty() ) {
                        return;//we're done
                    }
                    else {
                        currentShape.addAll( next );
                        position.set( chute.size() + STARTING_Y );
                    }
                    if ( periodHeight == 0 && detectCycle( commandCounter.get() ) ) {
                        System.out.println( "CYCLES target = " + CYCLES );
                        periodLength = getCycle();
                        periodHeight = chute.size() - 1; //height is without floor
                        System.out.println( "Period detected at " + periodLength + " with height=" + periodHeight );
                        Shape.shapeCounter.set( CYCLES - CYCLES % periodLength );//process the rest now
                    }
                }

                int currentPosition = position.getAndDecrement(); //moving down

                List<Integer> newShapePosition = new ArrayList<>();
                //do command:
                if ( command == '<' ) { //vs 51
                    newShapePosition = findCollision( chute, currentShape.stream().map( c -> c << 1 ).toList(), currentPosition, "left" );
                }
                else if ( command == '>' ) {
                    newShapePosition = findCollision( chute, currentShape.stream().map( c -> c >> 1 ).toList(), currentPosition, "right" );
                }
                if ( !newShapePosition.isEmpty() ) {
                    //command is ok, so execute:
                    currentShape.clear();
                    currentShape.addAll( newShapePosition );
                }

                //move down
                List<Integer> movingDown = findCollision( chute, currentShape, currentPosition - 1, "down" );
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
                commandCounter.incrementAndGet();
            } );
        }
        //new LinkedList<>( chute ).descendingIterator().forEachRemaining( i -> System.out.println(printLevel( i )) );
        return chute;
    }

    List<Integer> lastBlock( List<Integer> chute ) {
        List<Integer> lastBlocks = new ArrayList<>();
        List.of( 2, 4, 8, 16, 32, 64, 128 ).forEach( i -> {
            int listPos = chute.size() - 1;

            while ( !chute.isEmpty() && listPos > 0 ) {
                if ( ( i & chute.get( listPos ) ) != 0 ) {
                    lastBlocks.add( listPos );
                    break;
                }
                listPos = listPos - 1;
            }
        } );
        return lastBlocks;
    }

    List<Integer> findCollision( List<Integer> chute, List<Integer> attemptedShape, int chutePosition, String debug ) {
        for ( int i = 0; i < attemptedShape.size(); i++ ) {
            int chuteValue = ( chutePosition + i < chute.size() ) ? chute.get( chutePosition + i ) : WALL;
            int shapeValue = attemptedShape.get( i );
            if ( ( chuteValue & shapeValue ) > 0 ) {
                return Collections.emptyList();
            }
        }
        return attemptedShape;
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

}
