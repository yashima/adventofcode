package de.delusions.aoc;

import java.util.List;

public interface PathNode<T> {
    int getValue();

    int getId();

    List<T> getChildren();
}
