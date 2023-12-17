package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day17 extends Day<Integer> {
    public Day17( Integer... expected ) {
        super( 17, "Clumsy Crucible", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix industrialComplex = Matrix.createFromStream( input );
        return findBestPath( industrialComplex, 3 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix industrialComplex = Matrix.createFromStream( input );
        return findBestPath( industrialComplex, 4 );
    }

    static int findBestPath( Matrix industrialComplex, int maxInstability ) {
        PriorityQueue<Crucible> opens = new PriorityQueue<>();
        Map<Coordinates, Crucible> visited = new HashMap<>();

        Crucible start = new Crucible( new Coordinates( 0, 0 ), 0, null, 0, null );
        visited.put( start.position(), start );
        opens.add( start );

        while ( !opens.isEmpty() ) {
            Crucible crucible = opens.poll();
            if ( crucible.goal( industrialComplex ) ) { //we're done, the lava has arrived
                return reconstructPath( crucible, industrialComplex );
            }
            //ah well, this one has been visited
            if ( !visited.containsKey( crucible.position() ) ) {
                visited.put( crucible.position(), crucible );
            }

            crucible.getNeighbors( industrialComplex, maxInstability ).forEach( n -> {
                if ( !visited.containsKey( n.position() ) && !opens.contains( n ) ) {
                    opens.add( n );
                }
                else {
                    if ( opens.contains( n ) ) {
                        Crucible old = opens.stream().filter( c -> c.position().equals( n.position() ) ).findFirst().get();
                        if ( old.distance() > n.distance() ) {
                            opens.remove( old );
                            opens.add( n );
                        }
                    }
                }
            } );
        }
        return -1;
    }

    private static int reconstructPath( Crucible crucible, Matrix industrialComplex ) {
        int totalHeatLoss = crucible.heatLoss();
        while ( crucible.previous() != null ) {
            industrialComplex.setValue( crucible.position(), crucible.facing().getSymbol().charAt( 0 ) );
            crucible = crucible.previous();
        }
        System.out.println( industrialComplex );
        return totalHeatLoss;
    }

    record Crucible(Coordinates position, int heatLoss, Direction facing, int instability, Crucible previous) implements Comparable<Crucible> {

        List<Crucible> getNeighbors( Matrix industrialComplex, int maxInstability ) {
            List<Crucible> neighbors = new ArrayList<>();
            Direction.getBasic().forEach( d -> {
                Coordinates nextPosition = position.moveTo( d, 1 );

                if ( industrialComplex.isInTheMatrix( nextPosition ) ) { //do not consider stuff outside the matrix, duh
                    int losingMoreHeat = industrialComplex.getValue( nextPosition ) - 48; //still storing characters so I need the ascii offset
                    if ( d == facing ) {
                        if ( instability < maxInstability ) { //current direction we were going, still stable?
                            neighbors.add( new Crucible( nextPosition, heatLoss() + losingMoreHeat, d, instability() + 1, this ) );
                        }
                    }
                    else if ( facing == null || d != facing().opposite() ) { //we are not going back, ever!
                        neighbors.add( new Crucible( nextPosition, heatLoss() + losingMoreHeat, d, 1, this ) );
                    }
                }
            } );
            return neighbors;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof Crucible crucible ) ) {return false;}
            return Objects.equals( position, crucible.position );
        }

        @Override
        public int hashCode() {
            return Objects.hash( position );
        }

        @Override
        public String toString() {
            return "Crucible{" + "position=" + position + ", heatLoss=" + heatLoss + ", facing=" + facing + ", instability=" + instability + '}';
        }

        boolean goal( Matrix industrialComplex ) {
            return position.equals( new Coordinates( industrialComplex.getXLength() - 1, industrialComplex.getYLength() - 1 ) );
        }

        @Override
        public int compareTo( Crucible o ) {
            if ( this.distance() == o.distance() ) {return 0;}
            return this.distance() < o.distance() ? -1 : 1;
        }

        int distance() {
            return heatLoss + instability * 2;
        }
    }

}
