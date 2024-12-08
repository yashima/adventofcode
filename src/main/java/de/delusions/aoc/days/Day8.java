package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day8 extends Day<Integer> {

    public Day8() {
        super("Resonant Collinearity", 14, 34, 228, 0);
    }

    static char ANTINODE = '#';

    Matrix roofs;

    @Override
    public Integer part0(Stream<String> input) {
        roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findAllAntinodes(antennas, antinodes, this::mirroredAntinodes);
        return (int) antinodes.stream().filter(roofs::isInTheMatrix).count();
    }


    @Override
    public Integer part1(Stream<String> input) {
        roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findAllAntinodes(antennas, antinodes, this::endlessAntinodes);
        return (int) antinodes.stream().filter(roofs::isInTheMatrix).count();
    }

    private void findAllAntinodes(Map<Integer, List<Coordinates>> antennas, Set<Coordinates> antinodes, BiFunction<Coordinates, Coordinates, List<Coordinates>> calculateFunc) {
        for (List<Coordinates> antennaType : antennas.values()) {
            IntStream.range(0, antennaType.size()).forEach(i ->
                    IntStream.range(i + 1, antennaType.size()).forEach(j -> {
                        antinodes.addAll(calculateFunc.apply(antennaType.get(i), antennaType.get(j)));
                    })
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
