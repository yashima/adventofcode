package de.delusions.aoc.days;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day10 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day10.class);


    public Day10() {
        super("Hoof It", 36, 81,733, 0);
    }


    @Override
    public Integer part0(Stream<String> input) {
        Matrix map = Matrix.createFromStream(input);
        List<Coordinates> trailheads = map.findValues('0', false);
        return trailheads.stream().map( c -> new Dijkstra<>(new Path(c,0)).findAllReachableEndings(map)).mapToInt(List::size).sum();
    }

    @Override
    public Integer part1(Stream<String> input) {
        Matrix map = Matrix.createFromStream(input);
        List<Coordinates> trailheads = map.findValues('0', false);
        return trailheads.stream().map( c -> findAllPaths(new Path(c,0),map)).mapToInt(List::size).sum();
    }

    class Path extends Coordinates implements Pathable<Path,Integer,Matrix> {

        public Path(Coordinates coords,int elevation) {
            super(coords.x, coords.y);
            this.elevation = elevation;
        }

        int elevation = 0;
        Path previous;

        @Override
        public List<Path> getNeighbors(Matrix theMap) {
            Coordinates north = this.moveTo(Direction.north);
            Coordinates south = this.moveTo(Direction.south);
            Coordinates east = this.moveTo(Direction.east);
            Coordinates west = this.moveTo(Direction.west);
            List<Path> neighbors = new ArrayList<>();
            for( Coordinates c :  List.of(north, south, east, west)){
                if( theMap.isInTheMatrix(c) && theMap.getValue(c)-48 == 1 + elevation){
                    Path n = new Path(c,elevation+1);
                    n.previous = this;
                    neighbors.add(n);
                }
            }
            return neighbors;
        }

        @Override
        public Integer distance() {
            return 0;//TODO
        }

        @Override
        public boolean goal(Matrix theMap) {
            return elevation == 9;
        }

        @Override
        public Path previous() {
            return previous;
        }
        @Override
        public int compareTo(Path o) {
            return 0; //TODO
        }
    }


    public List<Path> findAllPaths(Path start, Matrix theMap){
        List<Path> paths = new ArrayList<>();
        PriorityQueue<Path> opens = new PriorityQueue<>();
        opens.add(start);
        while(!opens.isEmpty()){
            Path path = opens.poll();
            if (path.goal(theMap)) { //found one
                paths.add(path);
            }
            path.getNeighbors(theMap).forEach(opens::add);
        };
        return paths;
    }
}
