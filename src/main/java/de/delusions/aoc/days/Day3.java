package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 extends Day<Integer> {


    public Day3(){
        super(3, "Mull It Over",161,48,174103751,100411201);
    }

    static final String REGEX = "mul\\((\\d{1,3}),(\\d{1,3})\\)";
    static final Pattern pattern = Pattern.compile(REGEX);

    @Override
    public Integer part0(Stream<String> input) {
        String line = input.collect(Collectors.joining());
        AtomicInteger sum = new AtomicInteger(0);
        pattern.matcher(line).results().forEach(m -> {
            int a = Integer.parseInt(m.group(1));
            int b = Integer.parseInt(m.group(2));
            sum.accumulateAndGet(a*b , (i,j) -> i + j);
        });
        return sum.get();
    }

    static final String REGEXTRA = "(mul\\((\\d{1,3}),(\\d{1,3})\\)|(do\\(\\)|don't\\(\\)))";
    static final Pattern patternextra = Pattern.compile(REGEXTRA);

    @Override
    public Integer part1(Stream<String> input) {
        String line = input.collect(Collectors.joining());
        AtomicInteger sum = new AtomicInteger(0);
        AtomicBoolean multido = new AtomicBoolean(true);
        patternextra.matcher(line).results().forEach(m -> {
            if(m.group(2) != null) {
                if(multido.get()) {
                    int a = Integer.parseInt(m.group(2));
                    int b = Integer.parseInt(m.group(3));
                    sum.accumulateAndGet(a * b, (i, j) -> i + j);
                }
            } else {
                if(m.group(4)!=null) {
                    multido.set(m.group(4).equals("do()"));
                }
            }
        });
        return sum.get();
    }
}
