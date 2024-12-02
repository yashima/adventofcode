package de.delusions.aoc.days;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class Day2Test {

    @DataProvider(name = "reportData")
    public Object[][] createReportData() {
        return new Object[][] {
                { new Day2.Report(new ArrayList<>(List.of(1L,2L,3L,4L,5L)), true), true },
                { new Day2.Report(new ArrayList<>(List.of(7L,6L,4L,2L,1L)), true), true },
                { new Day2.Report(new ArrayList<>(List.of(1L, 2L, 7L, 8L, 9L)), true), false },
                { new Day2.Report(new ArrayList<>(List.of(9L, 7L, 6L, 2L, 1L)), true), false },
                { new Day2.Report(new ArrayList<>(List.of(1L ,3L, 2L, 4L )), true), true },
                { new Day2.Report(new ArrayList<>(List.of(8L,6L,4L,4L,1L)), true), true },
                { new Day2.Report(new ArrayList<>(List.of(6L,4L,4L,1L)), true), true },
                { new Day2.Report(new ArrayList<>(List.of(1L, 3L, 6L, 7L, 9L)), true), true },
                { new Day2.Report(new ArrayList<>(List.of(48L, 51L, 52L, 55L, 58L, 61L, 58L, 57L)),true),false},
                { new Day2.Report(new ArrayList<>(List.of(22L, 25L, 28L, 30L, 28L, 32L)),true),true}

        };
    }

    @Test(dataProvider = "reportData")
    public void testSimpleDamper(Day2.Report report, boolean expected) {
        assertThat(report.safe()).isEqualTo(expected);
    }


    @DataProvider(name = "diffData")
    public Object[][] createDiffData() {
        return new Object[][] {
                {2, true, false },   // diff is within maxDiff, increasing
                {2, false, true },   // diff is within maxDiff, not increasing
                { 10, true, true },   // diff is above maxDiff, increasing
                { -10, false, true }, // diff is above maxDiff, decreasing
                { 0, true, true },    // diff is zero, increasing
                { 0, false, true },  // diff is zero, decreasing
                { -2, true, true },   // negative diff, increasing
                { -2, false, false }   // positive diff, not increasing
        };
    }

    @Test(dataProvider = "diffData")
    public void testUnsafeDiff(int diff, boolean inc, boolean expected) {
        assertThat(Day2.Report.unsafeDiff(diff, inc)).isEqualTo(expected);
    }

    @Test
    public void testComplexDamper(){

    }
    
}