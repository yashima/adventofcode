package de.delusions.aoc.days;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day12 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day12.class);

    public Day12() {
        super("Garden Groups", 1930, 1206, 1375574, 830566);
    }

    record Plot(Coordinates coords, List<Coordinates> neighbors) {

        boolean hasEdge(Direction d) {
            return neighbors().stream().noneMatch(n -> n.equals(coords.moveTo(d)));
        }

    }

    record Edge(List<Integer> members) {

        int distinct() {
            int count = 1;
            for (int i = 1; i < members.size(); i++) {
                if (members.get(i) > members.get(i - 1) + 1) {
                    count++;
                }
            }
            return count;
        }
    }

    record Region(List<Plot> plots) {

        int countEdges() {
            return Arrays.stream(Direction.cardinals()).map(d -> createEdge(getByEdge(d), d)).flatMap(Collection::stream).mapToInt(Edge::distinct).sum();
        }

        List<Coordinates> getByEdge(Direction d) {
            return plots.stream().filter(p -> p.hasEdge(d)).map(Plot::coords).toList();
        }

        int fencePrice() {
            return plots.size() * plots.stream().mapToInt(p -> 4 - p.neighbors().size()).sum();
        }

        int fencePriceDiscount() {
            return countEdges() * plots.size();
        }

        static Region create(Collection<Plot> plots) {
            return new Region(new ArrayList<>(plots));
        }

    }


    @Override
    public Integer part0(Stream<String> input) {
        List<Region> regions = allocateRegions(input);
        return regions.stream().mapToInt(Region::fencePrice).sum();
    }

    @Override
    public Integer part1(Stream<String> input) {
        List<Region> regions = allocateRegions(input);
        return regions.stream().mapToInt(Region::fencePriceDiscount).sum();
    }

    /**
     * Allocates regions by identifying connected plots in a garden representation.
     * Each region consists of plots that are connected based on common neighbor logic.
     *
     * @param input A stream of strings representing the garden matrix layout.
     *              Each string represents a row in the garden, and each character represents a plot type.
     * @return A list of {@link Region} objects, where each region contains a group of connected plots.
     */
    static List<Region> allocateRegions(Stream<String> input) {
        List<Region> regions = new ArrayList<>();
        Matrix garden = Matrix.createFromStream(input);
        Set<Coordinates> allocated = new HashSet<>();
        Stack<Plot> candidates = new Stack<>();
        garden.coordinatesStream().forEach(c ->
        {
            if (!allocated.contains(c)) {
                Set<Plot> forCurrentRegion = new HashSet<>(); //refresh the list

                //this is always starting a new region, stack is empty at this point
                candidates.add(new Plot(c, findNeighbors(c, garden))); //add to candidates

                //move through neighbors until no more neighbors
                while (!candidates.isEmpty()) {
                    Plot plot = candidates.pop();
                    forCurrentRegion.add(plot);
                    allocated.add(plot.coords());
                    List<Plot> neighborPlots = plot.neighbors().stream().map(n -> new Plot(n, findNeighbors(n, garden))).toList();
                    for (Plot neighbor : neighborPlots) {
                        if (!allocated.contains(neighbor.coords())) {
                            candidates.add(neighbor);
                        }
                    }
                }

                //finish creating the region
                regions.add(Region.create(forCurrentRegion));
                allocated.addAll(regions.getLast().plots.stream().map(Plot::coords).collect(Collectors.toSet()));
            }
        });
        return regions;
    }


    /**
     * Finds neighbors of the given coordinates in the garden matrix that have the same type (value)
     * as the given coordinate. Neighbors are determined using cardinal directions
     * (north, south, east, west).
     *
     * @param c      The coordinate whose neighbors are to be found.
     * @param garden The matrix representation of the garden.
     * @return A list of neighbor coordinates that are within the matrix
     * and share the same type as the given coordinate.
     */
    static List<Coordinates> findNeighbors(Coordinates c, Matrix garden) {
        List<Coordinates> neighbors = new ArrayList<>();
        int type = garden.getValue(c);
        for (Direction d : Direction.cardinals()) {
            Coordinates neighborlyCandidate = c.moveTo(d);
            neighborlyCandidate.setFacing(d);
            if (garden.isInTheMatrix(neighborlyCandidate) && garden.getValue(neighborlyCandidate) == type) {
                neighbors.add(neighborlyCandidate);
            }
        }
        return neighbors;
    }

    /**
     * Creates edges for the given list of members based on their coordinates and direction.
     * The method groups members by their positions along either the X or Y axis, depending
     * on the specified direction (north/south or east/west). Each group is represented as
     * an {@link Edge} containing sorted members.
     *
     * @param members A list of {@link Coordinates} representing positions in the matrix.
     * @param d       The {@link Direction} indicating whether to group by X or Y axis.
     *                - For {@link Direction#north} or {@link Direction#south}, it groups by X.
     *                - For {@link Direction#east} or {@link Direction#west}, it groups by Y.
     * @return A collection of {@link Edge} objects, each containing a list of sorted members.
     */
    static Collection<Edge> createEdge(List<Coordinates> members, Direction d) {
        Map<Integer, Edge> edgeMap = new HashMap<>();
        for (Coordinates member : members) {
            if (List.of(Direction.north, Direction.south).contains(d)) {
                edgeMap.computeIfAbsent(member.x, n -> new Edge(new ArrayList<>())).members().add(member.y);
            } else {
                edgeMap.computeIfAbsent(member.y, n -> new Edge(new ArrayList<>())).members().add(member.x);
            }
        }
        edgeMap.values().forEach(e -> e.members().sort(Integer::compareTo));
        return edgeMap.values();
    }
}
