package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class Day8Test {

    String problem = """
            Part 1: Can you explain the following problem: an antinode occurs at any point that is perfectly in line with 
            two antennas of the same frequency - but only when one of the antennas is twice as far away as the other. 
            This means that for any pair of antennas with the same frequency, there are two antinodes, 
            one on either side of them.
            
            Part 2: Additionally an antinode occurs at any grid position exactly in line with at least two antennas of 
            the same frequency, regardless of distance. This means that some of the new antinodes will occur at the 
            position of each antenna (unless that antenna is the only one of its frequency).
            """;

    String testCase = """
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
        //I don't understand the problem description I think
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
        System.out.println(example.toString());

//        assertThat(antennas.get(0).lookingTowards(antennas.get(1))).isNotNull();
//        assertThat(antennas.get(0).lookingTowards(antennas.get(2))).isNotNull();
//        assertThat(antennas.get(1).lookingTowards(antennas.get(2))).isNotNull();
    }
}