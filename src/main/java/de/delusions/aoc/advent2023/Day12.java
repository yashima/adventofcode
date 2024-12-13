package main.java.de.delusions.aoc.advent2023;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //gets all the configurations for a single row
    static int getConfigurations( SpringRow row ) {
        Stack<SpringRow> opens = new Stack<>();
        opens.push( row );
        int configurations = 0;
        while ( !opens.isEmpty() ) {
            SpringRow current = opens.pop();
            if ( current.isDone() ) {
                configurations++;
            }
            else {
                SpringRow.getNext( current ).stream().filter( Objects::nonNull ).forEach( opens::push );
            }

        }
        return configurations;
    }

    static boolean isImpossible( List<String> groups, List<Integer> broken ) {
        List<String> prefix = groups.stream().filter( g -> g.matches( "#*" ) ).toList();
        return !prefix.stream().map( String::length ).toList().equals( broken.subList( 0, groups.size() ) );
    }



    private static List<String> createGroups( String line ) {
        Matcher matcher = SPRING_REG.matcher( line );
        List<String> groups = new ArrayList<>();
        while ( matcher.find() ) {
            groups.add( matcher.group( 1 ) );
        }
        return groups;
    }

    record SpringRow(List<String> groups, List<Integer> brokenGroups) {

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
            return new SpringRow( groups, foldedBroken );
        }

        static boolean allBroken( String group ) {
            return !group.isBlank() && group.indexOf( '?' ) < 0;
        }

        static boolean allUnknown( String group ) {
            return !group.isBlank() && group.indexOf( '#' ) < 0;
        }

        public static String splitOff( String group, int broken ) {
            if ( group.length() == broken ) { //group should have been removed by prefix remove but never mind
                return "";
            }
            else if ( group.startsWith( "?" ) //this will likely necessitate 2 branches, should be caught elsewhere
                || group.length() < broken //definitely not a match, should also be caught elsewhere
                || group.charAt( broken ) ==
                '#' ) { //this one us yucky, it means that there is no possible break between groups, probably please catch elsewhere

                //summary: this is not a valid result, we should not be here, sadly for various reasons
                return null;
            }
            return group.substring( broken + 1 ); //eliminating the break as we know we should
        }

        static List<SpringRow> getNext( SpringRow oldRow ) {
            while ( !oldRow.isDone() && oldRow.removeUnambiguousHead() ) {
                //do nothing?
            }
            List<SpringRow> result = new ArrayList<>();
            SpringRow row = oldRow;
            if ( row.isDone() ) {
                result.add( row );
                return result;
            }
            else if ( row.groups().isEmpty() || row.brokenGroups().isEmpty() ) {
                return result;
            }
            String group = row.groups().getFirst();
            int broken = row.brokenGroups().getFirst();
            if ( group.startsWith( "?" ) ) {//make two branches one with . and one with #
                result.add( makeAChoicyRow( row, group.substring( 1 ) ) );
                result.add( makeAChoicyRow( row, group.replaceFirst( "\\?", "#" ) ) );
            }
            else {
                //we're not starting with ? but with #, so that # must be part of the first group, try to split it off:
                String remaining = splitOff( group, broken );

                //null indicates some kind of failure, in any case the branch ends.
                if ( remaining != null ) {
                    //split was successful and matched the first group:
                    row.groups().removeFirst();
                    if ( !remaining.isBlank() ) {
                        row.groups().addFirst( remaining );
                    }
                    row.brokenGroups().removeFirst();
                    if ( !row.isEarlyMismatch() ) {
                        result.add( row );
                    }
                    else {
                        //  System.err.println(row);
                    }
                }
            }
            return result;
        }

        private static SpringRow makeAChoicyRow( SpringRow row, String choicyGroup ) {
            //replace the first '?'
            SpringRow choicyRow = new SpringRow( new ArrayList<>( row.groups() ), new ArrayList<>( row.brokenGroups() ) );
            choicyRow.groups().removeFirst();
            choicyRow.groups().addFirst( choicyGroup );
            if ( choicyRow.isEarlyMismatch() ) {
                //    System.err.println("Oops next nono "+choicyRow);
                return null;
            }
            return choicyRow;
        }

        boolean allMatch( List<String> candidate ) {
            return brokenGroups().equals( candidate.stream().map( String::length ).toList() );
        }

        public boolean allMatch() {
            return brokenGroups.equals( groups.stream().map( String::length ).toList() );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof SpringRow springRow ) ) {return false;}
            return Objects.equals( groups, springRow.groups ) && Objects.equals( brokenGroups, springRow.brokenGroups );
        }

        @Override
        public int hashCode() {
            return Objects.hash( groups, brokenGroups );
        }

        public boolean isDone() {
            return brokenGroups.isEmpty() && ( groups.isEmpty() || groups.stream().noneMatch( s -> s.indexOf( '#' ) > 0 ) ) || allMatch();
        }

        public boolean removeUnambiguousHead() {
            boolean result = false;
            if ( !groups.isEmpty() ) {
                String first = groups.getFirst();
                if ( allUnknown( first ) && ( brokenGroups.isEmpty() || brokenGroups.getFirst() > first.length() ) ) {
                    groups.removeFirst();
                    result = true;
                }
                else if ( allBroken( first ) && !brokenGroups.isEmpty() && brokenGroups.getFirst() == first.length() ) {
                    groups.removeFirst();
                    brokenGroups.removeFirst();
                    result = true;
                }
            }
            return result;
        }

        public boolean isEarlyMismatch() {
            String group = groups.isEmpty() ? "" : groups.getFirst();
            int broken = brokenGroups().isEmpty() ? -1 : brokenGroups().getFirst();
            return !isDone() && ( allBroken( group ) && group.length() != broken );
        }

    }


}
