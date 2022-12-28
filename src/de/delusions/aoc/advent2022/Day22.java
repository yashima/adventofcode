package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Matrix;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day22 extends Day<Integer> {

    static final int WALL = 2;

    static final int FLOOR = 1;

    static final int VOID = 0;

    static final Map<Character, Integer> characterMapping = Map.of( '#', WALL, '.', FLOOR, ' ', VOID, '\n', VOID );

    final String mapRegex = "[#\\. ]*";

    AtomicReference<String> commands = new AtomicReference<>();

    public Day22() {
        super( 22, "Monkey Map" );
    }

    Matrix map;

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        map = parse( input );
        System.out.println( commands.get() );
        return solve();
    }

    int solve() {
        return 0;
    }

    Matrix parse( Stream<String> input ) {
        return new Matrix( input.map( line -> mapLine( line ) ).filter( Objects::nonNull ).toArray( int[][]::new ) );
    }

    int[] mapLine( String line ) {
        if ( line.matches( mapRegex ) ) {
            return line.chars().map( c -> characterMapping.get( (char) c ) ).toArray();
        }
        else if ( !line.isEmpty() ) {
            commands.set( line );
        }
        return null;
    }
}
