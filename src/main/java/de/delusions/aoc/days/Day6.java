package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day6 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day6.class);

    public Day6() {
        super("", 41, 6, 4982, 1663);
        Coordinates.USE_FACING = true;
    }

    char OBSTACLE = '#';
    char VISITED = 'X';
    char PRISTINE = '.';

    @Override
    public Integer part0(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates guard = warehouse.findValues('^', true).getFirst();
        guard.setFacing(Direction.north);
        trackTheGuard(warehouse, guard, d -> VISITED);
        return warehouse.findValues(VISITED, false).size();
    }

    private List<Coordinates> trackTheGuard(Matrix warehouse, Coordinates guard, Function<Direction, Character> visitor) {
        List<Coordinates> steps = new ArrayList<>();
        while (warehouse.isInTheMatrix(guard)) {

            if (visitor != null) {
                warehouse.setValue(guard, visitor.apply(guard.getFacing()));
            }

            if (warehouse.getValue(guard.moveToNext(), -1) == OBSTACLE) {
                guard.setFacing(guard.getFacing().turnRight());//guard turns before the obstacle
                //only check for cycles before an obstacle
                //checking the set is somewhat more expensive than moving through the matrix
                if (steps.contains(guard)) {
                    throw new IllegalStateException("Cycle detected");
                }
            } else {
                //move only when there is no obstacle!
                steps.add(guard);
                guard = guard.moveToNext();
            }
        }
        return steps;
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates guard = warehouse.findValues('^', true).getFirst();
        guard.setFacing(Direction.north);
        List<Coordinates> steps = trackTheGuard(warehouse, guard, null); //get all original steps
        Set<Coordinates> loops = new HashSet<>();
        for (Coordinates step : steps) {
            warehouse.setValue(step, VISITED);
            Coordinates candidate = step.moveToNext();
            //since I am not testing cycles completely from the beginning, I need to be sure the guard
            //has not been here before. This was the major issue I couldn't solve. Thanks @tshirtman@mas.to
            if (warehouse.isInTheMatrix(candidate) && warehouse.getValue(candidate) != VISITED) {
                warehouse.setValue(candidate, OBSTACLE); //temporarily add obstacle
                try {
                    trackTheGuard(warehouse, step, null); //check circles
                } catch (IllegalStateException e) {
                    loops.add(candidate); //add candidate to set
                }
                warehouse.setValue(candidate, PRISTINE);
            }
        }
        return loops.size();
    }

}
