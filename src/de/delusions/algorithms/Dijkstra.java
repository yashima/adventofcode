package de.delusions.algorithms;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

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
}
