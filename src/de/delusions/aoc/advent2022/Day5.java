package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
public class Day5
    extends Day<String> {
    final static Pattern day5MovePattern = Pattern.compile( "move ([0-9]+) from ([0-9]+) to ([0-9]+)" );

    final static Pattern day5StackPattern = Pattern.compile( ".(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)." );

    record Move(int number, int from, int to) {
    }

    Day5() {
        super( 5, "Supply Stacks" );
    }


    @Override
    public String part1( Stream<String> input ) {
        Map<Integer, LinkedList<String>> boxes = new HashMap<>();
        input.forEach( line -> {
            final Matcher stackMatcher = day5StackPattern.matcher( line );
            if ( stackMatcher.matches() ) {
                for ( int group = 1; group <= 9; group++ ) {
                    String match = stackMatcher.group( group ).trim();
                    if ( !match.isEmpty() && match.matches( "[A-Z]" ) ) {
                        boxes.putIfAbsent( group, new LinkedList<>() );
                        boxes.get( group ).add( match );
                    }
                }
            }
            final Matcher moveMatcher = day5MovePattern.matcher( line );
            if ( moveMatcher.matches() ) {
                Move move = new Move( Integer.parseInt( moveMatcher.group( 1 ) ), Integer.parseInt( moveMatcher.group( 2 ) ),
                                      Integer.parseInt( moveMatcher.group( 3 ) ) );
                LinkedList<String> lifter = new LinkedList<>();
                for ( int n = 0; n < move.number; n++ ) {
                    lifter.push( boxes.get( move.from ).pop() );
                }
                lifter.forEach( box -> boxes.get( move.to ).push( box ) );
            }
        } );
        return boxes.keySet().stream().sorted().map( key -> boxes.get( key ).pop() ).reduce( "", ( a, b ) -> a + b );
    }

    @Override
    public  String part2( Stream<String> input ) {
        //TODO (solved it but neglegcted to write code for both separately
        return null;
    }
}
