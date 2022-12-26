package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day20 extends Day<Integer> {

    static AtomicInteger ID = new AtomicInteger();

    LinkedList<CryptPair> numbers;

    Integer ZERO = null;

    public Day20() {
        super( 20, "Grove Positioning System" );
    }

    static CryptPair newPair( int number ) {
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

    int getValueAtPosition( int index ) {
        return numbers.get( ( index + getZeroPosition() ) % ( numbers.size() ) ).number;
    }

    //14306 too low
    @Override
    public Integer part1( Stream<String> input ) {
        numbers = new LinkedList<>( parse( input ) );
        mixNumbers();
        System.out.println( numbers );
        return getValueAtPosition( 1000 ) + getValueAtPosition( 2000 ) + getValueAtPosition( 3000 );
    }

    void mixNumbers() {
        CryptPair nextPair = getNext();
        while ( nextPair != null ) {
            movePair( nextPair );
            nextPair = getNext();
        }
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

    int calculateNewIndex( CryptPair nextPair, int index ) {
        int newIndex = index + nextPair.number; //simple sum suffices for many non-loops
        int numbersWitoutAlice = numbers.size() - 1;
        if ( newIndex < 0 ) { //step left
            newIndex = numbersWitoutAlice + newIndex % numbersWitoutAlice;
        }
        else if ( newIndex > numbersWitoutAlice ) { //loop right
            newIndex = nextPair.number() % numbersWitoutAlice;
        }
        else if ( newIndex == 0 && index != 0 ) { //the edge goes to the end
            newIndex = numbersWitoutAlice;
        }
        return newIndex;
    }

    CryptPair getById( int id ) {
        return numbers.stream().filter( cp -> cp.id == id ).findFirst().orElse( null );
    }

    List<CryptPair> parse( Stream<String> input ) {
        return input.map( line -> newPair( Integer.parseInt( line ) ) ).toList();
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
