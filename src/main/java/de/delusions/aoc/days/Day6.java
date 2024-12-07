package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
        while (warehouse.isInTheMatrix(guard)) {

            if (visitor != null) {
                warehouse.setValue(guard, visitor.apply(guard.getFacing()));
            }

            if (warehouse.getValue(guard.moveToNext(),-1) == OBSTACLE) {
                guard.setFacing(guard.getFacing().turnRight());//guard turns before the obstacle
                if(steps.contains(guard)){//only check for cycles before an obstacle
                    throw new IllegalStateException("Cycle detected");
                }
            }
            steps.add(guard);//add with the turn
            guard = guard.moveToNext();
        }
        return steps;
    }

    //1598 too low, 2450 too high, 1743 not right
    @Override
    public Integer part1(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates guard = warehouse.findValues('^', true).getFirst();
        guard.setFacing(Direction.north);
        List<Coordinates> steps = trackTheGuard(warehouse, guard,null);
        Set<Coordinates> loops = new HashSet<>();
        for (Coordinates step : steps) {
            Coordinates candidate = step.moveToNext();
            if (warehouse.isInTheMatrix(candidate) && warehouse.getValue(candidate) != OBSTACLE && !loops.contains(candidate)) {
                warehouse.setValue(candidate, OBSTACLE);
                try {
                    trackTheGuard(warehouse, step, null);
                } catch (IllegalStateException e) {
                    loops.add(candidate);
                }
                warehouse.setValue(candidate, '.');
            }
        }
        return loops.size();
    }

}
