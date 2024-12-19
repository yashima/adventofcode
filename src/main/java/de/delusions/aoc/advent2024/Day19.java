package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 extends Day<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(Day19.class);

    public Day19() {

        super("", 6, 0, 0, 0);
    }



    @Override
    public Integer part0(Stream<String> input) {
        List<String> patterns = input.collect(Collectors.toList());
        List<String> towels = Arrays.stream(patterns.get(0).split(",")).map(String::trim).toList();
        Pattern metaPattern = Pattern.compile(String.format("(%s)+", towels.stream().collect(Collectors.joining("|"))));
        return patterns.stream().skip(2).filter(line -> metaPattern.matcher(line).matches()).toList().size();
    }


    @Override
    public Integer part1(Stream<String> input) {
        return 0;
    }


}
