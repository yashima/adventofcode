package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 extends Day<String> {

    public Day1() {
        super(1, "Historian Hysteria","11","0" ,"0","0");
    }

    @Override
    public String part0(Stream<String> input) {
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        input.forEach(line -> {
            String[] parts = line.split("\\s+");
            a.add(Integer.parseInt(parts[0].trim()));
            b.add(Integer.parseInt(parts[1].trim()));
        });
        a.sort(Integer::compareTo);
        b.sort(Integer::compareTo);
        int sum = 0;
        for(int i = 0; i < a.size(); i++) {
            sum += Math.abs(a.get(i) - b.get(i));
        }
        return sum+"";
    }

    @Override
    public String part1(Stream<String> input) {

        return "0";
    }
}
