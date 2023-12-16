package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day12 extends Day<Integer> {

    static Pattern SPRING_REG = Pattern.compile( "([#\\?]+)" );

    static Pattern NUMBER_REG = Pattern.compile( "(\\d+)" );

    public Day12( Integer... expected ) {
        super( 12, "Hot Springs", expected );
    }


    static int folding = 1;

    @Override
    public Integer part0( Stream<String> input ) {
        folding = 1;
        List<SpringRow> rows = input.map( SpringRow::create ).toList();
        return countAllRowConfigurations( rows );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        folding = 5;
        List<SpringRow> rows = input.map( SpringRow::create ).toList();
        return countAllRowConfigurations( rows );
    }

    static int countAllRowConfigurations( List<SpringRow> rows ) {
        int configurations = 0;
        for ( SpringRow row : rows ) {
            int rowConfigs = getConfigurations( row );
            configurations += rowConfigs;
            System.out.println( row + "configurations=" + rowConfigs );
        }
        return configurations;
    }

    static int getConfigurations( SpringRow row ) {
        Stack<List<String>> opens = new Stack<>();
        opens.push( row.groups() );
        int configurations = 0;
        while ( !opens.isEmpty() ) {
            List<String> next = opens.pop();
            if ( next != null ) {
                if ( next.stream().noneMatch( g -> g.contains( "?" ) ) ) {
                    if ( row.matches( next ) ) {
                        configurations++;
                    }
                }
                else {
                    opens.push( generateCandidate( next, '#', row ) );
                    opens.push( generateCandidate( next, '.', row ) );
                }
            }
        }
        return configurations;
    }

    static boolean isImpossible( List<String> groups, List<Integer> broken ) {
        List<String> prefix = groups.stream().filter( g -> g.matches( "#*" ) ).toList();
        return !prefix.stream().map( String::length ).toList().equals( broken.subList( 0, groups.size() ) );
    }

    private static List<String> generateCandidate( List<String> next, char replace, SpringRow row ) {
        List<String> candidate = new ArrayList<>();
        boolean foundUnknown = false;
        for ( int i = 0; i < next.size(); i++ ) {
            String group = next.get( i );
            if ( foundUnknown ) {
                candidate.add( group );
            }
            else if ( group.contains( "?" ) ) {
                foundUnknown = true;
                if ( replace == '.' ) {
                    int unknownIndex = group.indexOf( '?' );
                    if ( unknownIndex == 0 ) {
                        candidate.add( group.substring( 1 ) );
                    }
                    else if ( unknownIndex == group.length() - 1 ) {
                        candidate.add( group.substring( 0, unknownIndex ) );
                    }
                    else {
                        candidate.add( group.substring( 0, unknownIndex ) );
                        candidate.add( group.substring( unknownIndex + 1 ) );
                    }
                }
                else { //#
                    candidate.add( group.replaceFirst( "\\?", "" + replace ) );
                }
            }
            else {
                if ( group.length() == row.brokenGroups().get( i ) ) {
                    candidate.add( group );
                }
                else {
                    return null;
                }
            }
        }
        return candidate;
    }


    private static List<String> createGroups( String line ) {
        Matcher matcher = SPRING_REG.matcher( line );
        List<String> groups = new ArrayList<>();
        while ( matcher.find() ) {
            groups.add( matcher.group( 1 ) );
        }
        return groups;
    }

    record SpringRow(String original, List<String> groups, List<Integer> brokenGroups) {
        static SpringRow create( String line ) {
            Matcher matcher = NUMBER_REG.matcher( line );
            List<Integer> broken = new ArrayList<>();
            while ( matcher.find() ) {
                broken.add( Integer.parseInt( matcher.group( 1 ) ) );
            }
            String original = line.split( " " )[0];
            List<Integer> foldedBroken = new ArrayList<>();
            StringBuilder foldedOriginal = new StringBuilder();
            for ( int i = 0; i < folding; i++ ) {
                foldedOriginal.append( original );
                if ( i < folding - 1 ) {foldedOriginal.append( "?" );}
                foldedBroken.addAll( broken );
            }
            List<String> groups = createGroups( foldedOriginal.toString() );
            return new SpringRow( foldedOriginal.toString(), groups, foldedBroken );
        }

        boolean matches( List<String> candidate ) {
            return brokenGroups().equals( candidate.stream().map( String::length ).toList() );
        }

    }


}
