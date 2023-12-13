package de.delusions.aoc;

import de.delusions.util.Matrix;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class Day13Test {

    String testCase1 = """
                       ##..##...
                       ###.##...
                       #.###..#.
                       #.###..#.
                       ###.##...
                       ##..##...
                       #....#...
                       #####..#.
                       ##..#####
                       #.##..#.#
                       .###.#.#.
                       ###.###.#
                       ...###...
                       """;

    String testCase2 = """
                       .#.#..#..
                       ..#.##.#.
                       ..#.##.#.
                       ##.#..#..
                       .#.#.#.##
                       ...#...##
                       ...#...##
                       """;

    @Test
    public void testCase1() {
        Matrix matrix = Matrix.createFromString( testCase1, "\n" );
        Assertions.assertThat( Day13.findMirrorPosition( matrix ) ).isEqualTo( 3 );
        Assertions.assertThat( Day13.findMirrorPosition( matrix.transpose() ) ).isEqualTo( -1 );
    }

    @Test
    public void testCase2() {
        Matrix matrix = Matrix.createFromString( testCase2, "\n" );
        Assertions.assertThat( Day13.findMirrorPosition( matrix ) ).isEqualTo( 6 );
        Assertions.assertThat( Day13.findMirrorPosition( matrix.transpose() ) ).isEqualTo( -1 );
    }
}
