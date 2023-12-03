package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day03 extends Day<Integer> {

    private static final char EMPTY = '.';

    private static final char GEAR = '*';

    private static final List<Character> DIGITS = List.of( '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' );

    Matrix engineSchematic;

    public Day03( Integer... expected ) {
        super( 3, "Gear Ratios", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        engineSchematic = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        return findNumbersAdjacentTo( null ).stream().mapToInt( this::convertCoordinatesToNumber ).sum();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        engineSchematic = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        List<Coordinates> gears = engineSchematic.findValues( GEAR, false );
        List<List<Coordinates>> numbersAdjacentToParts = findNumbersAdjacentTo( '*' );
        List<Integer> ratios = new ArrayList<>();
        for ( Coordinates gear : gears ) {
            List<Coordinates> adjacent = gear.getAdjacent();
            List<List<Coordinates>> numbersFound = new ArrayList<>();
            for ( Coordinates coord : adjacent ) {
                if ( DIGITS.contains( (char) engineSchematic.getValue( coord ) ) ) {
                    List<Coordinates> number = findNumberBy( coord, numbersAdjacentToParts );
                    if ( !numbersFound.contains( number ) ) {
                        numbersFound.add( number );
                    }
                }
            }
            if ( numbersFound.size() == 2 ) {
                ratios.add( convertCoordinatesToNumber( numbersFound.get( 0 ) ) * convertCoordinatesToNumber( numbersFound.get( 1 ) ) );
            }
        }
        return ratios.stream().mapToInt( i -> i ).sum();
    }

    private List<List<Coordinates>> findNumbersAdjacentTo( Character part ) {
        List<List<Coordinates>> numbers = new ArrayList<>();
        for ( int x = 0; x < engineSchematic.getXLength(); x++ ) {
            int[] row = engineSchematic.getRow( x );
            List<Coordinates> currentNumber = new ArrayList<>();
            boolean adjacentSymbol = false;
            for ( int y = 0; y < engineSchematic.getYLength(); y++ ) {
                char current = (char) row[y];
                if ( DIGITS.contains( current ) ) {
                    currentNumber.add( new Coordinates( x, y ) );
                    adjacentSymbol = adjacentSymbol || testForSymbol( part, engineSchematic.createCoords( x, y ) );
                }
                else if ( !currentNumber.isEmpty() ) {
                    if ( adjacentSymbol ) {
                        numbers.add( currentNumber );
                    }
                    currentNumber = new ArrayList<>();
                    adjacentSymbol = false;
                }
            }
        }
        return numbers;
    }

    private List<Coordinates> findNumberBy( Coordinates coord, List<List<Coordinates>> numbersAdjacentToParts ) {
        for ( List<Coordinates> number : numbersAdjacentToParts ) {
            if ( number.contains( coord ) ) {
                return number;
            }
        }
        throw new IllegalStateException( "We know there should be a number in the list that matches the coords " + coord );
    }


    private boolean testForSymbol( Character part, Coordinates current ) {
        for ( Direction direction : Direction.values() ) {
            Coordinates coordinates = current.moveTo( direction, 1 );
            if ( engineSchematic.isInTheMatrix( coordinates ) ) {
                char value = (char) engineSchematic.getValue( coordinates );
                if ( ( part != null && value == part ) || ( value != EMPTY && !DIGITS.contains( value ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }


    private Integer convertCoordinatesToNumber( List<Coordinates> currentNumber ) {
        return Integer.valueOf(
            currentNumber.stream().map( coords -> "" + (char) engineSchematic.getValue( coords ) ).reduce( ( a, b ) -> a + b ).orElse( "0" ) );
    }
}
