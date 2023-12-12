package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day12 extends Day<Integer> {

    static Pattern SPRING_REG = Pattern.compile( "(\\?+|#+|\\.+)" );

    static Pattern NUMBER_REG = Pattern.compile( "(\\d+)" );

    public Day12( Integer... expected ) {
        super( 12, "Hot Springs", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        List<SpringRow> rows = input.map( SpringRow::create ).peek( System.out::println ).toList();
        return 0;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    enum SpringState {
        BROKEN, FINE, UNKNOWN;

        static SpringState getByChar( char c ) {
            return switch ( c ) {
                case '?' -> UNKNOWN;
                case '.' -> FINE;
                case '#' -> BROKEN;
                default -> null;
            };
        }
    }

    record SpringGroup(SpringState state, int length) {
        static SpringGroup create( String group ) {
            return new SpringGroup( SpringState.getByChar( group.charAt( 0 ) ), group.length() );
        }
    }

    record SpringRow(List<SpringGroup> groups, List<Integer> brokenGroups) {
        static SpringRow create( String line ) {
            Matcher matcher = SPRING_REG.matcher( line );
            List<SpringGroup> groups = new ArrayList<>();
            while ( matcher.find() ) {
                groups.add( SpringGroup.create( matcher.group( 1 ) ) );
            }
            matcher = NUMBER_REG.matcher( line );
            List<Integer> broken = new ArrayList<>();
            while ( matcher.find() ) {
                broken.add( Integer.parseInt( matcher.group( 1 ) ) );
            }
            return new SpringRow( groups, broken );
        }
    }
}
