package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Day6 extends Day<Integer> {


    public Day6() {
        super("", 41, 6, 4982, 0);
    }

    char OBSTACLE = '#';
    char VISITED = 'X';
    char LOOP = 'O';
    char CORNER = '+';

    @Override
    public Integer part0(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates guard = warehouse.findValues('^', true).getFirst();
        warehouse.setValue(guard, VISITED);
        Direction direction = Direction.north;
        Coordinates nextStep = guard.moveTo(direction);
        while (warehouse.isInTheMatrix(nextStep)) {
            if (warehouse.getValue(nextStep) == OBSTACLE) {
                direction = direction.turnRight();
                nextStep = guard.moveTo(direction);
            } else {
                guard = nextStep;
                warehouse.setValue(guard, VISITED);
                nextStep = guard.moveTo(direction);
            }
        }

        return warehouse.findValues(VISITED, false).size();
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix warehouse = Matrix.createFromStream(input);
        Coordinates start = warehouse.findValues('^', true).getFirst();
        Coordinates guard = start;
        Direction direction = Direction.north;
        Coordinates nextStep = guard.moveTo(direction);
        Set<Coordinates> loop = new HashSet<>();
        while (warehouse.isInTheMatrix(nextStep)) {
            if (warehouse.getValue(nextStep) == OBSTACLE) {
                direction = direction.turnRight();
                warehouse.setValue(guard, direction.getCharacter());
                nextStep = guard.moveTo(direction);
            } else {
                guard = nextStep;
                warehouse.setValue(guard, direction.getCharacter());
                if (look(warehouse, direction.turnRight(), guard)) {
                    Coordinates candidate = guard.moveTo(direction);
                    if (warehouse.isInTheMatrix(candidate)) {
                        loop.add(candidate);
                    }
                }
                nextStep = guard.moveTo(direction);
            }
        }

        loop.forEach(l -> warehouse.setValue(l, LOOP));
        System.out.println(warehouse);
        return warehouse.findValues(LOOP, false).size();
    }

    //1598 too low, 2450 too high
    boolean look(Matrix warehouse, Direction look, Coordinates guard) {
        Coordinates lookout = guard.moveTo(look);
        char previous = (char) warehouse.getValue(guard);
        while (warehouse.isInTheMatrix(lookout)) {
            char value = (char) warehouse.getValue(lookout);
            if (value == OBSTACLE && (previous == look.getCharacter() || previous == look.turnRight().getCharacter())) {
                return true;
            }
            previous = value;
            lookout = lookout.moveTo(look);
        }
        return false;
    }


}
