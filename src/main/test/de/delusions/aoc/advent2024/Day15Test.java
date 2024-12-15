package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.testng.annotations.Test;


import static org.assertj.core.api.Assertions.assertThat;
public class Day15Test {

    static final String INPUT = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
            """;

    static final String RESULT = """
            ########
            #...@OO#
            ##..O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
            """;

    @Test
    public void testMove(){
        Matrix room = Matrix.createFromString(INPUT,"\n");
        Coordinates robot = room.findValues(Day15.ROBOT,true).getFirst();
        robot.setFacing(Direction.north);
        robot = Day15.moveBoxes(robot,room);
        robot.setFacing(Direction.east);
        robot = Day15.moveBoxes(robot,room);
        Day15.moveBoxes(robot,room);
        assertThat(room.toString()).isEqualTo(RESULT);
    }
}