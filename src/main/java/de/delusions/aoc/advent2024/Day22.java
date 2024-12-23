package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public class Day22 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day22.class);

    static BigInteger NO_64 = BigInteger.valueOf(64);
    static BigInteger NO_32 = BigInteger.valueOf(32);
    static BigInteger NO_2048 = BigInteger.valueOf(2048);
    static BigInteger NO_16777216 = BigInteger.valueOf(16777216);

    static Map<String, List<Integer>> SEQUENCE_CACHE = new HashMap<>();

    public Day22() {
        super("Monkey Market", 37327623L, 23L, 19927218456L, 2189L);
    }


    class Generator {

        int start;

        BigInteger current;

        int step;
        int[] prices = new int[2000];
        int[] diff = new int[2000];

        Map<String, Integer> localCache = new HashMap<>();

        Generator(int start) {
            this.start = start;
            this.current = BigInteger.valueOf(start);
            this.step = 0;
        }

        BigInteger mix(BigInteger value) {
            return value.xor(current);
        }

        BigInteger prune(BigInteger value) {
            return value.mod(NO_16777216);
        }

        void step() {
            current = prune(mix(current.multiply(NO_64)));
            current = prune(mix(current.divide(NO_32)));
            current = prune(mix(current.multiply(NO_2048)));
            prices[step] = current.intValue() % 10;
            diff[step] = step == 0 ? 0 : prices[step] - prices[step - 1];
            cacheDiffSequence();
            step++;
        }

        long getSequenceNumber(int index) {
            current = BigInteger.valueOf(start);
            step = 0;
            while (step < index) {
                step();
            }
            return current.longValue();
        }

        Generator finalizePrices(int index) {
            getSequenceNumber(index);
            //merge cache
            localCache.forEach((key, value) -> {
                SEQUENCE_CACHE.putIfAbsent(key, new ArrayList<>());
                SEQUENCE_CACHE.get(key).add(value);
            });
            return this;
        }

        void cacheDiffSequence() {
            if (this.step >= 4) {
                String diffSeq = String.format("%d,%d,%d,%d", diff[step - 3], diff[step - 2], diff[step - 1], diff[step]);
                if (!localCache.containsKey(diffSeq)) {
                    localCache.put(diffSeq, prices[step]);
                }
            }
        }
    }

    @Override
    public Long part0(Stream<String> input) {
        return input.mapToInt(Integer::parseInt).mapToObj(Generator::new).mapToLong(g -> g.getSequenceNumber(2000)).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        SEQUENCE_CACHE.clear();

        input.mapToInt(Integer::parseInt).mapToObj(Generator::new).forEach(g -> g.finalizePrices(2000));

        return SEQUENCE_CACHE.values().stream()
                .mapToLong(l -> l.stream().mapToInt(i -> i).sum())
                .max().orElse(-1);
    }
}
