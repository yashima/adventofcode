package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day20 extends Day<Integer> {

    AtomicInteger ID = new AtomicInteger( 0 );

    LinkedList<CryptPair> numbers;

    Integer ZERO = null;

    public Day20() {
        super( 20, "Grove Positioning System" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        numbers = new LinkedList<>( parse( input ) );
        CryptPair nextPair = getNext();
        while ( nextPair != null ) {
            movePair( nextPair );
            nextPair = getNext();
            System.out.println( numbers );
        }
        return getValueAtPosition( 1000 ) + getValueAtPosition( 2000 ) + getValueAtPosition( 3000 );
    }

    int getZeroPosition() {
        if ( ZERO == null ) {
            CryptPair zero = numbers.stream().filter( p -> p.number == 0 ).findFirst().get();
            ZERO = numbers.indexOf( zero );
        }
        return ZERO;
    }

    int getValueAtPosition( int index ) {
        return numbers.get( ( index + getZeroPosition() ) % ( numbers.size() ) ).number;
    }

    private void movePair( CryptPair nextPair ) {
        int index = numbers.indexOf( nextPair );
        int newIndex = index + nextPair.number;
        if ( newIndex < 0 ) {
            newIndex = numbers.size() + newIndex - 1;
        }
        else if ( newIndex > numbers.size() ) {
            newIndex = newIndex % numbers.size() + 1;
        }
        else if ( newIndex == 0 && index != 0 ) {
            newIndex = numbers.size() - 1;
        }
        numbers.remove( index );
        numbers.add( newIndex, new CryptPair( nextPair.id, nextPair.number, true ) );
    }

    private CryptPair getNext() {
        return numbers.stream().filter( Predicate.not( CryptPair::moved ) ).findFirst().orElse( null );
    }

    List<CryptPair> parse( Stream<String> input ) {
        return input.map( line -> new CryptPair( ID.getAndIncrement(), Integer.parseInt( line ), false ) ).toList();
    }


    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    record CryptPair(int id, int number, boolean moved) {
        @Override
        public String toString() {
            return number + "";
        }
    }
}
