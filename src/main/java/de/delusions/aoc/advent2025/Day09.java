package de.delusions.aoc.advent2025;

import de.delusions.util.*;
import de.delusions.util.dimensions.TupelLong;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Part 1: find the biggest rectangle area that can be made by a pair of coordinates
 * Part 2: Oops coordinates are a sequence that describe an orthogonal polygon, find the biggest empty rectangle inside
 * Solution: ray-casting on all the corners of the rectangle candidate + check that none of the rectangle edges cross any
 * of the boundary edges while excluding corners. This was something I was trying very early on but coordinate wrangling is tricky.
 * Lots of unit testing needed.
 */
@Slf4j
public class Day09 extends Day<Long> {

    @Setter
    private List<TupelLong> coordinates;

    public Day09() {
        super("Movie Theater", 50L, 24L, 4739623064L, 1654141440L);
    }

    enum Orientation {
        HORIZONTAL, VERTICAL
    }

    /**
     * A pair of coordinates can define either an edge or a rectangle.
     * The record provides helper methods to deal with both cases.
     */
    record Pair(TupelLong a, TupelLong b) {
        static Pair createPair(Long a1, Long a2, Long b1, Long b2) {
            return new Pair(TupelLong.from(a1,a2),TupelLong.from(b1,b2));
        }

        Long rectangle() {
            long deltaX = getDelta(0) + 1;
            long deltaY = getDelta(1) + 1;
            return deltaX * deltaY;
        }

        Long sizeOfBorder(){
            long deltaX = getDelta(0) + 1;
            long deltaY = getDelta(1) + 1;
            return deltaX + deltaY;
        }

        List<Pair> getRectangleEdges() {
            Pair inverted = invertCorners();
            return List.of(new Pair(a(), inverted.a), new Pair(a(), inverted.b), new Pair(b(), inverted.b),  new Pair(b(), inverted.a) );
        }

        Pair invertCorners() {
            return new Pair(TupelLong.from(a.x(), b.y()), TupelLong.from(b.x(), a.y()));
        }

        long getDelta(int position) {
            return Math.abs(a.get(position) - b.get(position));
        }

        Orientation orientation() {
            return Objects.equals(a.y(), b.y()) ? Orientation.HORIZONTAL : (Objects.equals(a.x(), b.x()) ? Orientation.VERTICAL : null);
        }

        Interval range() {
            int index = orientation() == Orientation.HORIZONTAL ? 0 : 1;
            return Interval.fromUnordered(a.get(index), b.get(index));
        }

        long constant() {
            return orientation() == Orientation.HORIZONTAL ? a.get(1) : a.get(0);
        }

        /**
         * Tests if any given point is on the edge represented by this pair
         * @param point the point to check
         * @return true if the point is on the edge
         */
        boolean isOnEdge(TupelLong point) {
            if (orientation() == Orientation.HORIZONTAL) {
                return point.y() == constant() && range().contains(point.x());
            }
            return point.x() == constant() && range().contains(point.y());
        }

        /**
         * Tests if an edge intersects with the edge represented by this pair
         * @param edge the pair representing the edge to check
         * @param excludeCorner if true, the vertexes of the edge are excluded from the intersection check
         * @return the intersection point as TupelLong or null if no intersection
         */
        TupelLong intersects(Pair edge,boolean excludeCorner){
            boolean orthogonal = orientation() != edge.orientation();
            boolean inRange = excludeCorner
                    ? range().containsExclusive(edge.constant()) && edge.range().containsExclusive(constant())
                    : range().contains(edge.constant()) && edge.range().contains(constant());
            if( orthogonal && inRange) {
                log.debug("Pair.intersects: Intersecting edge {} with {}", this, edge);
                return orientation() == Orientation.VERTICAL ? TupelLong.from(constant(), edge.constant()) : TupelLong.from(edge.constant(), constant());
            }
            return null;
        }


        /**
         * Checks if any given point is inside the rectangle represented by this pair
         * @param point point to check
         * @return true if the point is inside the rectangle
         */
        boolean isInsideRectangle(TupelLong point) {
            return point.x()< (Math.max(a.x(),b.x())) && point.x()> (Math.min(a.x(),b.x())) && point.y()< (Math.max(a.y(),b.y())) && point.y()> (Math.min(a.y(),b.y()));
        }

