package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day08 extends Day<Integer> {

    public Day08() {
        super("Resonant Collinearity", 14, 34, 228, 0);
    }

    static final char ANTINODE = '#';

    Matrix roofs;

    @Override
    public Integer part0(Stream<String> input) {
        return solveIt(input, this::mirroredAntinodes);
    }


    @Override
    public Integer part1(Stream<String> input) {
        return solveIt(input, this::endlessAntinodes);
    }

    private int solveIt(Stream<String> input, BiFunction<Coordinates, Coordinates, List<Coordinates>> calculateFunc) {
        roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findAllAntinodes(antennas, antinodes, calculateFunc);
        return (int) antinodes.stream().filter(roofs::isInTheMatrix).count();
    }

    private void findAllAntinodes(Map<Integer, List<Coordinates>> antennas, Set<Coordinates> antinodes, BiFunction<Coordinates, Coordinates, List<Coordinates>> calculateFunc) {
        for (List<Coordinates> antennaType : antennas.values()) {
            IntStream.range(0, antennaType.size()).forEach(i ->
                    IntStream.range(i + 1, antennaType.size()).forEach(j -> antinodes.addAll(calculateFunc.apply(antennaType.get(i), antennaType.get(j))))
            );
        }
    }

    private List<Coordinates> mirroredAntinodes(Coordinates antennaI, Coordinates antennaJ) {
        return List.of(antennaI.vector(antennaJ, 2, ANTINODE), antennaJ.vector(antennaI, 2, ANTINODE));
    }

    private List<Coordinates> endlessAntinodes(Coordinates antennaI, Coordinates antennaJ) {
        List<Coordinates> result = new ArrayList<>();
        for (int factor = 1; factor <= 50; factor++) {
            result.add(antennaI.vector(antennaJ,factor,ANTINODE));
            result.add(antennaJ.vector(antennaI,factor,ANTINODE));
        }
        return result;
    }

}
