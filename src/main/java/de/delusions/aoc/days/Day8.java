package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day8 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day8.class);

    public Day8(){
        super( "Resonant Collinearity",14,34,228,0);
    }

static char ANTINODE ='#';

    @Override
    public Integer part0(Stream<String> input) {
        Matrix roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findMirrored(antennas, antinodes);
        return (int) antinodes.stream().filter(a -> roofs.isInTheMatrix(a)).count();
    }


    @Override
    public Integer part1(Stream<String> input) {
        Matrix roofs = Matrix.createFromStream(input);
        Map<Integer, List<Coordinates>> antennas = roofs.findNotValues('.').stream().collect(Collectors.groupingBy(Coordinates::getValue));
        Set<Coordinates> antinodes = new HashSet<>();
        findMirrored(antennas, antinodes);
        findAligned(antennas,antinodes);
        return (int) antinodes.stream().filter(a -> roofs.isInTheMatrix(a)).count();
    }

    private static void findMirrored(Map<Integer, List<Coordinates>> antennas, Set<Coordinates> antinodes) {
        for(List<Coordinates> antennaType : antennas.values()){
            IntStream.range(0, antennaType.size()).forEach(i ->
                    IntStream.range(i + 1, antennaType.size()).forEach(j -> {
                        Coordinates antennaI = antennaType.get(i);
                        Coordinates antennaJ = antennaType.get(j);
                        antinodes.add(antennaI.mirror(antennaJ));
                        antinodes.add(antennaJ.mirror(antennaI));
                    })
            );
        }
    }

    private void findAligned(Map<Integer, List<Coordinates>> antennas, Set<Coordinates> antinodes) {
        for(List<Coordinates> antennaType : antennas.values()){
            IntStream.range(0, antennaType.size()).forEach(i ->
                    IntStream.range(i + 1, antennaType.size()).forEach(j -> {
                        Coordinates antennaI = antennaType.get(i);
                        Direction dir = antennaI.lookingTowards(antennaType.get(j));
                        if(dir!=null){
                            LOG.warn("Found aligned antennas {} {}",antennaI,dir);
                        }
                    })
            );
        }
    }
}
