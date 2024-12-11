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

    @Override
    public Long part0(Stream<String> input) {
        return blink(readStones(input), 25);
    }

    @Override
    public Long part1(Stream<String> input) {
        return blink(readStones(input), 75);
    }

    static Pattern REGEX = Pattern.compile("(\\d+)");

    static List<Long> readStones(Stream<String> input) {
        return REGEX.matcher(input.collect(Collectors.joining())).results().map(m -> m.group(1)).map(Long::parseLong).toList();
    }

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

    Map<Long, List<Long>> cache = new HashMap<>();

    List<Long> nextStep(Long stone) {
        List<Long> result;
        if (cache.containsKey(stone)) { return cache.get(stone); }
        if (stone == 0L) {
            result = List.of(1L);
        } else if (stone.toString().length() % 2 == 0) {
            result = split(stone);
        } else {
            result = List.of(stone*2024);
        }
        cache.put(stone,result);
        return result;
    }

    static List<Long> split(Long number) {
        long powerOfTen = (long) Math.pow(10, number.toString().length() / 2);
        return List.of(number / powerOfTen, number % powerOfTen);
    }


}
