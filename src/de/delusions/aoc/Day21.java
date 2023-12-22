package de.delusions.aoc;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day21 extends Day<Integer> {
    static final char ROCK = '#';

    public Day21( Integer... expected ) {super( 21, "Step Counter", expected );}

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        return getNumberOfPlots( garden, isTestMode() ? 6 : 64 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        IntStream.range( 0, garden.getXLength() ).forEach( x -> {
            Coordinates target = new Coordinates( x, 0 );
            System.out.println( target + " " + findShortestPath( target, garden ) );
        } );
        return getNumberOfPlots( garden, isTestMode() ? 100 : 1 );
    }

    private int findShortestPath( Coordinates c, Matrix garden ) {
        StepCounter start = new StepCounter( 0, garden.findValues( 'S', true ).getFirst(), c );
        Dijkstra<StepCounter, Matrix> dijkstra = new Dijkstra<>( start );
        StepCounter path = dijkstra.findBestPath( garden );
        return path.steps;
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
        //found.forEach( c -> garden.setValue( c.theElf,'O' ) );
        //System.out.println(garden);
        return found.size();
    }

    //50 -> 1594 vs 1579
    //100-> 6536 vs 6552
    //500-> 167004
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
            return Objects.equals( theElf, that.theElf );
        }

        @Override
        public int hashCode() {
            return Objects.hash( theElf );
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
