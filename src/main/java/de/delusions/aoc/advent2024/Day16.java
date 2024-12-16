package de.delusions.aoc.advent2024;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class Day16 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day16.class);

    static final char WALL = '#';

    public Day16() {
        super("Reindeer Maze", 11048, 0, 160624, 0);
    }


    @Override
    public Integer part0(Stream<String> input) {
        Matrix labyrinth = Matrix.createFromStream(input);
        return new Dijkstra<>(new RunRudolphRun(labyrinth.findValue('S'), 0,Direction.east)).findBestPath(labyrinth).distance();
    }

    @Override
    public Integer part1(Stream<String> input) {
        return 0;
    }

    String printPath(Coordinates end, Matrix theMap) {
        Coordinates current = end;
        while(current != null) {
            theMap.setValue(current, current.getFacing().getCharacter());
            current = current.getPrevious();
        }
        return theMap.toString();
    }

    class RunRudolphRun implements Pathable<RunRudolphRun, Integer, Matrix> {

        Coordinates current;
        int steps;
        Direction facing;

        RunRudolphRun(Coordinates c, int steps, Direction facing) {
            this.current = c;
            this.steps = steps;
            this.facing = facing;
        }

        @Override
        public List<RunRudolphRun> getNeighbors(Matrix theMap) {
            List<RunRudolphRun> result = Arrays.stream(Direction.cardinals())
                    .filter(d -> d != facing.opposite())
                    .map(d -> current.moveTo(d).setFacing(d))
                    .filter(c -> theMap.getValue(c) != WALL)
                    .map(c -> new RunRudolphRun(c, this.steps + 1 + (this.facing == c.getFacing() ? 0 : 1000),c.getFacing()))
                    .toList();
            return result;
        }

        @Override
        public Integer distance() {
            return steps;
        }

        @Override
        public boolean goal(Matrix theMap) {
            return theMap.getValue(current) == 'E';
        }

        @Override
        public RunRudolphRun previous() {
            return null;
        }

        @Override
        public int compareTo(RunRudolphRun o) {
            return Integer.compare(this.steps, o.steps);
        }

        @Override
        public String toString() {
            return current.toString() + " " + steps;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RunRudolphRun that = (RunRudolphRun) o;
            return Objects.equals(current, that.current) && this.facing == that.facing;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(current, facing);
        }

    }
}
