package de.delusions.aoc.advent2024;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 extends Day<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(Day18.class);

    public Day18() {

        super("", 22, 0, 382, 0);
    }

    static final int CORRUPT = 1;
    static final int EMPTY = 0;
    static final int PATH = 2;

    Pattern REGEX = Pattern.compile("(\\d+),(\\d+)");

    @Override
    public Integer part0(Stream<String> input) {
        int size = isTestMode() ? 7 : 71;
        int simulationLimit = isTestMode() ? 12 : 1024;

        Matrix memorySpace = new Matrix(size, size,0,0);
        memorySpace.setPrintMap(Map.of(0,".",1,"#",2,"O"));
        Path start = new Path(0,0,0);
        List<Coordinates> fallingBytes = input.map( l -> REGEX.matcher(l)).filter(m -> m.matches()).map(m -> new Coordinates(Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2)))).collect(Collectors.toList());
        fallingBytes.stream().limit(simulationLimit).forEach(c -> memorySpace.setValue(c,CORRUPT));
        Dijkstra<Path,Matrix> dijkstra = new Dijkstra<>(start);
        //140 too low

        Path bestPath = dijkstra.findBestPath(memorySpace);
        bestPath.collectPath().forEach(c -> memorySpace.setValue(c,PATH));
        System.out.println(memorySpace.toString());
        return bestPath.steps ;
    }

    class Path implements Pathable<Path,Integer,Matrix> {
    
        int steps = 0;
        Coordinates coords;

        public Path(int x, int y,int steps) {
            coords = new Coordinates(x,y);
            this.steps = steps;
        }

        public Path(Coordinates coords,int steps) {
            this.coords = coords;
            this.steps = steps;
        }


        @Override
        public List<Path> getNeighbors(Matrix theMap) {
            List<Path> result = Arrays.stream(Direction.cardinals())
                    .map(d -> this.coords.moveTo(d).setFacing(d).setPrevious(this.coords))
                    .filter(c -> theMap.isInTheMatrix(c) && theMap.getValue(c) != CORRUPT)
                    .map(c -> new Path(c, this.steps + 1))
                    .toList();
            return result;
        }

        @Override
        public Integer distance() {
            return steps;
        }


        @Override
        public boolean goal(Matrix theMap) {
            return this.coords.x==theMap.getXLength()-1 && this.coords.y==theMap.getYLength()-1;
        }

        @Override
        public Path previous() {
            throw new UnsupportedOperationException("not needed");
        }

        @Override
        public int compareTo(Path o) {
            return Integer.compare(this.steps, o.steps);
        }

        List<Coordinates> collectPath(){
            List<Coordinates> result = new ArrayList<>();
            Coordinates current = this.coords;
            while(current!=null){
                result.add(current);
                current = current.getPrevious();
            }
            return result;
        }


        @Override
        public int hashCode() {
            return coords.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Path that = (Path) obj;
            return this.coords.equals(that.coords);
        }

    }

    @Override
    public Integer part1(Stream<String> input) {
        return 0;
    }


}
