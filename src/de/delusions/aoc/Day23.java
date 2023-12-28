package de.delusions.aoc;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day23 extends Day<Integer> {
    public Day23( Integer... expected ) {super( 23, "A Long Walk", expected );}

    static int MAX_PATH;

    static Coordinates GOAL;

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix park = Matrix.createFromStream( input );
        MAX_PATH = park.size() - park.findValues( '#', false ).size();
        GOAL = new Coordinates( park.getXLength() - 1, park.getYLength() - 2 );
        Dijkstra<LongestPath, Matrix> dijkstra = new Dijkstra<>( new LongestPath( new Coordinates( 0, 1 ), new ArrayList<>() ) );
        LongestPath bestPath = dijkstra.findBestPath( park );
        bestPath.wholePath().forEach( c -> park.setValue( c, 'O' ) );
        System.out.println( park );
        return bestPath.onThePath.size();
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }


    record LongestPath(Coordinates current, List<Coordinates> onThePath) implements Pathable<LongestPath, Integer, Matrix> {

        @Override
        public List<LongestPath> getNeighbors( Matrix theMap ) {
            return Direction.getBasic()
                            .stream()
                            .map( current::moveTo )
                            .filter( theMap::isInTheMatrix )
                            .filter( Predicate.not( onThePath::contains ) )
                            .filter( c -> theMap.getValue( c ) == '.' || theMap.getValue( c ) == c.getFacing().getSymbol().charAt( 0 ) )
                            .map( c -> new LongestPath( c, wholePath() ) )
                            .toList();
        }

        List<Coordinates> wholePath() {
            List<Coordinates> result = new ArrayList<>( onThePath );
            result.add( current );
            return result;
        }

        @Override
        public Integer distance() {
            return onThePath().size() + 4 * current.manhattanDistance( GOAL );
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
