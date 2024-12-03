package de.delusions.aoc;

import de.delusions.aoc.days.Day1;
import de.delusions.aoc.days.Day2;
import de.delusions.aoc.days.Day3;
import de.delusions.util.Day;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
