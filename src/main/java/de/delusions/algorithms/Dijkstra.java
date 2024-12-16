package de.delusions.algorithms;

import java.util.*;

public class Dijkstra<PATH extends Pathable<PATH, ?, MAP>, MAP> {

    private final PATH start;

    private Set<PATH> visited;

    public Dijkstra(PATH start) {
        this.start = start;
    }

    PATH getStart() {
        return start;
    }

    public Set<PATH> getVisited() {
        return visited;
    }

    public PATH findBestPath(MAP theMap) {
        PriorityQueue<PATH> opens = new PriorityQueue<>();
        visited = new HashSet<>();

        //we don't really need start in visited because it gets added in the loop
        opens.add(start);

        while (!opens.isEmpty()) {
            PATH path = opens.poll();
            if (path.goal(theMap)) { //we're done, the lava has arrived
                return path;
            }
            //ah well, this one has been visited
            visited.add(path);

            path.getNeighbors(theMap).forEach(n -> {
                if (!visited.contains(n) && !opens.contains(n)) {
                    opens.add(n);
                } else if (opens.contains(n)) {
                    PATH old = opens.stream().filter(n::equals).findFirst().orElse(null);
                    if (old != null && n.compareTo(old) < 1) {
                        opens.remove(old);
                        opens.add(n);
                    }
                }
            });

        }
        return null;
    }

    public List<PATH> findAllReachableEndings(MAP theMap) {
        List<PATH> paths = new ArrayList<>();
        Stack<PATH> opens = new Stack<>();
        visited = new HashSet<>();
        opens.add(start);
        while (!opens.isEmpty()) {
            PATH path = opens.pop();
            visited.add(path);
            if (path.goal(theMap)) { //found one
                paths.add(path);
            }
            path.getNeighbors(theMap).forEach(n -> {
                if (!visited.contains(n) && !opens.contains(n)) {
                    opens.add(n);
                }
            });

        }
        return paths;
    }

    public List<PATH> findAllUniquePaths(MAP theMap) {
        List<PATH> paths = new ArrayList<>();
        Stack<PATH> opens = new Stack<>();
        opens.add(start);
        while (!opens.isEmpty()) {
            PATH path = opens.pop();
            if (path.goal(theMap)) { //found one
                paths.add(path);
            }
            opens.addAll(path.getNeighbors(theMap));
        }
        return paths;
    }

    public List<PATH> findAllBestPaths(MAP theMap) {
        PriorityQueue<PATH> opens = new PriorityQueue<>();
        visited = new HashSet<>();
        List<PATH> bestPaths = new ArrayList<>();
        //we don't really need start in visited because it gets added in the loop
        opens.add(start);

        while (!opens.isEmpty()) {
            PATH path = opens.poll();
            if (path.goal(theMap)) { //we're done, we're at the goal
                bestPaths.add(path);
                continue;
            }
            visited.add(path);
            //ah well, this one has been visited


            path.getNeighbors(theMap).forEach(n -> {
                if (!opens.contains(n) && !visited.contains(n)) {
                    opens.add(n);
                } else if (!visited.contains(n)) {
                    //keep searching
                    opens.add(n);
                }
            });

        }
        return bestPaths;
    }
}
