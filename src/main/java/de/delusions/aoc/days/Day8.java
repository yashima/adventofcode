package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day8 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day8.class);

    public Day8() {
        super("Resonant Collinearity", 14, 34, 228, 0);
    }

    static char ANTINODE = '#';

    @Override
    public Integer part0(Stream<String> input) {
        Matrix roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findAllAntinodes(antennas, antinodes, Day8::antinodesOnALinePart0);
        antinodes.stream().filter(roofs::isInTheMatrix).forEach(roofs::setValue);
        return (int) antinodes.stream().filter(a -> roofs.isInTheMatrix(a)).count();
    }


    @Override
    public Integer part1(Stream<String> input) {
        Matrix roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findAllAntinodes(antennas, antinodes, Day8::antinodesOnALinePart1);
        antinodes.stream().filter(roofs::isInTheMatrix).forEach(roofs::setValue);
        if(isTestMode()) System.out.println(roofs.toString());
        return (int) antinodes.stream().filter(a -> roofs.isInTheMatrix(a)).count();
    }

    private static void findAllAntinodes(Map<Integer, List<Coordinates>> antennas, Set<Coordinates> antinodes, BiFunction<Coordinates, Coordinates,List<Coordinates>> calculateFunc) {
        for (List<Coordinates> antennaType : antennas.values()) {
            IntStream.range(0, antennaType.size()).forEach(i ->
                    IntStream.range(i + 1, antennaType.size()).forEach(j -> {
                        antinodes.addAll(calculateFunc.apply(antennaType.get(i), antennaType.get(j)));
                    })
            );
        }
    }

    private static  List<Coordinates> antinodesOnALinePart0(Coordinates antennaI, Coordinates antennaJ) {
        List<Coordinates> result = new ArrayList<>();
        for (int factor = 1; factor <= 1; factor++) {
            result.add(antennaI.vector(antennaJ,factor*2,ANTINODE));
            result.add(antennaJ.vector(antennaI,factor*2,ANTINODE));
        }
        return result;
    }

    private static List<Coordinates> antinodesOnALinePart1(Coordinates antennaI, Coordinates antennaJ) {
        List<Coordinates> result = new ArrayList<>();
        for (int factor = 2; factor <= 50; factor++) {
            result.add(antennaI.vector(antennaJ,factor,ANTINODE));
            result.add(antennaJ.vector(antennaI,factor,ANTINODE));
        }
        return result;
    }

}
