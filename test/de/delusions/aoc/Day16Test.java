package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Matrix;
import main.java.de.delusions.aoc.advent2023.Day16;
import org.testng.annotations.Test;

import static main.java.de.delusions.aoc.advent2023.Day16.Mirror.*;
import static de.delusions.util.Direction.*;
import static org.assertj.core.api.Assertions.assertThat;

public class Day16Test {

    String testcase = """
                      .|...\\....
                      |.-.\\.....
                      .....|-...
                      ........|.
                      ..........
                      .........\\
                      ..../.\\\\..
                      .-.-/..|..
                      .|....-|.\\
                      ..//.|....
                      """;

    @Test
    public void test() {
        Matrix mirrors = Matrix.createFromString( testcase, "\n" );
        Coordinates coordinates = new Coordinates( 7, 4 );
        assertThat( Day16.Mirror.getBySymbol( (char) mirrors.getValue( coordinates ) ) ).isEqualTo( SLASH );

        assertThat( SLASH.getNextLights( new Coordinates( 7, 4, north ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );
        assertThat( SLASH.getNextLights( new Coordinates( 7, 4, south ) ).getFirst() ).isEqualTo( new Coordinates( 7, 3, west ) );
        assertThat( SLASH.getNextLights( new Coordinates( 7, 4, west ) ).getFirst() ).isEqualTo( new Coordinates( 8, 4, south ) );
        assertThat( SLASH.getNextLights( new Coordinates( 7, 4, east ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );

        assertThat( BACKSLASH.getNextLights( new Coordinates( 7, 4, north ) ).getFirst() ).isEqualTo( new Coordinates( 7, 3, west ) );
        assertThat( BACKSLASH.getNextLights( new Coordinates( 7, 4, south ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );
        assertThat( BACKSLASH.getNextLights( new Coordinates( 7, 4, west ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );
        assertThat( BACKSLASH.getNextLights( new Coordinates( 7, 4, east ) ).getFirst() ).isEqualTo( new Coordinates( 8, 4, south ) );

        assertThat( EMPTY.getNextLights( new Coordinates( 7, 4, north ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );
        assertThat( EMPTY.getNextLights( new Coordinates( 7, 4, south ) ).getFirst() ).isEqualTo( new Coordinates( 8, 4, south ) );
        assertThat( EMPTY.getNextLights( new Coordinates( 7, 4, west ) ).getFirst() ).isEqualTo( new Coordinates( 7, 3, west ) );
        assertThat( EMPTY.getNextLights( new Coordinates( 7, 4, east ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );

        assertThat( DIVIDER_VERTICAL.getNextLights( new Coordinates( 7, 4, north ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );
        assertThat( DIVIDER_VERTICAL.getNextLights( new Coordinates( 7, 4, south ) ).getFirst() ).isEqualTo( new Coordinates( 8, 4, south ) );
        assertThat( DIVIDER_VERTICAL.getNextLights( new Coordinates( 7, 4, west ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );
        assertThat( DIVIDER_VERTICAL.getNextLights( new Coordinates( 7, 4, east ) ).getFirst() ).isEqualTo( new Coordinates( 6, 4, north ) );

        assertThat( DIVIDER_HORIZONTAL.getNextLights( new Coordinates( 7, 4, north ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );
        assertThat( DIVIDER_HORIZONTAL.getNextLights( new Coordinates( 7, 4, south ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );
        assertThat( DIVIDER_HORIZONTAL.getNextLights( new Coordinates( 7, 4, west ) ).getFirst() ).isEqualTo( new Coordinates( 7, 3, west ) );
        assertThat( DIVIDER_HORIZONTAL.getNextLights( new Coordinates( 7, 4, east ) ).getFirst() ).isEqualTo( new Coordinates( 7, 5, east ) );

    }
}