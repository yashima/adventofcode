package de.delusions.aoc;

import de.delusions.util.Matrix;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    //case 1 from the examples
    String testCase3 = """
                       #.##..##.
                       ..#.##.#.
                       ##......#
                       ##......#
                       ..#.##.#.
                       ..##..##.
                       #.#.##.#.
                       """;

    //case 2 from the examples
    String testCase4 = """
                       #...##..#
                       #....#..#
                       ..##..###
                       #####.##.
                       #####.##.
                       ..##..###
                       #....#..#
                       """;


    @Test
    public void testCase1Part1() {
        Matrix matrix = Matrix.createFromString( testCase1, "\n" );
        assertThat( Day13.getCandidates( matrix, false ) ).containsExactly( 3 );
        assertThat( Day13.isFullMirror( matrix, 3, false ) ).isTrue();
        assertThat( Day13.getCandidates( matrix.transpose(), false ) ).isEmpty();
        assertThat( Day13.findMirrorPosition( matrix, false ) ).isEqualTo( 3 );
        assertThat( Day13.findMirrorPosition( matrix.transpose(), false ) ).isEqualTo( -1 );
    }

    @Test
    public void testCase2Part1() {
        Matrix matrix = Matrix.createFromString( testCase2, "\n" );
        assertThat( Day13.getCandidates( matrix, false ) ).containsExactly( 2, 6 );
        assertThat( Day13.isFullMirror( matrix, 2, false ) ).isFalse();
        assertThat( Day13.findMirrorPosition( matrix, false ) ).isEqualTo( 6 );
        assertThat( Day13.findMirrorPosition( matrix.transpose(), false ) ).isEqualTo( -1 );
    }

    @Test
    public void testCase3Part2() {
        Matrix matrix = Matrix.createFromString( testCase3, "\n" );
        assertThat( Day13.getCandidates( matrix, false ) ).containsExactly( 3 );
        assertThat( Day13.isFullMirror( matrix, 3, true ) ).isTrue();
    }

    @Test
    public void testCase4Part2() {
        Matrix matrix = Matrix.createFromString( testCase4, "\n" );
        System.out.println( matrix.transpose() );
        assertThat( Day13.diff( matrix, 3, 4 ) ).isEqualTo( 0 );
        assertThat( Day13.getCandidates( matrix, false ) ).containsExactly( 4 );
        assertThat( Day13.findMirrorPosition( matrix, false ) ).isEqualTo( 4 );
        assertThat( Day13.getCandidates( matrix.transpose(), false ) ).containsExactly( 3, 7 );

        assertThat( matrix.rowToString( 0 ) ).isEqualTo( "#...##..#" );
        assertThat( matrix.rowToString( 1 ) ).isEqualTo( "#....#..#" );
        assertThat( Day13.diff( matrix, 0, 1 ) ).isEqualTo( 1 );
        assertThat( Day13.getCandidates( matrix, true ) ).containsExactly( 1, 4 );
        assertThat( Day13.findMirrorPosition( matrix, true ) ).isEqualTo( 1 );
    }
}

