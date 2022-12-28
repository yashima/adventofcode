package de.delusions.aoc.advent2022;

import de.delusions.aoc.advent2022.Day19.MachineState;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static de.delusions.aoc.advent2022.Day19.MachineState.CANNOT_BUILD;
import static de.delusions.aoc.advent2022.Day19.Material.*;
import static org.testng.Assert.*;

public class Day19Test {

    Day19 classUnderTest;

    Day19.Blueprint blueprint;

    @BeforeMethod
    public void setup() {
        classUnderTest = new Day19();
        blueprint = new Day19.Blueprint( 1, 2, 3, 4, 5, 6, 7 );
    }


    @Test
    public void testProduce() {
        MachineState machine = new MachineState( blueprint );
        machine.state[ORE.ordinal()][ORE.ordinal()] = 2;
        machine.setProduction( ORE, 3 );
        machine.setProduction( CLAY, 5 );
        machine.produce( 1 );
        assertEquals( machine.getPile( ORE ), 3 );
        assertEquals( machine.getPile( CLAY ), 5 );
        machine.produce( 1 );
        //assertTrue(build);
        assertEquals( machine.getPile( ORE ), 2 * 3 );
        assertEquals( machine.getPile( CLAY ), 2 * 5 );

    }

    @Test
    public void testIsReadyToBuild() {
        //build clay
        MachineState machine = new MachineState( blueprint );
        machine.robotToBuild = CLAY;
        machine.state[CLAY.ordinal()][ORE.ordinal()] = 3;
        assertFalse( machine.isReadyToBuild() );
        machine.state[ORE.ordinal()][PILE.ordinal()] = 4;
        assertTrue( machine.isReadyToBuild() );

        //build obsidian
        machine = new MachineState( blueprint );
        machine.robotToBuild = OBSIDIAN;
        machine.state[OBSIDIAN.ordinal()][ORE.ordinal()] = 3;
        machine.state[OBSIDIAN.ordinal()][CLAY.ordinal()] = 5;
        assertFalse( machine.isReadyToBuild() );
        machine.state[ORE.ordinal()][PILE.ordinal()] = 4;
        assertFalse( machine.isReadyToBuild() );
        machine.state[CLAY.ordinal()][PILE.ordinal()] = 5;
        assertTrue( machine.isReadyToBuild() );
    }

    @Test
    public void testStepsUntilBuild() {
        //check steps for clay
        MachineState machine = new MachineState( blueprint );
        machine.robotToBuild = CLAY;
        assertFalse( machine.isReadyToBuild() );
        assertEquals( machine.stepsUntilBuild(), 3 );
        machine.setPile( ORE, 4 );
        assertEquals( machine.stepsUntilBuild(), 0 );

        //check steps for geode
        machine = new MachineState( blueprint );
        machine.robotToBuild = GEODE;
        machine.setProduction( OBSIDIAN, 0 );
        assertEquals( machine.stepsUntilBuild(), CANNOT_BUILD );
        machine.state[GEODE.ordinal()][ORE.ordinal()] = 2;
        machine.state[GEODE.ordinal()][OBSIDIAN.ordinal()] = 10;
        machine.state[ORE.ordinal()][PROD.ordinal()] = 1;
        machine.state[OBSIDIAN.ordinal()][PROD.ordinal()] = 3;
        assertEquals( machine.stepsUntilBuild(), 4 );

    }

}