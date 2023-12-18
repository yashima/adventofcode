package de.delusions.algorithms;

import java.util.List;

public interface Pathable<P, N extends Number, MAP> extends Comparable<P> {

    List<P> getNeighbors( MAP theMap );

    N distance();

    boolean goal( MAP theMap );

    P previous();
}
