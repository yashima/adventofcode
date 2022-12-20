package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Interval;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day4 extends Day<Long> {

    static Pattern day4Pattern = Pattern.compile( "([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)" );

    Day4() {
        super( 4, "Camp Cleanup" );
    }

    @Override
    public Long part1( Stream<String> input ) {
        return input.map( this::parse ).filter( Objects::nonNull ).filter(
            pair -> pair.foo.overlap( pair.bar ) || pair.foo.contains( pair.bar ) ).count();
    }

    @Override
    public Long part2( Stream<String> input ) {
        return input.map( this::parse ).filter( Objects::nonNull ).filter(
            pair -> pair.foo.contains( pair.bar ) || pair.bar.contains( pair.foo ) ).count();
    }

    Pair parse( String input ) {
        final Matcher matcher = day4Pattern.matcher( input );
        return matcher.matches() ? new Pair( new Interval( matcher.group( 1 ), matcher.group( 2 ) ),
                                             new Interval( matcher.group( 3 ), matcher.group( 4 ) ) ) : null;
    }

    record Pair(Interval foo, Interval bar) {}

}
