package de.delusions.aoc.advent2025;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;

import java.util.List;
import java.util.stream.Stream;

/**
 * Day 4: Finding things in a matrix is really easy when you have already created a matrix class in previous years.
 * This was the easiest day so far. Re-use! Lazy programmers write library code :)
 */
public class Day04 extends Day<Integer> {
    public Day04() {
        super("Printing Department", 13,43,1547,8948);
    }

    public static char PAPER = '@';
    public static char NONE = '.';

    /** Checks all the coordinates in the matrix for tiles that have paper and fewer than 4 paper neighbors and returns them as list */
    public static List<Coordinates> findRemovablePaper(Matrix wall) {
        return wall
                .coordinatesStream()
                .filter(coordinates -> wall.getValue(coordinates) == PAPER)
                .filter( coordinates -> wall.findNeighbors(coordinates).stream().filter(n -> wall.getValue(n) == PAPER).count()<4)
                .toList();
    }

    @Override
    public Integer part0(Stream<String> input) {
        return findRemovablePaper(Matrix.createFromStream(input)).size();
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix wall = Matrix.createFromStream(input);
        //find initial set of removable tiles
        List<Coordinates> coordinates = findRemovablePaper(wall);
        int sum =  coordinates.size();
        //repeat until no more removable tiles are found
        while (coordinates.size() > 0) {
            //remove paper
            coordinates.forEach( c -> wall.setValue(c, NONE) );
            //find more removable paper
            coordinates = findRemovablePaper(wall);
            sum += coordinates.size();
        }
        return sum;
    }
}
