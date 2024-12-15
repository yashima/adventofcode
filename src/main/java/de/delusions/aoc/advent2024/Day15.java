package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day15 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day15.class);

    public static final int BOX = 'O';
    public static final int EMPTY = '.';
    public static final int ROBOT = '@';
    public static final int WALL = '#';

    public Day15() {
        super("", 10092, 0, 1465523, 0);
    }

    private static void setColorMap(Matrix aRoom) {
        aRoom.setColor(ROBOT, Color.RED);
        aRoom.setColor(BOX, Color.GREEN);
        aRoom.setColor(EMPTY, Color.white);
        aRoom.setColor(WALL, Color.BLACK);
    }

    @Override
    public Integer part0(Stream<String> input) {
        //java streams cannot be re-used so I need to store than recreate it
        List<String> lines = input.toList();
        int partition = isTestMode() ? 10 : 50; //manually looked up where the input switches
        Matrix aRoom = Matrix.createFromStream(lines.stream().limit(partition));
        List<Direction> directions = lines.stream().skip(partition).collect(Collectors.joining()).strip().chars().mapToObj(Direction::getBySymbol).filter(Objects::nonNull).toList();
        Coordinates robot = aRoom.findValues(ROBOT, true).getFirst();
        long boxes = aRoom.findValues(BOX, false).stream().count();
        setColorMap(aRoom);
        AtomicInteger frame = new AtomicInteger(0);
        for (Direction d : directions) {
            robot.setFacing(d);
            if (!isTestMode()) {
                aRoom.saveImage(15, frame.getAndIncrement());
            }
            robot = moveBoxes(robot, aRoom);
            if (boxes != aRoom.findValues(BOX, false).stream().count()) {
                throw new IllegalStateException("The robot ate another box");
            }
        }
        return aRoom.findValues(BOX, false).stream().mapToInt(c -> gps(c, aRoom.getYLength())).sum();
    }

    /**
     * Moves a box or the given entity in the matrix (e.g., robot or box) to the next position based on its direction,
     * adhering to the rules of the matrix and handling collisions with walls or other boxes.
     *
     * @param thing The current coordinates of an entity (e.g., a robot or a box).
     * @param aRoom The matrix representing the room, which contains walls, empty spaces, boxes, and entities.
     * @return The updated coordinates of the entity after attempting to move.
     * @throws IllegalStateException If the entity tries to move outside the bounds of the matrix
     *                               or encounters an unknown cell value.
     */
    static Coordinates moveBoxes(Coordinates thing, Matrix aRoom) {
        Coordinates nextSpace = thing.moveToNext();
        if (!aRoom.isInTheMatrix(nextSpace)) {
            throw new IllegalStateException("You have broken the 4th wall");//or any wall
        }
        int thingValue = aRoom.getValue(thing);
        int nextValue = aRoom.getValue(nextSpace);
        if (nextValue == WALL) {
            return thing;
        } else if (nextValue == EMPTY) {
            aRoom.setValue(thing, EMPTY);
            aRoom.setValue(nextSpace, thingValue);
            return nextSpace; //it has moved
        } else if (nextValue == BOX) {
            moveBoxes(nextSpace, aRoom);
            if (aRoom.getValue(nextSpace) == EMPTY) {
                aRoom.setValue(thing, EMPTY);
                aRoom.setValue(nextSpace, thingValue);
                return nextSpace;
            }
            return thing;
        } else {
            throw new IllegalStateException("Found unknown value " + (char) nextValue);
        }
    }


    static int gps(Coordinates current, int length) {
        return current.x * 100 + current.y;//guessing which way my matrix is turned
    }

    @Override
    public Integer part1(Stream<String> input) {
        return 0;
    }
}
