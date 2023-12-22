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

public class Day21 extends Day<Integer> {
    static final char ROCK = '#';

    public Day21( Integer... expected ) {super( 21, "Step Counter", expected );}

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        return getNumberOfPlots( garden, isTestMode() ? 6 : 64, false );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Matrix garden = Matrix.createFromStream( input );
        return getNumberOfPlots( garden, isTestMode() ? 100 : 26501365, true );
    }

    private int getNumberOfPlots( Matrix garden, int stepsToTake, boolean endless ) {
        PriorityQueue<StepCounter> queue = new PriorityQueue<>();
        Set<StepCounter> found = new HashSet<>();
        Set<StepCounter> seen = new HashSet<>();
        queue.add( new StepCounter( stepsToTake, garden.findValues( 'S', true ).getFirst() ) );
        while ( !queue.isEmpty() ) {
            StepCounter stepCounter = queue.poll();
            if ( stepCounter.isDone() ) {
                found.add( stepCounter );
            }
            else if ( !seen.contains( stepCounter ) ) {
                seen.add( stepCounter );
                queue.addAll( stepCounter.getPlots( garden, endless ) );
            }
        }
        //found.forEach( c -> garden.setValue( c.theElf,'O' ) );
        //System.out.println(garden);
        return found.size();
    }

    //50 -> 1594 vs 1579
    //100-> 6536 vs 6552
    //500-> 167004
    record StepCounter(int stepsToGo, Coordinates theElf) implements Comparable<StepCounter> {
        List<StepCounter> getPlots( Matrix garden, boolean endless ) {
            List<StepCounter> stepCounters = new ArrayList<>();
            Direction.getBasic().forEach( dir -> {
                Coordinates step = theElf.moveTo( dir );
                if ( garden.getRelativeValue( step ) != ROCK ) {
                    stepCounters.add( new StepCounter( stepsToGo - 1, step ) );
                }
            } );
            return stepCounters;
        }

        boolean isDone() {
            return stepsToGo == 0;
        }

        @Override
        public int compareTo( StepCounter o ) {
            if ( stepsToGo == o.stepsToGo ) {
                return 0;
            }
            return stepsToGo < o.stepsToGo ? -1 : 1;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof StepCounter that ) ) {return false;}
            return stepsToGo == that.stepsToGo && Objects.equals( theElf, that.theElf );
        }

        @Override
        public int hashCode() {
            return Objects.hash( stepsToGo, theElf );
        }
    }


}
