package de.delusions.aoc.advent2023;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Day17 extends Day<Integer> {

    public static final int STRAIGHT_AFTER_TURN = 1;


    static int maxStraight;

    static int minStraight;

    public Day17( Integer... expected ) {
        super( 17, "Clumsy Crucible", expected );
    }

    /* for some debug output if needed */
    private static int reconstructPath( Crucible crucible, Matrix industrialComplex ) {
        int totalHeatLoss = crucible.heatLoss();
        while ( crucible.previous() != null ) {
            industrialComplex.setValue( crucible.position(), crucible.facing().getSymbol().charAt( 0 ) );
            crucible = crucible.previous();
        }
        System.out.println( industrialComplex );
        return totalHeatLoss;
    }

    @Override
    public Integer part0( Stream<String> input ) {
        maxStraight = 3;
        minStraight = 1;
        return run( input );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        maxStraight = 10;
        minStraight = 4;
        return run( input );
    }

    private static Integer run( Stream<String> input ) {
        Matrix industrialComplex = Matrix.createFromStream( input );
        Crucible start = new Crucible( new Coordinates( 0, 0 ), 0, null, 1, null );
        //I extracted my algorithm into a generic version so I don't have to go flailing about next time
        Dijkstra<Crucible, Matrix> crucibleMatrixDijkstra = new Dijkstra<>( start );
        return crucibleMatrixDijkstra.findBestPath( industrialComplex ).distance();
    }

    record Crucible(Coordinates position, int heatLoss, Direction facing, int straight, Crucible previous)
        implements Pathable<Crucible, Integer, Matrix> {

        public List<Crucible> getNeighbors( Matrix industrialComplex ) {

            List<Crucible> neighbors = new ArrayList<>();
            Direction.getBasic().forEach( d -> {
                Coordinates nextPosition = position.moveTo( d, 1 );

                if ( industrialComplex.isInTheMatrix( nextPosition ) ) { //do not consider stuff outside the matrix, duh
                    int losingMoreHeat = industrialComplex.getValue( nextPosition ) - 48; //still storing characters so I need the ascii offset
                    if ( facing == null ) { //just the starter
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

        public Integer distance() {
            return heatLoss;
        }

        public boolean goal( Matrix industrialComplex ) {
            return straight >= minStraight &&
                position.equals( new Coordinates( industrialComplex.getXLength() - 1, industrialComplex.getYLength() - 1 ) );
        }

        @Override
        public int compareTo( Crucible o ) {
            if ( this.distance().equals( o.distance() ) ) {return 0;}
            return this.distance() < o.distance() ? -1 : 1;
        }
    }

}
