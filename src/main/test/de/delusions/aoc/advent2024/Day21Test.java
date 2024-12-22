package de.delusions.aoc.advent2024;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Day21Test {

    @DataProvider(name = "codeProvider")
    public Object[][] codeProvider() {
        
        return new Object[][]{
            {"029A", "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"},
            {"980A", "<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A"},
            {"179A", "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"},
            {"456A", "<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A"},
            {"379A", "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"}
        };
    }

    @Test(dataProvider = "codeProvider")
    public void testGetCodeString(String code, String expectedResult) {
       Day21.State result = new Day21.RobotsAllTheWayDown(code).pressSomeButtons();
        assertEquals(result.target().length(), expectedResult.length(),"Length wrong: " + code);
        //assertEquals(result.target(), expectedResult,"Failed for code: " + code);
    }


    //<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
    //<Av<AAvA
}