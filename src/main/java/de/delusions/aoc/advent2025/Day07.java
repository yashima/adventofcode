package de.delusions.aoc.advent2025;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Part 1: In the matrix tachyon beams fall from top and are multiplied by ^ splitters. How many splitters get hit by the beams?
 * Part 2: Calculate the strength of the beams (many worlds) at the bottom (beams that merge with other beams add their strength)
 */
@Slf4j
public class Day07 extends Day<Long> {
    public Day07() {
        super("tag", 21L, 40L, 1660L, 0L);
    }

    static char START = 'S';
    static char EMPTY = '.';
    static char SPLIT = '^';
    static char BEAM = '|';

    Matrix tachyons;
    Map<Coordinates, BigInteger> beamStrengths;

    @Override
    public Long part0(Stream<String> input) {
        tachyons = Matrix.createFromStream(input);
        AtomicLong splitCount = new AtomicLong(0);
        tachyons.coordinatesStream().forEach(c -> {
            char value = (char) tachyons.getValue(c);
            if (List.of(START, BEAM).contains(value)) { //process below
                Coordinates below = c.moveTo(Direction.south);
                if (!tachyons.isInTheMatrix(below)) {
                    return; //nothing below, we're done
                }
                char belowValue = (char) tachyons.getValue(below);
                if (EMPTY == belowValue) {
                    tachyons.setValue(below, BEAM);
                } else if (SPLIT == belowValue) {
                    splitCount.incrementAndGet();
                    addBeam(below, Direction.east);
                    addBeam(below, Direction.west);
                }
            }
        });
        return splitCount.get();
    }

    @Override
    public Long part1(Stream<String> input) {
        //prep
        tachyons = Matrix.createFromStream(input);
        beamStrengths = new HashMap<>();
        beamStrengths.put(tachyons.findValue(START), BigInteger.ONE); //We start with strength 1

        tachyons.coordinatesStream().forEach(this::processBelow);
        return summarizeResult(beamStrengths,tachyons.getXLength()-1);
    }

    /** Process whatever is below the given position */
    void processBelow(Coordinates c) {
        if (List.of(START, BEAM).contains((char) tachyons.getValue(c))) { //process below
            BigInteger currentBeamStrength = beamStrengths.get(c);
            Coordinates below = c.moveTo(Direction.south);
            if (!tachyons.isInTheMatrix(below)) {
                return;
            }
            char belowValue = (char) tachyons.getValue(below);
            if (EMPTY == belowValue) {
                tachyons.setValue(below, BEAM);
                beamStrengths.put(below, currentBeamStrength); //previously empty space gets current strength
            } else if (SPLIT == belowValue) {
                addBeamStrength(below,Direction.east, currentBeamStrength);
                addBeamStrength(below,Direction.west, currentBeamStrength);
            } else {
                beamStrengths.put(below, currentBeamStrength.add(beamStrengths.get(below)));
            }
        }
    }

    static long summarizeResult(Map<Coordinates, BigInteger> beamStrengths, int rowIdx) {
        BigInteger result = beamStrengths.keySet().stream()
                .filter(c -> c.getX()==rowIdx)
                //.peek(c->log.info("{} = {}",c,beamStrengths.get(c).longValue()))
                .map( c -> beamStrengths.get(c) )
                .reduce(BigInteger.ZERO,(x,y)->x.add(y));
        System.out.println(result);
        return result.longValue();
    }


    void addBeamStrength(Coordinates below, Direction direction,  BigInteger currentStrength) {
        Coordinates splitBeamCoords = below.moveTo(direction);
        BigInteger splitBeamValue = beamStrengths.getOrDefault(splitBeamCoords,BigInteger.ZERO); //currentBeamValue
        if (tachyons.isInTheMatrix(splitBeamCoords)) { //no splits next to each other in my input it seems on visual check
            tachyons.setValue(splitBeamCoords, BEAM);  //modify matrix
            beamStrengths.put(splitBeamCoords, splitBeamValue.add(currentStrength)); //modify beam strengths
        }
    }

    /** Check if the position is part of the matrix and empty set value to BEAM */
    void addBeam(Coordinates current, Direction direction) {
        Coordinates nextBeamCoords = current.moveTo(direction);
        if (tachyons.isInTheMatrix(nextBeamCoords) && tachyons.getValue(nextBeamCoords) == EMPTY) {
            tachyons.setValue(nextBeamCoords, BEAM);
        }
    }

}
