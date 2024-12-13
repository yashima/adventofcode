package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class Day8Test {



    final String testCase = """
            T...........
            ...T....0...
            .T...0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
            """;

    @Test
    public void testAlignment(){
        //I don't understand the problem description, I think
        Matrix example = Matrix.createFromString(testCase, "\n");
        assertThat(example.getValue(new Coordinates(0,0))).isEqualTo('T');
        List<Coordinates> antennas = example.findValues('T',false);
        assertThat(antennas).hasSize(3);
        Coordinates first = antennas.getFirst();
        first.setFacing(Direction.southeast);
        while(example.isInTheMatrix(first)){
            example.setValue(first,first.getFacing().getCharacter());
            first = first.moveToNext();
        }
        first = antennas.getFirst();
        first.setFacing(Direction.south);
        while(example.isInTheMatrix(first)){
            example.setValue(first,first.getFacing().getCharacter());
            first = first.moveToNext();
        }

        first = antennas.getFirst();
        first.setFacing(Direction.east);
        while(example.isInTheMatrix(first)){
            example.setValue(first,first.getFacing().getCharacter());
            first = first.moveToNext();
        }
        System.out.println(example);

    }
}