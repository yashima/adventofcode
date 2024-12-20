package de.delusions.aoc.advent2024;

import de.delusions.algorithms.Dijkstra;
import de.delusions.algorithms.Pathable;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Day20 extends Day<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(Day20.class);

    public Day20() {
        super("Race Condition", 44L, 0L, 1404L, 0L);
    }


    @Override
    public Long part0(Stream<String> input) {
        Matrix races = Matrix.createFromStream(input);
        races.setEmptyValue('.');
        races.setObstacle('#');
        RaceTrack raceTrack = new RaceTrack(races.findValue('S')).setCheats(races);
        return new Dijkstra<>(raceTrack).findBestPath(races).countValidCheats(races, isTestMode() ? 0 : 100);
    }

    @Override
    public Long part1(Stream<String> input) {
        return 0L;
    }

    Map<Coordinates, AtomicLong> cheatTargetCount = new HashMap<>();

    class RaceTrack implements Pathable<RaceTrack, Long, Matrix> {

        RaceTrack previous;
        Coordinates coords;
        long steps = 0;
        List<Coordinates> cheats;

        RaceTrack(Coordinates coords) {
            this(coords, null);
        }
        RaceTrack(Coordinates coords, RaceTrack previous ) {
            this.coords = coords;
            this.previous = previous;
            this.steps = previous==null ? 1 : previous.steps + 1;
        }

        RaceTrack setCheats(Matrix theMap){
            this.cheats = findCheats(theMap);
            return this;
        }

        @Override
        public List<RaceTrack> getNeighbors(Matrix theMap) {
            return Arrays.stream(Direction.cardinals())
                    .map(d -> coords.moveTo(d,1))
                    .filter(c -> theMap.isEmpty(c) || theMap.getValue(c) == 'E')
                    .map(c -> new RaceTrack(c, this).setCheats(theMap))
                    .toList();
        }

        long countValidCheats(Matrix races, int minAdvantage){
            long validCheats = 0;
            RaceTrack current = this.previous;
            while(current!=null){
                if(current.cheats!=null && current.cheats.contains(this.coords) && this.steps - current.steps -1 >= minAdvantage){
                    validCheats++;
                    races.setValue(current.coords, '0');
                    races.setValue(this.coords, '2');
                }
                current = current.previous;
            }
            return validCheats + (previous==null ? 0 : previous.countValidCheats(races, minAdvantage))  ;
        }

        public List<Coordinates> findCheats(Matrix theMap){
            return Arrays.stream(Direction.cardinals())
                    .filter(d -> theMap.isObstacle(coords.moveTo(d,1)))
                    .map(d -> coords.moveTo(d,2,0))
                    .filter(c -> theMap.isInTheMatrix(c) && ( theMap.isEmpty(c) || theMap.getValue(c) == 'E'))
                    .toList();
        }

        @Override
        public Long distance() {
            return steps;
        }

        @Override
        public boolean goal(Matrix theMap) {
            return theMap.getValue(coords) == 'E';
        }

        @Override
        public RaceTrack previous() {
            return previous;
        }

        @Override
        public int compareTo(RaceTrack o) {
            return Long.compare(this.steps, o.steps);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RaceTrack raceTrack = (RaceTrack) o;
            return coords.equals(raceTrack.coords);
        }

        @Override
        public int hashCode() {
            return coords.hashCode();
        }

    }
}
