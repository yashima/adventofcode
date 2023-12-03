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

    private static final List<Character> DIGITS = List.of( '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' );

    Matrix engineSchematic;

    public Day03( Integer... expected ) {
        super( 3, "Gear Ratios", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        engineSchematic = new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
        List<Integer> numbers = new ArrayList<>();
        for ( int x = 0; x < engineSchematic.getXLength(); x++ ) {
            int[] row = engineSchematic.getRow( x );
            List<Character> currentNumber = new ArrayList<>();
            boolean adjacentSymbol = false;
            for ( int y = 0; y < engineSchematic.getYLength(); y++ ) {
                char current = (char) row[y];
                if ( DIGITS.contains( current ) ) {
                    currentNumber.add( current );
                    adjacentSymbol = adjacentSymbol || testForSymbol( engineSchematic.createCoords( x, y ) );
                }
                else if ( !currentNumber.isEmpty() ) {
                    if ( adjacentSymbol ) {
                        numbers.add( convertCharacterListToNumber( currentNumber ) );
                    }
                    currentNumber.clear();
                    adjacentSymbol = false;
                }
            }
        }

        return numbers.stream().mapToInt( i -> i ).sum();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    private boolean testForSymbol( Coordinates current ) {
        for ( Direction direction : Direction.values() ) {
            Coordinates coordinates = current.moveTo( direction, 1 );
            if ( engineSchematic.isInTheMatrix( coordinates ) ) {
                char value = (char) engineSchematic.getValue( coordinates );
                if ( value != EMPTY && !DIGITS.contains( value ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer convertCharacterListToNumber( List<Character> currentNumber ) {
        return Integer.valueOf( currentNumber.stream().map( c -> "" + c ).reduce( ( a, b ) -> a + b ).orElse( "0" ) );
    }
}
