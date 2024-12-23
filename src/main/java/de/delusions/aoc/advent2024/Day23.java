package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day23 extends Day<String> {
    private static final Logger LOG = LoggerFactory.getLogger(Day23.class);

    public Day23() {
        super("LAN Party", "7", "co,de,ka,ta", "1323", "er,fh,fi,ir,kk,lo,lp,qi,ti,vb,xf,ys,yu");
    }

    static Pattern REGEX = Pattern.compile("(..)-(..)");

    record Computer(String id, Set<String> connections) {

        Set<String> intersection(Computer that) {
            Set<String> result = new HashSet<>(this.connections);
            result.retainAll(that.connections);
            return result;
        }

        Set<Set<String>> getNetworks(Map<String, Computer> cache) {
            Set<Set<String>> result = new HashSet<>();
            for (String connection : connections) {
                Set<String> network = new HashSet<>(List.of(connection, this.id));
                Set<String> intersection = intersection(cache.get(connection));
                if (!intersection.isEmpty()) {
                    for (String candidate : intersection) {
                        if (network.stream().allMatch(netComp -> cache.get(candidate).connections.contains(netComp))) {
                            network.add(candidate);
                        }
                    }
                }
                //just ignore stuff that's not really a network:
                if (network.size() > 2) {
                    result.add(network);
                }
            }
            return result;
        }
    }

    //this is for the part 1 groups of 3. Could have been easier because this need not be parametrized
    //with just 3 could have been hardcoded...
    Set<Set<String>> getUniqueGroups(Set<String> networks, int size) {
        Set<Set<String>> result = new HashSet<>();
        if (!networks.stream().anyMatch(s -> s.startsWith("t"))) {
            //do nothing
        } else if (networks.size() == size) {
            result.add(networks);
        } else {
            for (String computer : networks) {
                if (computer.startsWith("t")) {
                    result.addAll(makeGroups(new HashSet<>(List.of(computer)), networks, 3));
                }
            }
        }
        return result;
    }

    private Set<Set<String>> makeGroups(Set<String> current, Set<String> networks, int size) {
        if (current.size() == size) {
            return Set.of(current);
        }
        Set<Set<String>> result = new HashSet<>();
        Set<String> nextNetwork = new HashSet<>(networks);
        nextNetwork.removeAll(current);
        for (String comp : nextNetwork) {
            Set<String> nextCurrent = new HashSet<>(current);
            nextCurrent.add(comp);
            result.addAll(makeGroups(nextCurrent, nextNetwork, size));
        }
        return result;
    }

    @Override
    public String part0(Stream<String> input) {
        final Map<String, Computer> cache = processInput(input);
        return cache.values().stream()
                .map(c -> c.getNetworks(cache))
                .flatMap(Collection::stream)
                .map(network -> getUniqueGroups(network, 3))
                .flatMap(Collection::stream)
                .distinct()
                .count() + "";
    }

    @Override
    public String part1(Stream<String> input) {
        Map<String, Computer> cache = processInput(input);
        return cache.values().stream()
                .map(c -> c.getNetworks(cache))
                .flatMap(Collection::stream)
                .distinct()
                .max(Comparator.comparing(Set::size))
                .get()
                .stream()
                .toList()
                .stream()
                .sorted()
                .collect(Collectors.joining(","));

    }


    private static Map<String, Computer> processInput(Stream<String> input) {
        Map<String,Computer> cache = new HashMap<>();
        input.filter(Predicate.not(String::isEmpty)).forEach(l -> REGEX.matcher(l).results().forEach(m -> createAndCacheComputer(m.group(1), m.group(2), cache)));
        return cache;
    }

    static void createAndCacheComputer(String first, String second, Map<String, Computer> cache) {
        Computer one = cache.putIfAbsent(first, new Computer(first, new HashSet<>(List.of(second))));
        Computer two = cache.putIfAbsent(second, new Computer(second, new HashSet<>(List.of(first))));
        if (one != null) {
            one.connections.add(second);
        }
        if (two != null) {
            two.connections.add(first);
        }
    }
}
