package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 extends Day<Integer> {


    public Day3() {
        super("Mull It Over", 161, 48, 174103751, 100411201);
    }


    private Integer getSum(Matcher matcher, AtomicBoolean multido) {
        AtomicInteger sum = new AtomicInteger(0);
        matcher.results().forEach(m -> {
            if (m.group(2) != null && multido.get()) {
                int a = Integer.parseInt(m.group(2));
                int b = Integer.parseInt(m.group(3));
                sum.accumulateAndGet(a * b, (i, j) -> i + j);
            } else{
                if (m.group(4) != null) {
                    multido.set(m.group(4).equals("do()"));
                }
            }
        });
        return sum.get();
    }

    static final String REGEX = "(mul\\((\\d{1,3}),(\\d{1,3})\\))";


    @Override
    public Integer part0(Stream<String> input) {
        String line = input.collect(Collectors.joining());
        return getSum(Pattern.compile(REGEX).matcher(line), new AtomicBoolean(true));
    }

    static final String REGEXTRA = "(mul\\((\\d{1,3}),(\\d{1,3})\\)|(do\\(\\)|don't\\(\\)))";

    @Override
    public Integer part1(Stream<String> input) {
        String line = input.collect(Collectors.joining());
        return getSum(Pattern.compile(REGEXTRA).matcher(line), new AtomicBoolean(true));
    }
}
