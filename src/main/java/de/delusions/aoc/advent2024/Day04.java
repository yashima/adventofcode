package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day04 extends Day<Integer> {


    public Day04() {
        super("Ceres Search", 18, 9, 2454, 0);
    }

    char[] SEARCH = {'X', 'M', 'A', 'S'};

    @Override
    public Integer part0(Stream<String> input) {
        Matrix xmas = Matrix.createFromStream(input);
        List<Coordinates> x = xmas.findValues('X', false);
        AtomicInteger sum = new AtomicInteger(0);
        x.stream().forEach(coords -> {
            for (Direction dir : Direction.values()) {
                Coordinates current = coords;//step0
                boolean foundXmas = true;
                for (int step = 1; step < SEARCH.length; step++) {
                    current = current.moveTo(dir, 1);
                    if (!xmas.isInTheMatrix(current) || xmas.getValue(current) != SEARCH[step]) {
                        foundXmas = false;
                        break;
                    }
                }
                if (foundXmas) {
                    sum.incrementAndGet();
                }
            }
        });
        return sum.get();
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix xmas = Matrix.createFromStream(input);
        List<Coordinates> a = xmas.findValues('A', false);
        AtomicInteger sum = new AtomicInteger(0);
        a.stream().forEach(coords -> {
            Coordinates ne = coords.moveTo(Direction.northeast);
            Coordinates sw = coords.moveTo(Direction.southwest);
            Coordinates se = coords.moveTo(Direction.southeast);
            Coordinates nw = coords.moveTo(Direction.northwest);
            if (xmas.isInTheMatrix(ne, sw, se, nw)) {
                if (xmas.getValue(ne) + xmas.getValue(sw) == 'M' + 'S' && xmas.getValue(se) + xmas.getValue(nw) == 'M' + 'S') {
                    sum.incrementAndGet();
                }
            }
        });
        return sum.get();
    }
}
