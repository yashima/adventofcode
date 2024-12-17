package de.delusions.aoc.advent2024;

import de.delusions.util.Matrix;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

public class Day17Test {

    @Test
    public void testComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(0, 0, 9, 2, 6));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(1);
        assertThat(c.c().get()).isEqualTo(9);
        assertThat(c.isRunning()).isFalse();
    }

    @Test
    public void testAComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(0, 29, 0, 1, 7));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(26);
        assertThat(c.c().get()).isEqualTo(0);
        assertThat(c.isRunning()).isFalse();
    }

    @Test
    public void testTheComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(0, 2024, 43690, 4, 0));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(44354);
        assertThat(c.c().get()).isEqualTo(43690);
        assertThat(c.isRunning()).isFalse();
    }


    @Test
    public void testAnotherComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(10, 0, 0, 5, 0, 5, 1, 5, 4));
        while (c.isRunning()) {
            c.executeNextInstruction();
        }
        assertThat(c.output()).isEqualTo(List.of(0, 1, 2));
    }

    @Test
    public void testYetAnotherComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(2024, 0, 0, 0, 1, 5, 4, 3, 0));
        while (c.isRunning()) {
            c.executeNextInstruction();
            System.out.println(c.output());
        }
        assertThat(c.output()).isEqualTo(List.of(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0));
    }

    @Test
    public void testTheTestComputer() {
        Day17.Computer c = Day17.Computer.create(List.of(729, 0, 0, 0, 1, 5, 4, 3, 0));
        while (c.isRunning()) {
            c.executeNextInstruction();
        }
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.output()).isEqualTo(List.of(4, 6, 3, 5, 6, 3, 5, 2, 1, 0));
    }


    @Test
    public void testSpielwiese() {
        List<Integer> program = List.of(2,4,1,1,7,5,0,3,1,4,4,5,5,5,3,0);
        Day17.Computer c = Day17.Computer.create(List.of(0,0,0,2,4,1,1,7,5,0,3,1,4,4,5,5,5,3,0));
        // 0->5,1->4,2->7,3->6, [4,5]->11,5->1/6,6->3
        // 5, 46,
        // 0 -> [5]
        // 1 -> [5]
        // 2 -> [7]
        // 3 -> [6]
        // 4 -> [1]
        // 5 -> [0]
        // 6 -> [3]
        // 7 -> [2]
        // 8 -> [1,5]
        // 9 -> [5,5] 1+1*8
        //10 -> [6,5]
        //11 -> [4,5]
        //12 -> [1,5]
        //13 -> [0,5]

        AtomicLong sum = new AtomicLong(0);
        List<Long> target = new ArrayList();
        program.reversed().forEach( i -> {
            target.add(0,(long)i);
            long counter = sum.get()*8-1;
            while(!c.output().equals(target)){
                counter++;
                c.reset(counter);
                while (c.isRunning()) {
                    c.executeNextInstruction();
                }
                sum.set(counter);
            }
        });
        System.out.println(sum.get());
        System.out.println(c.output());
    }

    @Test
    public void testMath() {
        System.out.println(Math.pow(8, 16));
        if (Math.pow(8, 16) > Long.MAX_VALUE) {
            System.out.println("overflow");
        }
        System.out.println(Math.pow(2, 3 * 16));
        System.out.println(1 ^ 4);
        //a%8->i (0..7) i ^ 5 ^ (x * 8 / i in List.of(0,3,5,9,17,33,65,129)
        //5 binär: 101
        //4 binär: 100
        for (int i = 0; i <= 7; i++) {
            System.out.println(String.format("Sei a mod 8=%d i^5=%d, divisor=%d", i, i ^ 5, ((int) Math.pow(2, i) ^ 1)));
            System.out.println(String.format("4 = %d xor X / %d", i ^ 5, ((int) Math.pow(2, i) ^ 2)));

        }

    }

}
