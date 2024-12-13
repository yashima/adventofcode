package de.delusions.aoc.advent2024;

import org.testng.annotations.Test;


import static org.assertj.core.api.Assertions.assertThat;
public class Day9Test {

    @Test
    public void testPickLastFile(){
        Day09.FileTuple fileTuple = Day09.pickLastFile(new int[]{1,1,-1,2,2,2,-1,-1});
        assertThat(fileTuple.id()).isEqualTo(2);
        assertThat(fileTuple.size()).isEqualTo(3);
    }

    @Test
    public void testFindEmpty(){
        int[] blocks = {1, 1, -1, -1, -1, 2, 2, 2, -1, -1, 3, 3, -1, -1, -1, -1};
        assertThat(Day09.findEmptyIdx(3, blocks)).isEqualTo(2);
        assertThat(Day09.findEmptyIdx(4, blocks)).isEqualTo(8);
        assertThat(Day09.findEmptyIdx(1, blocks)).isEqualTo(2);
    }
}