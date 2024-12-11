package de.delusions.aoc.days;

import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
public class Day11Test {

    @Test
    public void testSplit(){
        assertThat(Day11.split(1000L)).isEqualTo(List.of(10L,0L));
        assertThat(Day11.split(1024L)).isEqualTo(List.of(10L,24L));
        assertThat(Day11.split(12L)).isEqualTo(List.of(1L,2L));
        assertThat(Day11.split(1266L)).isEqualTo(List.of(12L,66L));
        assertThat(Day11.split(155266L)).isEqualTo(List.of(155L,266L));
    }

    @Test
    public void testInsertList(){
        List<Long> blink = new Day11().blink(List.of(0L), 30);
        Set<Long> set = new HashSet<>(blink);
        assertThat(blink.size()).isEqualTo(156451);
        assertThat(set.size()).isEqualTo(54);

    }


}