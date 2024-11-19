package de.delusions.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Path<T extends PathNode<T>> {
    Path<T> parent;

    T node;

    int totalCost;

    int heuristic;

    int totalHeuristic;

    public Path( Path<T> parent, T node, int cost, int heuristic ) {
        this.parent = parent;
        this.node = node;
        this.totalCost = ( parent == null ? 0 : parent.totalCost ) + cost;
        this.heuristic = heuristic;
        this.totalHeuristic = ( parent == null ? 0 : parent.totalHeuristic ) + this.heuristic;
    }

    public boolean contains( PathNode<T> node ) {
        return this.node.equals( node ) || ( parent != null && parent.contains( node ) );
    }

    public int getF() { //f = g + h, hier ist die heuristic aber invertiert
        return totalCost - totalHeuristic;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public T getNode() {
        return node;
    }

    public int length() { //minus parent
        return parent == null ? 1 : parent.length() + 1;
    }

    public int getTotalHeuristic() {
        return totalHeuristic;
    }

    public Set<Integer> getNodeIdsWithoutStart() {
        Set<Integer> result = parent == null ? new HashSet<>() : parent.getNodeIdsWithoutStart();
        if ( parent != null ) {
            result.add( node.getId() );
        }
        return result;
    }

    @Override
    public String toString() {
        return ( parent == null ? "" : parent + ", " ) + totalCost + ": " + node + " h=" + heuristic;
    }

    public List<Path<T>> search( Function<Path<T>, List<Path<T>>> childPaths, Function<Path<T>, Boolean> finished ) {
        Map<T, Path<T>> openList = new HashMap<>();
        Map<T, Path<T>> closedList = new HashMap<>();

        openList.put( this.node, this );

        List<Path<T>> candidates = new ArrayList<>();
        while ( !openList.isEmpty() ) {

            Path<T> path = openList.values().stream().min( Comparator.comparing( Path::getF ) ).get();
            openList.remove( path.node );

            List<Path<T>> children = childPaths.apply( path );

            children.forEach( child -> {
                if ( finished.apply( child ) ) {
                    candidates.add( child );
                    return;
                }
                if ( ( !openList.containsKey( child.node ) || openList.get( child.node ).getF() > child.getF() ) &&
                    ( !closedList.containsKey( child.node ) || closedList.get( child.node ).getF() > child.getF() ) ) {
                    openList.put( child.node, child );
                }
            } );
            closedList.put( path.node, path );
        }
        return candidates;
    }

}
