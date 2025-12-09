package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import de.delusions.util.dimensions.TupelLong;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

/**
 * Not my finest day. The playground proved that sometimes code does weird things. Again.
 * Treemaps however are cool maps. I will keep those in mind for future reference. Those are fast even with 500.000 entries!
 *
 * Part 1: Sort pairs of coordinates by distance, take the shortest n and find out how they are grouped into 'networks'
 * Part 2: Sort pairs by distance again, this time find out which pair finalizes the circuit to connect all coordinates
 */
public class Day08 extends Day<Long> {
    public Day08() {
        super("Playground", 40L, 25272L, 175500L, 6934702555L);
    }

    record Pair(TupelLong a, TupelLong b) {
        BigDecimal distance() {
            return a.distance(b);
        }
    }

    List<TupelLong> coordinates;
    TreeMap<BigDecimal, Pair> pairs = new TreeMap<>();
    List<Set<Integer>> networks = new ArrayList<>();

    @Override
    public Long part0(Stream<String> input) {

        this.coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i + 1; j < coordinates.size(); j++) {
                Pair pair = new Pair(coordinates.get(i), coordinates.get(j));
                BigDecimal distance = pair.distance();
                //< because we can add one last pair before we need to check
                if (pairs.size() < (isTestMode() ? 10 : 1000)) {
                    pairs.put(distance, pair);
                    //distance is less than lastKey which is the biggest by definition of Treemap
                } else if (distance.compareTo(pairs.lastKey()) < 0) {
                    pairs.remove(pairs.lastKey());
                    pairs.put(distance, pair);
                }
            }
        }

        pairs.values().forEach(pair -> findNetwork(pair.a, pair.b));

        //This part works:
        networks.sort(Comparator.comparingInt(Set::size));
        networks = networks.reversed();
        return (long) networks.stream()
                .mapToInt(Set::size)
                .limit(3)
                //.peek(System.out::println)
                .reduce(1, (a, b) -> a * b);
    }


    void findNetwork(TupelLong tupelA, TupelLong tupelB) {
        Set<Integer> networkA = findNetwork(tupelA);
        Set<Integer> networkB = findNetwork(tupelB);
        if (networkA == null && networkB == null) {
            networks.add(new HashSet<>(Arrays.asList(tupelA.getId(), tupelB.getId())));
        } else if (networkB != null && networkA == null) {
            networkB.add(tupelA.getId());
        } else if (networkA != null && networkB == null) {
            networkA.add(tupelB.getId());
        } else { //both non null: merge the networks
            if (!networkA.equals(networkB)) {
                networks.remove(networkB);
                networkA.addAll(networkB);
            } else {
                //there is actually nothing to do A and B are in the same network
            }
        }
    }

    Set<Integer> findNetwork(TupelLong tupel) {
        return networks.stream().filter(p -> p.contains(tupel.getId())).findFirst().orElse(null);
    }

    @Override
    public Long part1(Stream<String> input) {
        TupelLong.resetID(); //I have no idea why this fixed the last issue with my code.
        this.coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        Pair last = findPairThatConnectsTheCircuit();
        return last.a.get(0) * last.b.get(0);
    }

    private Pair findPairThatConnectsTheCircuit() {
        Set<Integer> usedTupels = new HashSet<>();
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i + 1; j < coordinates.size(); j++) {
                Pair pair = new Pair(coordinates.get(i), coordinates.get(j));
                pairs.put(pair.distance(), pair);
            }
        }
        for(Pair pair : pairs.values()) {
            usedTupels.add(pair.a.getId());
            usedTupels.add(pair.b.getId());
            if(usedTupels.size()==coordinates.size()) return pair;
        }
        return null;
    }
}
