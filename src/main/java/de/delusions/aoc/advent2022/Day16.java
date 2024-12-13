package de.delusions.aoc.advent2022;

import de.delusions.util.Day;
import de.delusions.util.Path;
import de.delusions.util.PathNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 extends Day<Integer> {

    static String START = "AA";

    static AtomicInteger ID = new AtomicInteger( 0 );

    static int TIME = 30;

    static Map<String, Valve> VALVES;

    private Integer[][] pathMatrix;

    private Valve[] valveLine;

    private List<Valve> workingValves;

    Day16() {
        super( 16, "Proboscidea Volcanium" );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        init( input );
        List<Path<Valve>> paths = new ArrayList<>( List.of( new Path<Valve>( null, VALVES.get( START ), 0, 0 ) ) );
        List<Path<Valve>> results = getPaths( paths, this::getLegalChildPaths );
        return results.stream().map( Path::getTotalHeuristic ).max( Integer::compareTo ).orElse( 0 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        //no need to init again?
        List<Path<Valve>> paths = new ArrayList<>( List.of( new Path<Valve>( null, VALVES.get( START ), 4, 0 ) ) );
        Map<Path<Valve>, Set<Integer>> results =
            getPaths( paths, this::getLegalChildPaths2 ).stream().collect( Collectors.toMap( Function.identity(), Path::getNodeIdsWithoutStart ) );
        Set<Integer> unions = new HashSet<>();

        results.keySet().parallelStream().forEach( path -> {
            results.keySet().parallelStream().forEach( compare -> {
                if ( results.get( path ).stream().noneMatch( a -> results.get( compare ).contains( a ) ) ) {
                    unions.add( path.getTotalHeuristic() + compare.getTotalHeuristic() );
                }
            } );
        } );
        return unions.stream().max( Integer::compareTo ).orElse( -1 );
    }


    <T> List<T> getPaths( List<T> paths, Function<T, List<T>> getChildPaths ) {
        List<T> results = new ArrayList<>();
        while ( !paths.isEmpty() ) {
            List<T> candidates = new ArrayList<>();
            for ( T path : paths ) {
                List<T> childPaths = getChildPaths.apply( path );
                if ( childPaths.isEmpty() ) {
                    results.add( path );
                }
                else {
                    candidates.addAll( childPaths );
                }
            }
            paths = candidates;
        }
        return results;
    }

    boolean isLegal( Path<Valve> path, Valve v ) {
        return path.getTotalCost() + cost( path, v ) < 30 && !path.contains( v );
    }

    boolean isLegal2( Path<Valve> path, Valve v ) {
        return path.length() <= workingValves.size() / 2 && path.getTotalCost() + cost( path, v ) < 30 && !path.contains( v );
    }

    //testdata 1651
    List<Path<Valve>> getLegalChildPaths( Path<Valve> parent ) {
        //+1: turning on a valve is +1
        return workingValves.stream().filter( v -> isLegal( parent, v ) ).map( valve -> {
            int cost = cost( parent, valve );
            return new Path<>( parent, valve, cost, ( TIME - ( parent.getTotalCost() + cost ) ) * valve.flowRate );
        } ).toList();
    }

    List<Path<Valve>> getLegalChildPaths2( Path<Valve> parent ) {
        //+1: turning on a valve is +1
        return workingValves.stream().filter( v -> isLegal2( parent, v ) ).map( valve -> {
            int cost = cost( parent, valve );
            return new Path<>( parent, valve, cost, ( TIME - ( parent.getTotalCost() + cost ) ) * valve.flowRate );
        } ).toList();
    }


    int cost( Path<Valve> parent, Valve valve ) {
        return pathMatrix[parent.getNode().id][valve.id] + 1;
    }

    void init( Stream<String> input ) {
        VALVES = input.map( Valve::new ).collect( Collectors.toMap( Valve::getName, v -> v ) );
        valveLine = VALVES.values().stream().sorted( Comparator.comparing( v -> v.id ) ).toList().toArray( new Valve[VALVES.size()] );
        workingValves = VALVES.values().stream().filter( Valve::hasFlow ).toList();
        pathMatrix = new Integer[VALVES.size()][VALVES.size()];
        VALVES.keySet().stream().map( VALVES::get ).forEach( valve -> {
            Integer[] line = pathMatrix[valve.id];
            line[valve.id] = 0;
            AtomicInteger steps = new AtomicInteger( 0 );
            while ( Arrays.stream( line ).anyMatch( Objects::isNull ) ) {
                steps.incrementAndGet();
                IntStream.range( 0, line.length ).filter( i -> line[i] != null && line[i] == steps.get() - 1 ).forEach( i -> {
                    for ( Valve child : valveLine[i].getChildren() ) {
                        if ( line[child.id] == null ) {
                            line[child.id] = steps.get();
                        }
                    }
                } );
            }
        } );
    }

    String printMatrix( Valve[] valveLine, Integer[][] matrix ) {
        StringBuilder builder = new StringBuilder();
        int size = valveLine.length;
        builder.append( "    " );
        for ( int i = 0; i < size; i++ ) {
            builder.append( valveLine[i].name ).append( "  " );
        }
        builder.append( "\n" );
        for ( int i = 0; i < size; i++ ) {
            builder.append( valveLine[i].name ).append( "  " );
            for ( int j = 0; j < size; j++ ) {
                builder.append( " " ).append( matrix[i][j] ).append( "  " );
            }
            builder.append( "\n" );
        }
        return builder.toString();
    }


    static class Valve implements PathNode<Valve>, Comparable<Valve> {
        static Pattern pattern = Pattern.compile( "Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.*)" );

        String name;

        int flowRate;

        Map<String, Integer> tunnelsTo;

        int id;

        Valve( String line ) {
            id = ID.getAndIncrement();
            Matcher matcher = pattern.matcher( line );
            if ( matcher.matches() ) {
                this.name = matcher.group( 1 );
                this.flowRate = Integer.parseInt( matcher.group( 2 ) );
                this.tunnelsTo =
                    Arrays.stream( matcher.group( 3 ).split( "," ) ).map( String::trim ).collect( Collectors.toMap( Function.identity(), v -> 1 ) );
            }
            else {
                throw new IllegalStateException( "no valve: " + line );
            }
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo( Valve o ) {
            return Integer.compare( flowRate, o.flowRate );
        }

        @Override
        public int hashCode() {
            return Objects.hash( getName() );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof Valve valve ) ) {
                return false;
            }
            return getName().equals( valve.getName() );
        }

        @Override
        public String toString() {
            return getName() + "(" + flowRate + ")";
        }

        @Override
        public int getValue() {
            return flowRate;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public List<Valve> getChildren() {
            return tunnelsTo.keySet().stream().map( VALVES::get ).toList();
        }

        public boolean hasFlow() {
            return flowRate > 0;
        }

    }

}
