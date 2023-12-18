package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

public class Day17 extends Day<Integer> {

    public static final int STRAIGHT_AFTER_TURN = 1;

    public Day17( Integer... expected ) {
        super( 17, "Clumsy Crucible", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix industrialComplex = Matrix.createFromStream( input );
        return findBestPath( industrialComplex, 3, 1 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix industrialComplex = Matrix.createFromStream( input );
        return findBestPath( industrialComplex, 10, 2 );
    }

    static int findBestPath( Matrix industrialComplex, int maxStraight, int minStraight ) {
        PriorityQueue<Crucible> opens = new PriorityQueue<>();
        Set<Crucible> visited = new HashSet<>();

        //we don't really need start in visited because it gets added in the loop
        opens.add( new Crucible( new Coordinates( 0, 0 ), 0, null, 1, null ) );

        while ( !opens.isEmpty() ) {
            Crucible crucible = opens.poll();
            if ( crucible.goal( industrialComplex ) ) { //we're done, the lava has arrived
                if ( true ) {
                    return reconstructPath( crucible, industrialComplex );
                }
                return crucible.distance();
            }
            //ah well, this one has been visited
            visited.add( crucible );

            crucible.getNeighbors( industrialComplex, maxStraight, minStraight ).forEach( n -> {
                if ( !visited.contains( n ) && !opens.contains( n ) ) {
                    opens.add( n );
                }
                else if ( opens.contains( n ) ) {
                    Crucible old = opens.stream().filter( n::equals ).findFirst().get();
                    if ( old.distance() > n.distance() ) {
                        opens.remove( old );
                        opens.add( n );
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

    record Crucible(Coordinates position, int heatLoss, Direction facing, int straight, Crucible previous) implements Comparable<Crucible> {

        List<Crucible> getNeighbors( Matrix industrialComplex, int maxStraight, int minStraight ) {

            List<Crucible> neighbors = new ArrayList<>();
            Direction.getBasic().forEach( d -> {
                Coordinates nextPosition = position.moveTo( d, 1 );

                if ( industrialComplex.isInTheMatrix( nextPosition ) ) { //do not consider stuff outside the matrix, duh
                    int losingMoreHeat = industrialComplex.getValue( nextPosition ) - 48; //still storing characters so I need the ascii offset
                    if ( facing == null ) { //start case: all fine, north+west are outside the matrix
                        System.out.println( "Initial neighbor: to the " + d );
                        neighbors.add( new Crucible( nextPosition, losingMoreHeat, d, STRAIGHT_AFTER_TURN, this ) );
                    }
                    else if ( d == facing ) { //straight ahead
                        if ( straight < maxStraight ) {
                            neighbors.add( new Crucible( nextPosition, heatLoss() + losingMoreHeat, d, this.straight() + 1, this ) );
                        }
                    }
                    else if ( facing.isOrthogonal( d ) && straight >= minStraight ) { //turns, we are not going back, ever!
                        neighbors.add( new Crucible( nextPosition, heatLoss() + losingMoreHeat, d, STRAIGHT_AFTER_TURN, this ) );

                    }

                }
            } );
            if ( neighbors.isEmpty() ) {
                System.err.printf( "Found no neighbors for %s %n", this );
            }
            return neighbors;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof Crucible crucible ) ) {return false;}
            return straight == crucible.straight && Objects.equals( position, crucible.position ) && facing == crucible.facing;
        }

        @Override
        public int hashCode() {
            return Objects.hash( position, facing, straight );
        }

        @Override
        public String toString() {
            return "Crucible{" + "position=" + position + ", heatLoss=" + heatLoss + ", facing=" + facing + ", straight=" + straight + '}';
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
            return heatLoss;
        }
    }

}
