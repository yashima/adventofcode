package de.delusions.aoc;

import de.delusions.aoc.advent2024.Day08;
import de.delusions.aoc.advent2024.Day13;
import de.delusions.util.Day;

import java.time.LocalDate;

public class AdventOfCode {
    public static void main(String[] args) {

        LocalDate currentDate = LocalDate.now();
        Day<?> toDay = Day.loadDayClass(currentDate.getDayOfMonth());
        runAllVariants(toDay);

    }

    private static void runAllVariants(Day<?> today) {
        today.run(true, 0);
        today.run(false, 0);
        today.run(true, 1);
        today.run(false, 1);
    }
}
