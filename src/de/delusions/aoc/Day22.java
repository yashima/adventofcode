package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day22 extends Day<Integer> {
    Pattern p = Pattern.compile( "([0-9]+),([0-9]+),([0-9]+)~([0-9,]+),([0-9]+),([0-9]+)" );

    public Day22( Integer... expected ) {super( 22, "Sand Slabs", expected );}

    @Override
    public Integer part0( Stream<String> input ) {
        List<Brick> bricks = input.map( line -> {
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                int x1 = Integer.parseInt( m.group( 1 ) );
                int y1 = Integer.parseInt( m.group( 2 ) );
                int z1 = Integer.parseInt( m.group( 3 ) );
                int x2 = Integer.parseInt( m.group( 4 ) );
                int y2 = Integer.parseInt( m.group( 5 ) );
                int z2 = Integer.parseInt( m.group( 6 ) );
                return new Brick( new Dim( x1, y1, z1 ), new Dim( x2, y2, z2 ) );
            }
            return null;
        } ).toList();
        System.out.println( bricks );
        return 0;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    record Dim(int x, int y, int z) {}

    record Brick(Dim corner1, Dim corner2) {}
}
