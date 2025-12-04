package de.delusions.aoc.advent2025;

import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class Day04Test {

    static String input = """
            ..@@.@@@@.
            @@@.@.@.@@
            @@@@@.@.@@
            @.@@@@..@.
            @@.@@@@.@@
            .@@@@@@@.@
            .@.@.@.@@@
            @.@@@.@@@@
            .@@@@@@@@.
            @.@.@@@.@.
            """;


    @Test
    void testExampleInput(){
        Matrix wall = Matrix.createFromStream(input.lines());
        log.debug(wall.toString());
        Assertions.assertThat(wall).isNotNull();
        Assertions.assertThat((long) Day04.findRemovablePaper(wall).size()).isEqualTo(13);
    }

}