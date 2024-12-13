package de.delusions.util;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class MatrixTest {

    final String testCase = """
            1..2
            .56.
            .87.
            4..3
            """;

    final String testCase2 = """
            #..O#
            ..#.O
            .OO.#
            """;

    @Test
    public void testTranspose() {
        Matrix matrix = Matrix.createFromString(testCase, "\n").transpose();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("1..4");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo(".58.");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".67.");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("2..3");

    }

    @Test
    public void testTransposeRight() {
        Matrix matrix = Matrix.createFromString(testCase, "\n").transposeRight();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("4..1");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo(".85.");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".76.");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("3..2");

    }

    @Test
    public void testTranspose2() {
        Matrix matrix = Matrix.createFromString(testCase2, "\n").transposeLeft();
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("#O#");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo("O..");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".#O");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("..O");
        Assertions.assertThat(matrix.rowToString(4)).isEqualTo("#..");
    }

    @Test
    public void testTransposeLeft() {
        Matrix matrix = Matrix.createFromString(testCase, "\n");
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("1..2");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo(".56.");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".87.");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("4..3");

        matrix = matrix.transposeLeft();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("2..3");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo(".67.");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".58.");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("1..4");

        matrix = matrix.transposeLeft();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("3..4");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("2..1");

        matrix = matrix.transposeLeft();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("4..1");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("3..2");

        matrix = matrix.transposeLeft();
        System.out.println(matrix);
        Assertions.assertThat(matrix.rowToString(0)).isEqualTo("1..2");
        Assertions.assertThat(matrix.rowToString(1)).isEqualTo(".56.");
        Assertions.assertThat(matrix.rowToString(2)).isEqualTo(".87.");
        Assertions.assertThat(matrix.rowToString(3)).isEqualTo("4..3");


    }
}
