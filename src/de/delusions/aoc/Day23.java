package de.delusions.aoc;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 extends Day<Integer> {
    public Day23( Integer... expected ) {super( 23, "A Long Walk", expected );}

    static Coordinates GOAL;

    static Coordinates START = new Coordinates( 0, 1 );

    static int PART = 0;

    Matrix park;

    @Override
    public Integer part0( Stream<String> input ) {
        PART = 0;
        return findAPath( input );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        park = Matrix.createFromStream( input );
        GOAL = new Coordinates( park.getXLength() - 1, park.getYLength() - 2 );
        park.setValue( START, 'X' );
        park.setValue( GOAL, 'X' );

        Map<Coordinates, Crossing> graph = findCrossings();
        graph.values().forEach( c -> park.setValue( c.coordinates, 'X' ) );
        graph.values().forEach( this::calculateEdgeLengths );
        System.out.println( park );
        return 1;
    }

    Map<Coordinates, Crossing> findCrossings() {
        return park.findValues( '.', false )
                   .stream()
                   .filter( this::isCrossing )
                   .collect( Collectors.toMap( c -> c, c -> new Crossing( c, new HashMap<>() ) ) );
    }

    //TODO does not work.
    void calculateEdgeLengths( Crossing crossing ) {
        Set<Coordinates> visited = new HashSet<>();
        Set<Coordinates> endpoints = new HashSet<>();
        Stack<Coordinates> opens = new Stack<>();
        getNextSteps( crossing.coordinates, park ).forEach( opens::push );
        while ( !opens.isEmpty() ) {
            Coordinates current = opens.pop();
            if ( visited.contains( current ) ) {
                //do nothing we've been here
                continue;
            }
            List<Coordinates> next = getNextSteps( current, park );
            if ( next.size() > 2 ) {
                //found a neighbor crossing
                endpoints.add( current );
                continue;
            }
            visited.add( current );
            next.forEach( s -> {
                s.setValue( current.getValue() + 1 );
                opens.push( s );
            } );
        }
        endpoints.forEach( e -> {
            crossing.neighbors.put( e, e.getValue() );
        } );
    }

    boolean isCrossing( Coordinates candidate ) {
        long possibleDirections =
            Direction.getBasic().stream().map( candidate::moveTo ).filter( park::isInTheMatrix ).filter( c -> park.getValue( c ) != '#' ).count();
        return possibleDirections > 2;
    }

    static List<Coordinates> getNextSteps( Coordinates current, Matrix park ) {
        return Direction.getBasic().stream().map( current::moveTo ).filter( park::isInTheMatrix ).filter( c -> isValidStep( c, park ) ).toList();
    }

    static boolean isValidStep( Coordinates c, Matrix park ) {
        int value = park.getValue( c );
        return ( PART == 0 && ( value == '.' || value == c.getFacing().getSymbol().charAt( 0 ) ) ) || ( PART == 1 && value != '#' );
    }

    int findAPath( Stream<String> input ) {
        park = Matrix.createFromStream( input );
        GOAL = new Coordinates( park.getXLength() - 1, park.getYLength() - 2 );
        Dijkstra<LongestPath, Matrix> dijkstra = new Dijkstra<>( new LongestPath( START, 0, new ArrayList<>() ) );
        LongestPath bestPath = dijkstra.findBestPath( park );
        bestPath.wholePath().forEach( c -> park.setValue( c, 'O' ) );
        //System.out.println( park );
        return bestPath.onThePath.size();
    }

    record Crossing(Coordinates coordinates, Map<Coordinates, Integer> neighbors) {}

    record LongestPath(Coordinates current, int steps, List<Coordinates> onThePath) implements Pathable<LongestPath, Integer, Matrix> {

        @Override
        public List<LongestPath> getNeighbors( Matrix theMap ) {
            return getNextSteps( current, theMap ).stream()
                                                  .filter( Predicate.not( onThePath::contains ) )
                                                  .map( c -> new LongestPath( c, steps + 1 + ( theMap.getValue( c ) != '.' ? 10 : 0 ), wholePath() ) )
                                                  .toList();
        }

        List<Coordinates> wholePath() {
            List<Coordinates> result = new ArrayList<>( onThePath );
            result.add( current );
            return result;
        }

        @Override
        public Integer distance() {
            return steps + 10 * current.manhattanDistance( GOAL );
        }

        @Override
        public boolean goal( Matrix theMap ) {
            return this.current.equals( GOAL );
        }

        @Override
        public LongestPath previous() {
            return null;
        }

        @Override
        public int compareTo( LongestPath o ) {
            if ( this.distance().equals( o.distance() ) ) {return 0;}
            return this.distance() > o.distance() ? -1 : 1; //we want longer paths with more steps!
        }

    }
}
