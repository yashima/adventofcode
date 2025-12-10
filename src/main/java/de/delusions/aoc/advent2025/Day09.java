package de.delusions.aoc.advent2025;

import de.delusions.util.*;
import de.delusions.util.dimensions.TupelLong;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Part 1: find biggest reactangle area that can be made by a pair of coordinates
 * Part 2: Oops coordinates are a sequence that describe an orthogonal polygon, find the biggest empty rectangle inside
 */
@Slf4j
public class Day09 extends Day<Long> {
    private List<TupelLong> coordinates;

    public Day09() {
        super("Movie Theater", 50L, 24L, 4739623064L, 0L);
    }

    enum Orientation {
        HORIZONTAL, VERTICAL
    }

    record Pair(TupelLong a, TupelLong b) {
        Long rectangle() {
            long deltaX = getDelta(0) + 1;
            long deltaY = getDelta(1) + 1;
            return deltaX * deltaY;
        }

        List<Pair> getRectangleEdges() {
            Pair inverted = invertCorners();
            return List.of(new Pair(a, inverted.a), new Pair(b, inverted.b), new Pair(a, inverted.b), new Pair(b, inverted.a) );
        }

        Pair invertCorners() {
            return new Pair(TupelLong.from(a.get(0), b.get(1)), TupelLong.from(b.get(0), a.get(1)));
        }

        long getDelta(int position) {
            return Math.abs(a.get(position) - b.get(position));
        }

        Orientation orientation() {
            return a.get(0) == b.get(0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        }

        Interval range() {
            return orientation() == Orientation.HORIZONTAL ? new Interval(a.get(1), b.get(1)) : new Interval(a.get(0), b.get(0));
        }

        long constant() {
            return orientation() == Orientation.HORIZONTAL ? a.get(1) : a.get(0);
        }

        boolean isOnEdge(TupelLong point) {
            if (orientation() == Orientation.HORIZONTAL) {
                return point.get(0) == constant() && range().contains(point.get(1));
            }
            return point.get(1) == constant() && range().contains(point.get(0));
        }

        long edgeLength() {
            return Math.abs(orientation() == Orientation.HORIZONTAL ? a.get(1) - b.get(1) : a.get(0) - b.get(0));
        }

        boolean intersects(Pair edge){
            return orientation() != edge.orientation() && range().contains(edge.constant());
        }
    }


    @Override
    public Long part0(Stream<String> input) {
        TupelLong.resetID();
        this.coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        Long max = 0L;
        Pair best = null;
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i + 1; j < coordinates.size(); j++) {
                Pair pair = new Pair(coordinates.get(i), coordinates.get(j));
                if (pair.rectangle() > max) {
                    max = pair.rectangle();
                }
            }
        }
        return max;
    }


    /**
     * Find largest rectangle inside orthogonal polygon with red corners
     */
    @Override
    public Long part1(Stream<String> input) {
        TupelLong.resetID();
        TreeMap<Long, Pair> candidates = new TreeMap<>();
        List<Pair> edges = new ArrayList<>();
        coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        log.debug("Coords: {}", coordinates.size());
        for (int i = 0; i < coordinates.size(); i++) {
            edges.add(new Pair(coordinates.get(i), coordinates.get((i + 1) % coordinates.size())));
            for (int j = i + 1; j < coordinates.size(); j++) {
                Pair pair = new Pair(coordinates.get(i), coordinates.get(j));
                candidates.put(pair.rectangle(), pair);
            }
        }
        while (!candidates.isEmpty()) {
            Pair pair = candidates.pollLastEntry().getValue(); //get best candidate
            Pair inverted = pair.invertCorners();
            if (isInside(edges, inverted.a) && isInside(edges, inverted.b)) {
                //we found the best rectangle that has all edges inside
                //too low: 22170996
                if(pair.getRectangleEdges().stream().noneMatch( re -> edgeCrossings(edges,re)))
                    return pair.rectangle();
            }
        }
        //wut nothing?
        return 0L;
    }

    boolean edgeCrossings(List<Pair> pairs, Pair edge){
        return pairs.stream().anyMatch(p -> edge.intersects(p));
    }

    boolean isInside(List<Pair> edges, TupelLong point) {
        Optional onEdge = edges.stream().filter(p -> p.isOnEdge(point)).findFirst();
        if (onEdge.isPresent()) {
            return true;
        }

        List<TupelLong> verticalCrossings =  edges.stream()
                .filter(p -> p.orientation() == Orientation.VERTICAL) //we only look at vertical edges
                .filter(p -> p.range().contains(point.get(1))) //check if point y coordinate is in the range of the edge
                .filter(p -> p.constant() < point.get(0)) //just check a single direction
                .map(p -> TupelLong.from(p.constant(),point.get(1)) )
                .toList();
        return verticalCrossings.size() %2 ==1;
    }


}
