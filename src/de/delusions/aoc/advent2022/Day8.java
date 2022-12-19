package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Coordinates;
import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
public class Day8
    extends Day<Integer> {

    Day8() {
        super( 8, "Treetop Tree House" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Set<Coordinates> visibleTrees = new HashSet<>();

        Matrix treePatch = new Matrix( input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new ) );

        for ( int row = 0; row < treePatch.getMatrix().length; row++ ) {
            int westSize = -1, eastSize = -1, northSize = -1, southSize = -1;
            int rowLength = treePatch.getMatrix()[row].length;
            for ( int col = 0; col < rowLength; col++ ) {
                westSize = getMaxSize( visibleTrees, treePatch, row, col, westSize );
                eastSize = getMaxSize( visibleTrees, treePatch, row, rowLength - col - 1, eastSize );
                northSize = getMaxSize( visibleTrees, treePatch, col, row, northSize );
                southSize = getMaxSize( visibleTrees, treePatch, rowLength - col - 1, row, southSize );
            }
        }
        return visibleTrees.size();
    }

    @Override
    public Integer part2( Stream<String> input ) {
        List<Integer> scenery = new ArrayList<>();
        Matrix treePatch = new Matrix( input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new ) );

        for ( int row = 1; row < treePatch.getMatrix().length - 1; row++ ) {
            int rowLength = treePatch.getMatrix()[row].length;
            for ( int col = 1; col < rowLength - 1; col++ ) {
                scenery.add( scenicScore( treePatch, row, col ) );
            }
        }
        return scenery.stream().max( Integer::compareTo ).get();
    }

    private int getMaxSize( Set<Coordinates> visibleTrees, Matrix treePatch, int row, int col, int maxSizeForRow ) {
        Coordinates tree = new Coordinates( row, col );
        tree.setValue( treePatch.getValue( tree ));
        if ( tree.getValue() > maxSizeForRow ) {
            maxSizeForRow = tree.getValue();
            visibleTrees.add( tree );
        }
        return maxSizeForRow;
    }

    private Integer scenicScore( Matrix treePatch, int row, int col ) {
        int treeSize = treePatch.getValue( new Coordinates( row, col ) );
        boolean west = true, east = true, south = true, north = true;
        int score = 1;
        int radius = 1;
        while ( west || east || south || north && score > 0 ) {

            if ( west && lastTree( treePatch, treeSize, row, col - radius, false ) ) {
                west = false;
                score = score * radius;
            }
            if ( east && lastTree( treePatch, treeSize, row, col + radius, false ) ) {
                east = false;
                score = score * radius;
            }
            if ( south && lastTree( treePatch, treeSize, row + radius, col, true ) ) {
                south = false;
                score = score * radius;
            }
            if ( north && lastTree( treePatch, treeSize, row - radius, col, true ) ) {
                north = false;
                score = score * radius;
            }
            radius++;
        }
        return score;
    }

    private boolean lastTree( Matrix treePatch, int treeSize, int row, int col, boolean checkRow ) {
        return ( checkRow ? ( row == 0 || row == treePatch.getXLength() - 1 ) : ( col == 0 || col == treePatch.getYLength() - 1 ) ) ||
            treePatch.getMatrix()[row][col] >= treeSize;
    }
}
