package de.delusions.aoc.advent2022;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class Day20Test {

    Day20 classUnderTest = new Day20();

    int[] input = {1, 2, -3, 3, -2, 0, 4};

    int BORDER = input.length + 1; //because we add an element each test

    Day20.CryptPair cp;

    @BeforeMethod
    public void setup() {
        classUnderTest.ID.set( 0 );
        classUnderTest.numbers = new LinkedList<>();
        Arrays.stream( input ).forEach( number -> classUnderTest.numbers.add( Day20.newPair( number ) ) );
        Assert.assertEquals( input.length, classUnderTest.numbers.size() );
    }

    @Test
    public void testIndexUnique() {
        cp = new Day20.CryptPair( 0, 1, false );
        Day20.CryptPair twin = Day20.newPair( 1 );
        classUnderTest.numbers.add( twin );
        int index = classUnderTest.numbers.indexOf( cp );
        int lastIndexOf = classUnderTest.numbers.lastIndexOf( cp );
        assertEquals( index, lastIndexOf );
        assertEquals( index, 0 );
        assertNotEquals( index, classUnderTest.numbers.indexOf( twin ) );
    }

    @Test
    public void testOGTestInput() {
        //step right
        cp = classUnderTest.getById( 0 ); //1
        assertEquals( cp.number(), input[0] );
        int index = classUnderTest.numbers.indexOf( cp );
        int newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 1 );
        classUnderTest.movePair( cp );

        //step right
        cp = classUnderTest.getById( 1 ); //2
        assertEquals( cp.number(), input[1] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 2 );
        classUnderTest.movePair( cp );

        //loop left
        cp = classUnderTest.getById( 2 ); //-3
        assertEquals( cp.number(), input[2] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 4 );
        classUnderTest.movePair( cp );

        //(multi-)step right
        cp = classUnderTest.getById( 3 ); //3
        assertEquals( cp.number(), input[3] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 5 );
        classUnderTest.movePair( cp );

        //to the end of the list
        cp = classUnderTest.getById( 4 ); //-2
        assertEquals( cp.number(), input[4] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 6 );
        classUnderTest.movePair( cp );

        //zero stays
        cp = classUnderTest.getById( 5 ); //0
        assertEquals( cp.number(), input[5] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 3 );
        classUnderTest.movePair( cp );

        //loop right
        cp = classUnderTest.getById( 6 ); //4
        assertEquals( cp.number(), input[6] );
        index = classUnderTest.numbers.indexOf( cp );
        newIndex = classUnderTest.calculateNewIndex( cp, index );
        assertEquals( newIndex, 3 );
        classUnderTest.movePair( cp );
    }

    @Test
    public void testStepRight() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( 1 ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 3 );
    }

    @Test
    public void testStepLeft() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( -1 ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 1 );
    }

    @Test
    public void testZero() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( -2 ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), classUnderTest.numbers.size() - 1 );
    }

    @Test
    public void testInputLength() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( BORDER ) );//numbers grows with test
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 3 );
    }

    @Test
    public void testStepRightLoop() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( 1 + BORDER ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 4 );
    }

    @Test
    public void testStepLeftLoop() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( -1 - BORDER ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), BORDER - 1 );
    }

    @Test
    public void testStepRightMultiLoop() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( 1 + 2 * BORDER ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 5 );
    }

    @Test
    public void testStepLeftMultiLoop() {
        classUnderTest.numbers.add( 2, cp = Day20.newPair( -1 - 2 * BORDER ) );
        assertEquals( classUnderTest.calculateNewIndex( cp, 2 ), 6 );
    }
}