        /**
         * Tests if any of a number of points is within the rectangle represented by this pair
         * @param coordinates list of points to check
         * @return the first point that is inside, null if none is
         */
        TupelLong anyVertexIsInside(List<TupelLong> coordinates){
            return coordinates.stream().filter(this::isInsideRectangle).findFirst().orElse(null);
        }

        /**
         * Calculates edges crossing the edge represented by this pair
         * @param edges the list of edges to check
         * @return a list of crossing points
         */
        List<TupelLong> edgeCrossings(List<Pair> edges){
            return edges.stream()
                    .map(e -> intersects(e,true))
                    .filter(Objects::nonNull)
                    .toList();
        }

        /**
         * Checks if the given edge is completely on the edge represented by this pair
         * @param edge the edge to check
         * @return true if the edge is completely on the edge represented by this pair
         */
        boolean contains(Pair edge){
            return range().contains(edge.range()) && constant() == edge.constant();
        }

    }


    @Override
    public Long part0(Stream<String> input) {
        TupelLong.resetID();
        this.coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        long max = 0L;
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
     * Find the largest rectangle inside the orthogonal polygon with red corners
     */
    @Override
    public Long part1(Stream<String> input) {
        TupelLong.resetID();
        coordinates = new ArrayList<>(input.map(TupelLong::from).toList());
        TreeMap<Long, Pair> candidates = new TreeMap<>();
        List<Pair> edges = createEdges(candidates);
        while (!candidates.isEmpty()) {
            Pair pair = candidates.pollLastEntry().getValue();
            //if any coordinates are within the given rectangle, we can stop processing that one
            if(pair.anyVertexIsInside(coordinates)!=null){
                continue;
            }
            Pair inverted = pair.invertCorners();
            //check that both opposite corners are 'inside'
            if (castRay(edges, inverted.a) && castRay(edges, inverted.b)) {
                //check if any of the rectangle edges intersects with any of the boundary edges (outside of corners):
                List<TupelLong> crossingPoints = pair.getRectangleEdges().stream().flatMap(e -> e.edgeCrossings(edges).stream()).toList();
                if(crossingPoints.isEmpty()) {
                    return pair.rectangle();
                }
            }
        }
        //wut nothing?
        return 0L;
    }

    /**
     * Create the edges and candidate map from the parsed input.
     * @param candidates this is the map that will be filled with candidate rectangles
     * @return a list of edges that define the outer border of the area that has to contain the rectangles as per the puzzle
     */
    List<Pair> createEdges(TreeMap<Long, Pair> candidates) {
        List<Pair> edges = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            //each edge is formed by a subsequent pair of coordinates, % makes sure that the final edge closes the area
            edges.add(new Pair(coordinates.get(i), coordinates.get((i + 1) % coordinates.size())));
            for (int j = i + 1; j < coordinates.size(); j++) {
                Pair pair = new Pair(coordinates.get(i), coordinates.get(j));
                candidates.put(pair.rectangle(), pair);
            }
        }
        return edges;
    }

    /**
     * Very basic implementation of ray casting specifically adapted for this orthogonal polygon.
     * Also does a quick pre-check if the point is on an edge.
     * @param edges the ordered edges of the polygon
     * @param point the point to check
     * @return true if the point is inside, false if outside
     */
    boolean castRay(List<Pair> edges, TupelLong point) {
        if(coordinates.contains(point) || edges.stream().anyMatch(e -> e.isOnEdge(point))) {
            //early exit optimization: point is a coordinate or on an edge
            return true;
        }

        List<TupelLong> verticalCrossings =  edges.stream()
                .filter(p1 -> p1.orientation() == Orientation.VERTICAL) //we only look at vertical edges
                .filter(p -> p.range().contains(point.y())) //check if point y coordinate is in the range of the edge
                .filter(p -> p.constant() < point.x()) //just check a single direction
                .map(p -> TupelLong.from(p.constant(),point.y()) )
                .toList();

        long count = verticalCrossings.stream().filter(vC -> coordinates.contains(vC)).count();
        return (verticalCrossings.size()-count/2) % 2 == 1;
    }


}
