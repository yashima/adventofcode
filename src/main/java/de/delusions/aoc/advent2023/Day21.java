package main.java.de.delusions.aoc.advent2023;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day21 extends Day<Integer> {
    static final char ROCK = '#';

    static int ODD_PLOTS = 7458;

    static int EVEN_PLOTS = 7451;

    public Day21( Integer... expected ) {super( 21, "Step Counter", expected );}

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        return getNumberOfPlots( garden, isTestMode() ? 6 : 130 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        if ( isTestMode() ) {
            return getNumberOfPlots( garden, 131 );
        }
        int totalSteps = 26501365;
        int simulation = 26501365 % garden.getXLength() + garden.getXLength();
        BigInteger oddAndEven = BigInteger.valueOf( ODD_PLOTS + EVEN_PLOTS );
        BigInteger missingFactor = BigInteger.valueOf( ( totalSteps - simulation ) / garden.getXLength() );
        BigInteger thePointyBits = BigInteger.valueOf( getNumberOfPlots( garden, simulation ) - ODD_PLOTS );
        BigInteger result = missingFactor.multiply( missingFactor ).divide( BigInteger.TWO ).multiply( oddAndEven ).add( thePointyBits );
        System.out.println( result );//too low: 305074558240574
        return -1;
    }

    private int getNumberOfPlots( Matrix garden, int stepsToTake ) {
        PriorityQueue<StepCounter> queue = new PriorityQueue<>();
        Set<StepCounter> found = new HashSet<>();
        Set<StepCounter> seen = new HashSet<>();
        queue.add( new StepCounter( stepsToTake, garden.findValues( 'S', true ).getFirst(), null ) );
        while ( !queue.isEmpty() ) {
            StepCounter stepCounter = queue.poll();
            if ( stepCounter.isDone() ) {
                found.add( stepCounter );
            }
            else if ( !seen.contains( stepCounter ) ) {
                seen.add( stepCounter );
                queue.addAll( stepCounter.getPlots( garden, true ) );
            }
        }
        //setting found values for visual + debugging purposes
        found.forEach( c -> garden.setValueSafely( c.theElf, 'O', List.of( '#' ) ) );
        return found.size();
    }

    @Deprecated
    void findSomePaths( Matrix garden ) {
        //note: because the way I wrote this and that, hashCode & equals cannot contain the distance aka steps parameter! change to use
        IntStream.range( 0, garden.getXLength() ).forEach( x -> {
            Coordinates target = new Coordinates( x, 0 );
            StepCounter start = new StepCounter( 0, garden.findValues( 'S', true ).getFirst(), target );
            Dijkstra<StepCounter, Matrix> dijkstra = new Dijkstra<>( start );
            StepCounter path = dijkstra.findBestPath( garden );
            System.out.println( target + " " + path.steps );
        } );
    }

    record StepCounter(int steps, Coordinates theElf, Coordinates goTo) implements Pathable<StepCounter, Integer, Matrix>, Comparable<StepCounter> {
        boolean isDone() {
            return steps == 0;
        }

        @Override
        public int compareTo( StepCounter o ) {
            if ( steps == o.steps ) {
                return 0;
            }
            return steps < o.steps ? -1 : 1;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof StepCounter that ) ) {return false;}
            return steps == that.steps && Objects.equals( theElf, that.theElf );
        }

        @Override
        public int hashCode() {
            return Objects.hash( steps, theElf );
        }

        @Override
        public List<StepCounter> getNeighbors( Matrix theMap ) {
            return getPlots( theMap, false );
        }

        List<StepCounter> getPlots( Matrix garden, boolean countDown ) {
            List<StepCounter> stepCounters = new ArrayList<>();
            Direction.getBasic().forEach( dir -> {
                Coordinates step = theElf.moveTo( dir );
                if ( garden.getRelativeValue( step ) != ROCK ) {
                    stepCounters.add( new StepCounter( steps + ( countDown ? -1 : +1 ), step, goTo ) );
                }
            } );
            return stepCounters;
        }

        @Override
        public Integer distance() {
            return steps;
        }

        @Override
        public boolean goal( Matrix theMap ) {
            return this.theElf.equals( goTo );
        }

        @Override
        public StepCounter previous() {
            return null; //no reconstruction
        }
    }


}
