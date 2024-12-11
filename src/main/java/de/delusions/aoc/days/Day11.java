package de.delusions.aoc.days;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day11.class);


    public Day11() {
        super("Plutonian Pebbles", 55312L, 65601038650482L, 216042L, 255758646442399L);
    }

    static Pattern REGEX = Pattern.compile("(\\d+)");

    @Override
    public Long part0(Stream<String> input) {
        List<Long> stones = readStones(input);
        return blink(stones, 25);
    }

    @Override
    public Long part1(Stream<String> input) {
        List<Long> stones = readStones(input);
        return blink(stones, 75);
    }

    static List<Long> readStones(Stream<String> input) {
        return REGEX.matcher(input.collect(Collectors.joining())).results().map(m -> m.group(1)).map(Long::parseLong).toList();
    }

    Map<Long, List<Long>> cache = new HashMap<>();

    long blink(List<Long> input, int blinks) {
        Map<Long, Long> prevBlink = input.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        for (int blink = 0; blink < blinks; blink++) {
            Map<Long,Long> nextBlink = new HashMap<>();
            Stack<Long> stack = new Stack<>();
            stack.addAll(prevBlink.keySet());
            while (!stack.isEmpty()) {
                Long current = stack.pop();
                nextStep(current).forEach(stone -> {
                    nextBlink.merge(stone, prevBlink.get(current), Long::sum);
                });
            }
            prevBlink.clear();
            prevBlink.putAll(nextBlink);
        }
        return prevBlink.values().stream().mapToLong(Long::longValue).sum();
    }

    List<Long> nextStep(Long stone) {
        List<Long> result;
        if (cache.containsKey(stone)) { return cache.get(stone); }
        if (stone == 0L) {
            result = List.of(1L);
        } else if (stone.toString().length() % 2 == 0) {
            long powerOfTen = (long) Math.pow(10, stone.toString().length() / 2);
            result = List.of( stone / powerOfTen, stone % powerOfTen);
        } else {
            result = List.of(stone*2024);
        }
        cache.put(stone,result);
        return result;
    }

    //for speed reasons I moved the code into the method above

    static List<Long> split(Long number) {
        long powerOfTen = (long) Math.pow(10, number.toString().length() / 2);
        return List.of(number / powerOfTen, number % powerOfTen);
    }


}
