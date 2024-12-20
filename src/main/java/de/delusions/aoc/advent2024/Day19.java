package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19 extends Day<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(Day19.class);

    private static Pattern metaPattern;

    List<String> towels;
    Map<String, Memo> cache = new HashMap<>();

    public Day19() {
        super("Linen Layout", 6L, 16L, 342L, 891192814474630L);
    }

    @Override
    public Long part0(Stream<String> input) {
        List<String> patterns = input.toList();
        towels = Arrays.stream(patterns.getFirst().split(",")).map(String::trim).toList();
        metaPattern = Pattern.compile(String.format("(%s)+", String.join("|", towels)));
        return (long)patterns.stream().skip(2).filter(Day19::isValid).toList().size();
    }


    @Override
    public Long part1(Stream<String> input) {
        cache.clear();
        List<String> patterns = input.toList();
        towels = Arrays.stream(patterns.getFirst().split(",")).map(String::trim).toList();
        metaPattern = Pattern.compile(String.format("(%s)+", String.join("|", towels)));
        return patterns.stream()
                .skip(2)
                .mapToLong(line -> countVariants(line))
                .sum();
    }

    static boolean isValid(String line) {
        return line.isEmpty() || metaPattern.matcher(line).matches();
    }

    Long countVariants(Object key) {
        String suffix = (String)key;
        if (suffix.isEmpty()) { return 1L; }
        return towels.stream()
                .filter(suffix::startsWith)
                .filter(towel -> isValid(suffix.substring(towel.length())))
                .map(towel -> suffix.substring(towel.length()))
                .mapToLong(newSuffix -> cache
                        .computeIfAbsent(newSuffix, Memo::new)
                        .compute(this::countVariants))
                .sum();
    }


    //MEMO CACHE KEY -> need to write my own memoization abstraction
    //^^ this comment is for future reference in full text search!
    //inspired by: https://github.com/dashie/AdventOfCode2024/blob/main/src/main/java/adventofcode/commons/MemoizationCache.java
    class Memo<K,V> {
        K key;

        long value;

        Memo(K key) {
            this.key = key;
            this.value = 0;
        }
        long compute(Function<K,Long> comp) {
            if (value == 0) {
                value = comp.apply(key);
            }
            return value;
        }

    }


}
