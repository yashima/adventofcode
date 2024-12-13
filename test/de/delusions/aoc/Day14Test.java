package de.delusions.aoc;

import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import main.java.de.delusions.aoc.advent2023.Day14;
import org.testng.annotations.Test;

import static de.delusions.util.Direction.north;
import static org.assertj.core.api.Assertions.assertThat;

public class Day14Test {

    String testCase = """
                      O....#....
                      O.OO#....#
                      .....##...
                      OO.#O....O
                      .O.....O#.
                      O.#..O.#.#
                      ..O..#O..O
                      .......O..
                      #....###..
                      #OO..#....
                      """;

    String rolled = """
                     OOOO.#.O..
                     OO..#....#
                     OO..O##..O
                     O..#.OO...
                     ........#.
                     ..#....#.#
                     ..O..#.O.O
                     ..O.......
                     #....###..
                     #....#....
                    """;

    Day14 classUnderTest = new Day14();

    String oneCycle = """
                      .....#....
                      ....#...O#
                      ...OO##...
                      .OO#......
                      .....OOO#.
                      .O#...O#.#
                      ....O#....
                      ......OOOO
                      #...O###..
                      #..OO#....
                      """;

    String twoCycle = """
                      .....#....
                      ....#...O#
                      .....##...
                      ..O#......
                      .....OOO#.
                      .O#...O#.#
                      ....O#...O
                      .......OOO
                      #..OO###..
                      #.OOO#...O
                      """;

    @Test
    public void testTilt() {
        Matrix dish = Matrix.createFromString( testCase, "\n" );
        Matrix rolledNorth = Matrix.createFromString( rolled, "\n" );
        classUnderTest.processTiltDish( dish, Direction.north );
        for ( int i = 0; i < dish.getXLength(); i++ ) {
            assertThat( dish.rowToString( i ) ).withFailMessage( "single, row: %d: %s <-> %s",
                                                                 i,
                                                                 dish.rowToString( i ),
                                                                 rolledNorth.rowToString( i ) ).isEqualTo( rolledNorth.rowToString( i ) );
        }

    }

    @Test
    public void testStress() {
        Matrix dish = Matrix.createFromString( testCase, "\n" );
        classUnderTest.processTiltDish( dish, north );
        assertThat( classUnderTest.stress( dish.getRow( 0 ), 10 ) ).isEqualTo( 50L );
        assertThat( classUnderTest.stress( dish.getRow( 1 ), 9 ) ).isEqualTo( 18L );
        assertThat( classUnderTest.stress( dish.getRow( 2 ), 8 ) ).isEqualTo( 32L );
        assertThat( classUnderTest.stress( dish.getRow( 3 ), 7 ) ).isEqualTo( 21L );
        assertThat( classUnderTest.stress( dish.getRow( 4 ), 6 ) ).isEqualTo( 0L );
        assertThat( classUnderTest.stress( dish.getRow( 5 ), 5 ) ).isEqualTo( 0L );
        assertThat( classUnderTest.stress( dish.getRow( 6 ), 4 ) ).isEqualTo( 12 );
        assertThat( classUnderTest.stress( dish.getRow( 7 ), 3 ) ).isEqualTo( 3L );
        assertThat( classUnderTest.stress( dish.getRow( 8 ), 2 ) ).isEqualTo( 0L );
        assertThat( classUnderTest.stress( dish.getRow( 9 ), 1 ) ).isEqualTo( 0L );

        assertThat( classUnderTest.calculateCurrentStress( dish ) ).isEqualTo( 136 );
    }

    @Test
    public void testCycle() {
        Matrix dish = Matrix.createFromString( testCase, "\n" );
        Matrix cycle = Matrix.createFromString( oneCycle, "\n" );

        classUnderTest.processTiltCycle( dish );

        for ( int i = 0; i < dish.getXLength(); i++ ) {
            assertThat( dish.rowToString( i ) ).withFailMessage( "cycle, row: %d: %s <-> %s", i, dish.rowToString( i ), cycle.rowToString( i ) )
                                               .isEqualTo( cycle.rowToString( i ) );
        }
        cycle = Matrix.createFromString( twoCycle, "\n" );
        classUnderTest.processTiltCycle( dish );

        for ( int i = 0; i < dish.getXLength(); i++ ) {
            assertThat( dish.rowToString( i ) ).withFailMessage( "cycle, row: %d: %s <-> %s", i, dish.rowToString( i ), cycle.rowToString( i ) )
                                               .isEqualTo( cycle.rowToString( i ) );
        }
    }

}
