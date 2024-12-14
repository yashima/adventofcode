package de.delusions.aoc.advent2024;

import org.testng.annotations.Test;


import static org.assertj.core.api.Assertions.assertThat;
public class Day14Test {

    @Test
    public void testWrap(){
        assertThat(Day14.wrap(1,2,1,5)).isEqualTo(3);
        assertThat(Day14.wrap(1,2,2,5)).isEqualTo(0);
        assertThat(Day14.wrap(1,2,3,5)).isEqualTo(2);

        assertThat(Day14.wrap(1,-2,1,5)).isEqualTo(4);
        assertThat(Day14.wrap(1,-2,2,5)).isEqualTo(2);
    }

}