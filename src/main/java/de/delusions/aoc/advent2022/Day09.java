package de.delusions.aoc.advent2022;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day09 extends Day<Integer> {

    public Day09() {
        super( 9, "Rope Bridge" );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Set<Coordinates> positionsVisited = new HashSet<>();
        RowEnd head = new RowEnd( "head" );
        List<RowEnd> knots = IntStream.range( 1, 10 ).mapToObj( idx -> new RowEnd( "t" + idx ) ).toList();
        input.forEach( line -> {
            head.receiveCommand( line );
            while ( head.processCommand() ) {
                RowEnd current = head;
                for ( int idx = 0; idx < knots.size(); idx++ ) {
                    RowEnd knot = knots.get( idx );
                    Coordinates position = knot.follow( current );
                    current = knot;
                    if ( idx + 1 == knots.size() ) {
                        positionsVisited.add( position );
                    }
                }
            }
        } );
        return positionsVisited.size();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        //TODO solved both but...
        return null;
    }


    static class RowEnd { //only used by Day9
        final String name;

        int row = 0;

        int col = 0;

        int steps = 0;

        String direction;

        RowEnd( String name ) {
            this.name = name;
        }

        void receiveCommand( String line ) {
            String[] pair = line.split( " " );
            direction = pair[0];
            steps = Integer.parseInt( pair[1] );
        }

        boolean processCommand() {
            if ( steps == 0 ) {
                return false;
            }
            switch ( direction ) {
                case "U":
                    this.row++;
                    break;
                case "D":
                    this.row--;
                    break;
                case "L":
                    this.col--;
                    break;
                case "R":
                    this.col++;
                    break;
            }
            steps--;
            return true;
        }

        Coordinates follow( RowEnd head ) {
            if ( Math.abs( this.row - head.row ) >= 2 || Math.abs( this.col - head.col ) >= 2 ) {
                col = col + Integer.compare( head.col, this.col );
                row = row + Integer.compare( head.row, this.row );
            }
            return new Coordinates( row, col );
        }
    }
}
