package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 extends Day<Integer> {

    static Pattern SPRING_REG = Pattern.compile( "([#\\?]+)" );

    static Pattern NUMBER_REG = Pattern.compile( "(\\d+)" );

    static String DIVIDER = "[\\.\\?]{%d}";

    static String BROKEN_SEQUENCE = "[#\\?]{%d}";

    public Day12( Integer... expected ) {
        super( 12, "Hot Springs", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        List<SpringRow> rows = input.map( SpringRow::create ).toList();
        int configurations = 0;
        for ( SpringRow row : rows ) {
            System.out.println( "\n----\n" + row );
            String regexFormat = row.convertGroupsToString();
            System.out.println( row.convertGroupsToString() );
            if ( regexFormat == null ) {
                System.out.println( "oops" );
                configurations++;
                break;
            }
            for ( int i = row.minDigits(); i < row.maxDigits(); i++ ) {
                Integer[] digits = digits( i, row.brokenGroups().size() );
                if ( row.validDigits( digits ) ) {
                    System.out.print( i + " " );
                    String regex = String.format( regexFormat, (Object[]) digits );
                    Matcher m = Pattern.compile( regex ).matcher( row.original );
                    if ( m.find() ) {
                        System.out.println( i + " " + row.original + " -> " + row.variant( digits ) );
                        configurations++;
                    }
                }
            }
        }
        return configurations;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    /**
     * Converts a number into an array of digits with a leading zero if necessary
     *
     * @param number the number to be split into digits
     * @param needed the number of digits needed
     * @return an array of size needed with the digits of the number
     */
    static Integer[] digits( Integer number, Integer needed ) {
        LinkedList<Integer> result = new LinkedList<>();
        while ( number > 0 ) {
            result.push( number % 10 );
            number /= 10;
        }
        if ( result.size() < needed ) {
            result.push( 0 );
        }
        return result.toArray( new Integer[0] );
    }

    record SpringRow(String original, List<String> groups, List<Integer> brokenGroups) {
        static SpringRow create( String line ) {
            Matcher matcher = SPRING_REG.matcher( line );
            List<String> groups = new ArrayList<>();
            while ( matcher.find() ) {
                groups.add( matcher.group( 1 ) );
            }
            matcher = NUMBER_REG.matcher( line );
            List<Integer> broken = new ArrayList<>();
            while ( matcher.find() ) {
                broken.add( Integer.parseInt( matcher.group( 1 ) ) );
            }
            return new SpringRow( line.split( " " )[0], groups, broken );
        }

        boolean validDigits( Integer[] digits ) {
            return Arrays.stream( digits ).skip( 1 ).noneMatch( i -> i == 0 ) && Arrays.stream( digits ).mapToInt( i -> i ).sum() == notBroken();
        }

        int notBroken() {
            return original.length() - brokenGroups.stream().mapToInt( i -> i ).sum();
        }

        int maxDigits() {
            return (int) Math.pow( 10, brokenGroups.size() + 1 ) - 1;
        }

        int minDigits() {
            return (int) Math.pow( 10, brokenGroups.size() - 1 );
        }

        String variant( Integer[] dividers ) {
            StringBuilder b = new StringBuilder();
            for ( int i = 0; i < brokenGroups.size(); i++ ) {
                b.append( springs( '.', dividers[0] ) );
                b.append( springs( '#', brokenGroups.get( i ) ) );
            }
            return b.toString();
        }

        String springs( char type, int length ) {
            int[] springs = new int[length];
            Arrays.fill( springs, type );
            return Arrays.stream( springs ).mapToObj( i -> ( (char) i ) + "" ).collect( Collectors.joining() );
        }


        String convertGroupsToString() {
            int max = notBroken() + brokenGroups().size() - 1;
            if ( original.length() == max ) { //this means there can be only exactly 1 position
                return null;
            }
            StringBuilder builder = new StringBuilder();
            //builder.append("^");
            for ( int group : brokenGroups ) {
                builder.append( DIVIDER );
                builder.append( String.format( BROKEN_SEQUENCE, group ) );
            }
            return builder.toString();
        }
    }


}
