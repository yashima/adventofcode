package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Day25 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day25.class);

    public Day25() {
        super("", 3L, 0L, 0L, 0L);
    }

    static int LOCK_SIZE = 5;
    static int BLOCK = 7;
    static AtomicInteger ID_COUNTER = new AtomicInteger(0);
    static Map<Integer, int[]> VALUE_CACHE = new HashMap<>();

    record Key(int id, Matrix matrix) {
        int[] getValues() {
            return VALUE_CACHE.computeIfAbsent(id, u -> computeValues(matrix));
        }
        @Override
        public String toString() {
            return Arrays.toString(getValues());
        }
    }

    record Lock(int id, Matrix matrix) {
        int[] getValues() {
            return VALUE_CACHE.computeIfAbsent(id, u -> computeValues(matrix));
        }

        @Override
        public String toString() {
            return Arrays.toString(getValues());
        }
    }

    static int[] computeValues(Matrix matrix) {
        int[] result = new int[matrix.getYLength()];
        for (int y = 0; y < matrix.getYLength(); y++) {
            result[y] = matrix.findValuesInColumn(y, '#').size();
        }
        return result;
    }


    @Override
    public Long part0(Stream<String> input) {
        List<String> lines = input.filter(Predicate.not(String::isBlank)).toList();
        List<Lock> locks = new ArrayList<>();
        List<Key> keys = new ArrayList<>();
        for (int i = 0; i < lines.size() / BLOCK; i++) {
            List<String> lockLines = lines.subList(i * BLOCK + 1, i * BLOCK + 1 + LOCK_SIZE);
            if (lines.get(i * BLOCK).contains("#")) {
                locks.add(new Lock(ID_COUNTER.incrementAndGet(), Matrix.createFromString(lockLines)));
            } else {
                keys.add(new Key(ID_COUNTER.incrementAndGet(), Matrix.createFromString(lockLines)));
            }
        }

        AtomicLong counter = new AtomicLong(0);
        locks.forEach(lock ->
                keys.forEach(key -> {
                    if(IntStream
                            .range(0, lock.getValues().length)
                            .noneMatch(index -> lock.getValues()[index] + key.getValues()[index] > LOCK_SIZE)){
                        counter.incrementAndGet();
                    }
                }));
        return counter.get();
    }


    @Override
    public Long part1(Stream<String> input) {
        return 0L;
    }
}
