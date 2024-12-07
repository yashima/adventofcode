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
        super("", 41, 6, 4982, 0);
        Coordinates.USE_FACING = true;
    }

    char OBSTACLE = '#';
    char VISITED = 'X';

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
        while (warehouse.isInTheMatrix(guard)) { //while he is in the room

            if (visitor != null) { //only store visitor steps if you know how
                warehouse.setValue(guard, visitor.apply(guard.getFacing()));
            }

            if (warehouse.getValue(guard.moveToNext(), -1) == OBSTACLE) { //test next move for obstacles
                guard.setFacing(guard.getFacing().turnRight());//guard turns before the obstacle
                if (steps.contains(guard)) {//only check for cycles before an obstacle, it is much faster and gets same results as all steps
                    throw new IllegalStateException("Cycle detected");
                }
            } else {
                steps.add(guard);//add with the turn
                guard = guard.moveToNext();
            }
        }
        return steps;
    }

    //1598 too low, 2450 too high, 1743, 1796 not right not right
    @Override
    public Integer part1(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates guard = warehouse.findValues('^', true).getFirst();
        guard.setFacing(Direction.north);
        List<Coordinates> steps = trackTheGuard(warehouse, guard, null); //get all original steps
        Set<Coordinates> loops = new HashSet<>();
        for (Coordinates step : steps) {
            Coordinates candidate = step.moveToNext(); //try to block next step of the guard
            if (warehouse.isInTheMatrix(candidate) ) { //double test no issue, and since we turn before obstacle that doesn't happen
                warehouse.setValue(candidate, OBSTACLE); //temporarily add obstacle
                try {
                    trackTheGuard(warehouse, step, null); //check circles
                } catch (IllegalStateException e) {
                    loops.add(candidate); //add candidate to set
                }
                warehouse.setValue(candidate, '.'); //remove tested obstacle
            }
        }
        return loops.size();
    }

}
