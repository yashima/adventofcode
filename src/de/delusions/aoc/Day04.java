package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day04 extends Day<Integer> {
    public Day04( Integer... expected ) {
        super( 4, "Scratchcards", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return input.map( this::countWinningNumbers ).mapToInt( n -> n == 0 ? 0 : (int) Math.pow( 2, n - 1 ) ).sum();
    }

    private int countWinningNumbers( String scratchCard ) {
        //stupid single digits. Apparently there are no triple digits or else...
        scratchCard = scratchCard.replaceAll( "  ", " 0" );
        String winnersRegex = scratchCard.substring( scratchCard.indexOf( ":" ) + 1, scratchCard.indexOf( "|" ) ).trim().replace( ' ', '|' );
        String numbers = scratchCard.substring( scratchCard.indexOf( "|" ) );
        Matcher matcher = Pattern.compile( winnersRegex ).matcher( numbers );
        int count = 0;
        while ( matcher.find() ) {count++;}
        return count;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }
}
