package de.delusions.aoc;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class Day12Test {

    @Test
    public void testDigitization() {
        Assertions.assertThat( Day12.digits( 1234, 5 ) ).containsExactly( 0, 1, 2, 3, 4 );
        Assertions.assertThat( Day12.digits( 1234, 4 ) ).containsExactly( 1, 2, 3, 4 );
        Assertions.assertThat( Day12.digits( 1230, 4 ) ).containsExactly( 1, 2, 3, 0 );
    }

}
