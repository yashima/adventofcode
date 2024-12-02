package de.delusions.aoc.days;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Day2 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day2.class);

    public Day2() {
        super(2, "Red-Nosed Reports", 2l, 4L, 421l, 476L);
    }

    public record Report(List<Long> levels, boolean activatedDamper) {
        Report addLevels(String line) {
            Arrays.stream(line.split("\\s+")).map(Long::parseLong).forEach(levels::add);
            return this;
        }

        public boolean safe() {
            boolean inc = inc();
            long errors = countErrors(levels, inc);
            if (errors == 0) {
                return true;
            }
            if (activatedDamper) {
                //yes, I am just trying every stupid element now. Because it still runs in 20ms
                for (int i = 0; i < levels.size(); i++) {
                    if (testDamper(i)) {
                        return true;
                    }
                }
            }
            return false;
        }

        boolean testDamper(int index) {
            List<Long> newLevels = new ArrayList<>(levels);
            newLevels.set(index, null);
            return new Report(newLevels.stream().filter(Objects::nonNull).toList(), false).safe();
        }

        boolean inc() {
            return levels.getFirst() - levels.getLast() < 0;
        }

        private long countErrors(List<Long> list, boolean inc) {
            AtomicLong prev = new AtomicLong(levels.getFirst());
            return list.subList(1, list.size()).stream().filter(Objects::nonNull).map(l -> l - prev.getAndSet(l)).filter(diff -> unsafeDiff(diff, inc)).count();
        }

        static long maxDiff = 3l;

        static boolean unsafeDiff(long diff, boolean inc) {
            return Math.abs(diff) > maxDiff || (inc && diff <= 0) || (!inc && diff >= 0);
        }

    }

    @Override
    public Long part0(Stream<String> input) {
        return input.map(l -> new Report(new ArrayList<>(), false).addLevels(l)).filter(Report::safe).count();
    }

    @Override
    public Long part1(Stream<String> input) {
        return input.map(l -> new Report(new ArrayList<>(), true).addLevels(l)).filter(Report::safe).count();
    }


}
