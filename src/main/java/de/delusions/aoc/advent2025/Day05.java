package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import de.delusions.util.Interval;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Day 5: Checking intervals for numbers. And then merging all the intervals to get all the numbers.
 * I briefly considered just brute-forcing a huge list. But since I had the mathy stuff already from previous years...
 *
 * Part 1: Products in certain product ID ranges are fresh, others are spoiled. Find ids in the second part of the input that are still fresh
 * Part 2: Count all the numbers that are part of the intervals
 */
@Slf4j
public class Day05 extends Day<Long> {
    public Day05() {
        super("Cafeteria", 3L, 14L, 598L, 360341832208407L);
    }

    @Setter
    List<Interval> intervals = new ArrayList<>();

    boolean isSpoiled(long productId) {
        return intervals.stream().noneMatch(i -> i.contains(productId));
    }

    boolean isFresh(long productId){
        return !isSpoiled(productId);
    }

    @Override
    public Long part0(Stream<String> input) {
        intervals.clear();
        List<Long> products = new ArrayList<>();
        input.forEach(line -> {
            if (line.contains("-")) {
                intervals.add(Interval.from(line));
            } else if(!line.isBlank()){
                products.add(Long.parseLong(line));
            }
        });
        return products.stream().filter(this::isFresh).count();
    }

    @Override
    public Long part1(Stream<String> input) {
        intervals.clear();
        input.filter(l -> l.contains("-")).forEach(line -> intervals.add(Interval.from(line)));
        List<Interval> mergedIntervals = new ArrayList<>();
        for(Interval interval : intervals){
            List<Interval> overlaps = mergedIntervals.stream().filter( m -> m.overlap(interval)).toList();
            mergedIntervals.removeAll(overlaps);
            mergedIntervals.add(overlaps.stream().reduce(interval, Interval::union));
        }
        return mergedIntervals.stream().mapToLong(Interval::length).sum();
    }
}
