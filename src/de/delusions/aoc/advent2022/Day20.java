package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day20 extends Day<Long> {

    static AtomicInteger ID = new AtomicInteger();

    LinkedList<CryptPair> numbers;

    long key = 811589153L;

    Integer ZERO = null;

    public Day20() {
        super( 20, "Grove Positioning System" );
    }

    static CryptPair newPair( long number ) {
        return new CryptPair( ID.getAndIncrement(), number, false );
    }

    static CryptPair movedPair( CryptPair pair ) {
        return new CryptPair( pair.id, pair.number, true );
    }

    int getZeroPosition() {
        if ( ZERO == null ) {
            CryptPair zero = numbers.stream().filter( p -> p.number == 0 ).findFirst().get();
            ZERO = numbers.indexOf( zero );
        }
        return ZERO;
    }

    long getValueAtPosition( int index ) {
        return numbers.get( ( index + getZeroPosition() ) % ( numbers.size() ) ).number;
    }

    @Override
    public Long part1( Stream<String> input ) {
        numbers = new LinkedList<>( parse( input, 1 ) );
        mixNumbers();
        return getValueAtPosition( 1000 ) + getValueAtPosition( 2000 ) + getValueAtPosition( 3000 );
    }

    @Override
    public Long part2( Stream<String> input ) {
        numbers = new LinkedList<>( parse( input, key ) );
        for ( int mix = 0; mix < 10; mix++ ) {mixNumbers();}
        long a1000 = getValueAtPosition( 1000 );
        long a2000 = getValueAtPosition( 2000 );
        long a3000 = getValueAtPosition( 3000 );
        return a1000 + a2000 + a3000;

    }

    void mixNumbers() {
        CryptPair nextPair = getNext();
        while ( nextPair != null ) {
            movePair( nextPair );
            nextPair = getNext();
        }
        numbers = new LinkedList<>( numbers.stream().map( CryptPair::reset ).toList() );
    }

    private CryptPair getNext() {
        return numbers.stream().filter( Predicate.not( CryptPair::moved ) ).findFirst().orElse( null );
    }

    int movePair( CryptPair nextPair ) {
        int index = numbers.indexOf( nextPair );
        int newIndex = calculateNewIndex( nextPair, index );
        numbers.remove( index );
        numbers.add( newIndex, movedPair( nextPair ) );
        return newIndex;
    }

    CryptPair getById( int id ) {
        return numbers.stream().filter( cp -> cp.id == id ).findFirst().orElse( null );
    }

    int calculateNewIndex( CryptPair nextPair, int index ) {
        int numbersWitoutAlice = numbers.size() - 1;
        long newIndex = index + nextPair.number % numbersWitoutAlice;
        if ( newIndex < 0 ) {
            newIndex = numbersWitoutAlice + newIndex;
        }
        else if ( newIndex > numbersWitoutAlice ) {
            newIndex = newIndex - numbersWitoutAlice;
        }
        else if ( newIndex == 0 && index != 0 ) {
            newIndex = numbersWitoutAlice;
        }
        return (int) newIndex;
    }

    List<CryptPair> parse( Stream<String> input, long key ) {
        return input.map( line -> newPair( key * Integer.parseInt( line ) ) ).toList();
    }

    record CryptPair(int id, long number, boolean moved) {
        @Override
        public String toString() {
            return number + "";
        }

        CryptPair reset() {
            return new CryptPair( id, number, false );
        }
    }
}
