package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day01 extends Day<Long>
{
    public Day01(Long... expected )
    {
        super( 1, "Trebuchet",expected );
    }

    private static final Long DIGIT_OFFSET=48L;
    private static final Map<String,String>
        NUMBERS = Map.of( "one", "1", "two", "2", "three", "3", "four", "4", "five", "5", "six", "6", "seven", "7", "eight", "8", "nine", "9");
    private static final Pattern P = Pattern.compile( "(\\d|one|two|three|four|five|six|seven|eight|nine)" );

    static long findNumber( String line){
        long[] array = line.chars().filter( i -> i < 59 && i > 47 ).asLongStream().toArray();
        return ( array[0] - DIGIT_OFFSET ) * 10 + array[array.length - 1] - DIGIT_OFFSET;
    }

    static long findNumberWithWords( String line){
        Matcher matcher = P.matcher( line );
        List<Long> digitsFound = new ArrayList<>();
        while( matcher.find() ){
            String match = matcher.group( 0 );
            digitsFound.add( Long.valueOf( NUMBERS.getOrDefault(match,match) ) );
        }
        return digitsFound.get( 0 )*10 + digitsFound.get( digitsFound.size()-1 );
    }

    @Override
    public Long part0( Stream<String> input )
    {
        return input.mapToLong( Day01::findNumber ).sum();
    }

    @Override
    public Long part1( Stream<String> input )
    {
        return input.mapToLong( Day01::findNumberWithWords ).sum();
    }
}
