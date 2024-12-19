package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 extends Day<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(Day19.class);

    public Day19() {

        super("", 6, 16,342, 0);
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
        CACHE.clear();
        List<String> patterns = input.collect(Collectors.toList());
        List<String> towels = Arrays.stream(patterns.get(0).split(",")).map(String::trim).toList();
        Pattern metaPattern = Pattern.compile(String.format("(%s)+", towels.stream().collect(Collectors.joining("|"))));
        return patterns.stream()
                .skip(2)
                .filter( line -> metaPattern.matcher(line).matches())
                .mapToInt(line -> countVariants(towels, line))
                .sum();
    }


    static Map<String,Set<String>> CACHE = new HashMap<>();

    record Variant(String start, String towelsUsed, String patternEnd){
        static List<Variant> extendFrom(String key, Variant variant){
            return CACHE.get(key).stream()
                    .map(entry -> new Variant(variant.start+key,
                            String.format("%s%s%s",variant.towelsUsed, variant.towelsUsed=="" ? "" : "-", entry),
                            variant.patternEnd.substring(key.length())))
                    .toList();
        }

    }

    int countVariants(List<String> towels, String pattern){
        towels.forEach(t -> addToCache(t,t));
        Set<Variant> good = new HashSet<>();
        Set<Variant> visited = new HashSet<>();
        Stack<Variant> stack = new Stack<>();
        stack.push(new Variant("","", pattern));
        while(!stack.isEmpty()){
            Variant current = stack.pop();
            visited.add(current);
            if (current.patternEnd.isEmpty()){
                good.add(current);
            } else {
                getNext(towels, current).forEach(v -> {
                    if (!visited.contains(v)) {
                        stack.push(v);
                    }
                });
            }

        }
        System.out.println("Finished "+pattern);
        return good.size();
    }

    void addToCache(String start,String towelsUsed){
        CACHE.putIfAbsent(start, new HashSet<>());
        CACHE.get(start).add(towelsUsed);
    }


    List<Variant> getNext(List<String> towels, Variant variant){
        List<Variant> list = CACHE.keySet().stream()
                .filter(key -> variant.patternEnd.startsWith(key))
                .map(key -> Variant.extendFrom(key, variant))
                .flatMap(List::stream)
                .toList();
        list.stream().forEach(v -> addToCache(v.start,v.towelsUsed));
        return list;
    }


}
