package de.delusions.aoc.advent2022;

import de.delusions.util.Day;

import java.util.List;
import java.util.stream.Stream;

public class Day20 extends Day<Long> {


    List<CryptPair> numbers;

    final long key = 811589153L;

    Integer ZERO = null;

    public Day20() {
        super( 20, "Grove Positioning System" );
    }

    void getZeroPosition() {
        if ( ZERO == null ) {
            ZERO = numbers.indexOf( numbers.stream().filter( p -> p.number == 0 ).findFirst().get() );
        }
    }

    CryptPair getZero() {
        if ( ZERO == null ) {
            getZeroPosition();
        }
        return numbers.get( ZERO );
    }

    long getValueAtPosition( long index ) {
        long number = move( index, numbers.size(), getZero() ).number;
        System.out.println( "pos " + index + "=" + number );
        return number;
    }

    @Override
    public Long part0( Stream<String> input ) {
        numbers = parse( input, 1 );
        mixNumbers();
        return getValueAtPosition( 1000 ) + getValueAtPosition( 2000 ) + getValueAtPosition( 3000 );
    }

    @Override
    public Long part1( Stream<String> input ) {
        numbers = parse( input, key );
        for ( int mix = 0; mix < 10; mix++ ) {mixNumbers();}
        long a1000 = getValueAtPosition( 1000 );
        long a2000 = getValueAtPosition( 2000 );
        long a3000 = getValueAtPosition( 3000 );
        return a1000 + a2000 + a3000;
    }

    void mixNumbers() {
        numbers.forEach( element -> element.moveSteps( numbers.size() ) );
    }




    List<CryptPair> parse( Stream<String> input, long key ) {
        List<CryptPair> cryptPairs = input.map( line -> new CryptPair( key * Integer.parseInt( line ) ) ).toList();
        for ( int i = 1; i < cryptPairs.size() - 1; i++ ) {
            CryptPair prev = cryptPairs.get( i - 1 );
            CryptPair next = cryptPairs.get( i + 1 );
            cryptPairs.get( i ).setNeighbors( prev, next );
        }
        CryptPair first = cryptPairs.get( 0 );
        CryptPair last = cryptPairs.getLast();
        first.setNeighbors( last, cryptPairs.get( 1 ) );
        last.setNeighbors( cryptPairs.get( cryptPairs.size() - 2 ), first );
        return cryptPairs;
    }

    CryptPair move( long number, int listSize, CryptPair start ) {
        long stepsToTake = Math.abs( number % ( listSize ) ) + ( start.number == 0 ? -1 : 0 );
        CryptPair result = start;
        for ( int step = 0; step <= stepsToTake; step++ ) {
            result = number < 0 ? result.previous : result.next;
        }
        return result;
    }


    class CryptPair {

        final long number;

        CryptPair next;

        CryptPair previous;

        CryptPair( long number ) {
            this.number = number;
        }

        @Override
        public String toString() {
            return number + "";
        }

        void setNeighbors( CryptPair prev, CryptPair next ) {
            this.previous = prev;
            this.next = next;
        }

        void moveSteps( int listSize ) {
            if ( number % listSize == 0 || number == 0 ) {
                return;
            }

            CryptPair insertionPoint = move( this.number, numbers.size() - 1, this );

            //put the previous siblings "together"
            this.previous.next = this.next;
            this.next.previous = this.previous;

            //set the new siblings on this
            if ( number > 0 ) {
                this.next = insertionPoint;
                this.previous = insertionPoint.previous;
            }
            else {
                this.previous = insertionPoint;
                this.next = insertionPoint.next;
            }

            //tell the new siblings this has arrived
            this.next.previous = this;
            this.previous.next = this;

            //  System.out.println(prettyPrint());
        }

    }
}
