package de.delusions.aoc.advent2024;

import org.testng.annotations.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.*;
public class Day7Test {

    @Test
    public void testConOperator() {
        assertThat(Day07.Operator.CON.deoperate(156L,6L)).isEqualTo(15L);
        assertThat(Day07.Operator.CON.deoperate(6L,6L)).isEqualTo(0L);
        assertThat(Day07.Operator.CON.deoperate(6234L,6L)).isNull();
        assertThat(Day07.Operator.CON.deoperate(63L,663L)).isNull();
        assertThat(Day07.Operator.CON.deoperate(123453L,453L)).isEqualTo(123L);
    }

    @Test
    public void testSolvable(){
        //192: 17 8 14 can be made true using 17 || 8 + 14
        Day07.Equation equation = new Day07.Equation("",192L, List.of(17L,8L,14L));
        assertThat(equation.solveByLast(List.of(Day07.Operator.ADD, Day07.Operator.MUL))).isFalse();
        assertThat(equation.solveByLast(List.of(Day07.Operator.ADD, Day07.Operator.MUL, Day07.Operator.CON))).isTrue();
    }

}