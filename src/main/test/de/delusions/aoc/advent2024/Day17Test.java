package de.delusions.aoc.advent2024;

import de.delusions.util.Matrix;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
public class Day17Test {

    @Test
    public void testComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(0,0,9,2,6));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(1);
        assertThat(c.c().get()).isEqualTo(9);
        assertThat(c.isRunning()).isFalse();
    }

    @Test
    public void testAComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(0,29,0,1,7));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(26);
        assertThat(c.c().get()).isEqualTo(0);
        assertThat(c.isRunning()).isFalse();
    }

    @Test
    public void testTheComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(0,2024,43690,4,0));
        assertThat(c.isRunning()).isTrue();
        c.executeNextInstruction();
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.b().get()).isEqualTo(44354);
        assertThat(c.c().get()).isEqualTo(43690);
        assertThat(c.isRunning()).isFalse();
    }


    @Test
    public void testAnotherComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(10,0,0,5,0,5,1,5,4));
        while(c.isRunning()){
            c.executeNextInstruction();
        }
        assertThat(c.output()).isEqualTo(List.of(0,1,2));
    }

    @Test
    public void testYetAnotherComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(2024,0,0,0,1,5,4,3,0));
        while(c.isRunning()){
            c.executeNextInstruction();
            System.out.println(c.output());
        }
        assertThat(c.output()).isEqualTo(List.of(4,2,5,6,7,7,7,7,3,1,0));
    }

    @Test
    public void testTheTestComputer(){
        Day17.Computer c = Day17.Computer.create(List.of(729,0,0,0,1,5,4,3,0));
        while(c.isRunning()){
            c.executeNextInstruction();
        }
        assertThat(c.a().get()).isEqualTo(0);
        assertThat(c.output()).isEqualTo(List.of(4,6,3,5,6,3,5,2,1,0));
    }

    @Test
    public void testMath(){
        System.out.println(Math.pow(8,16));
        if(Math.pow(8,16) > Long.MAX_VALUE){ System.out.println("overflow");}
        System.out.println(Math.pow(2,3*16));
        Matrix m = new Matrix(8,8,0,0);
        for(int i=0; i<7; i++){
            for(int j=0; j<7; j++){
                m.setValue(i,j,i^j);
            }
        }
        m.setPrintMap(Map.of(0,"0",1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6", 7, "7"));
        System.out.println(m);
    }

}
