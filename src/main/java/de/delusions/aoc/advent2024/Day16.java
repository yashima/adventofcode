package de.delusions.aoc.advent2024;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;


public class Day16 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day16.class);

    static final char WALL = '#';

    public Day16() {
        super("Reindeer Maze", 11048, 45, 160624, 692);
    }

    @Override
    public Integer part0(Stream<String> input) {
        Matrix labyrinth = Matrix.createFromStream(input);
        return new Dijkstra<>(new RunRudolphRun(labyrinth.findValue('S'), 0, Direction.east, null)).findBestPath(labyrinth).distance();
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix labyrinth = Matrix.createFromStream(input);
        List<RunRudolphRun> paths = new Dijkstra<>(new RunRudolphRun(labyrinth.findValue('S'), 0, Direction.east, null)).findAllBestPaths(labyrinth);
        paths.stream().map(this::collectVisitedPlaces).flatMap(Collection::stream).forEach(c -> labyrinth.setValue(c, '0'));
        return labyrinth.findValues('0', false).size();
    }

    Set<Coordinates> collectVisitedPlaces(RunRudolphRun path) {
        HashSet<Coordinates> coordinates = new HashSet<>();
        RunRudolphRun current = path;
        while (current != null) {
            coordinates.add(current.current);
            current = current.previous;
        }
        return coordinates;
    }

    class RunRudolphRun implements Pathable<RunRudolphRun, Integer, Matrix> {

        Coordinates current;
        int steps;
        Direction facing;
        RunRudolphRun previous;

        RunRudolphRun(Coordinates c, int steps, Direction facing, RunRudolphRun previous) {
            this.current = c;
            this.steps = steps;
            this.facing = facing;
            this.previous = previous;
        }

        @Override
        public List<RunRudolphRun> getNeighbors(Matrix theMap) {
            List<RunRudolphRun> result = Arrays.stream(Direction.cardinals())
                    .filter(d -> d != facing.opposite())
                    .map(d -> current.moveTo(d).setFacing(d))
                    .filter(c -> theMap.getValue(c) != WALL)
                    .map(c -> new RunRudolphRun(c, this.steps + 1 + (this.facing == c.getFacing() ? 0 : 1000), c.getFacing(), this))
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
            return previous;
        }

        @Override
        public int compareTo(RunRudolphRun o) {
            return Integer.compare(this.distance(), o.distance());
        }

        @Override
        public String toString() {
            return current.toString() + " " + steps;
        }

        @Override
        public int hashCode() {
            return Objects.hash(current, facing);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RunRudolphRun that = (RunRudolphRun) o;
            return Objects.equals(current, that.current) && this.facing == that.facing;
        }

    }


}
