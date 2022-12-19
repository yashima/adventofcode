package de.delusions.aoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Matrix {
    //y = cols , -1 = more west, +1 more east
    //x = rows , -1 = more north, +1 more south
    int[][] matrix; //int rows | cols

    int xOffset;

    int yOffset;

    Map<Integer, String> printMap = Map.of( 0, ".", 1, "#", 2, "o", 3, "S", 4, "B" );

    public Matrix( int[][] initialized ) {
        this( initialized, 0, 0 );
    }

    public Matrix( int[][] initialized, int xOffset, int yOffset ) {
        this.matrix = initialized;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Matrix( int xDim, int yDim, int xOffset, int yOffset ) {
        this( new int[xDim][yDim], xOffset, yOffset );
    }

    public void setValue( Coordinates coordinates ) {
        int x = coordinates.x - xOffset;
        int y = coordinates.y - yOffset;
        matrix[x][y] = coordinates.value;
    }

    public int getValue( Coordinates coordinates ) {
        int y = coordinates.y - yOffset;
        int x = coordinates.x - xOffset;
        return matrix[x][y];
    }

    public void setAllValuesRow( int y, int value ) {
        for ( int i = 0; i < matrix.length; i++ ) {
            matrix[i][y] = value;
        }
    }

    public boolean isEmpty( Coordinates coordinates ) {
        return getValue( coordinates ) == 0;
    }

    public boolean isInTheMatrix( Coordinates coordinates ) {
        try {
            getValue( coordinates );
            return true;
        }
        catch ( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    public Coordinates createCoords( int x, int y ) {
        return new Coordinates( x + xOffset, y + yOffset );
    }

    public int getXLength() {
        return matrix.length;
    }

    public int getYLength() {
        return matrix[0].length;
    }

    public List<Coordinates> findValues( int value, boolean firstOnly ) {
        List<Coordinates> positions = new ArrayList<>();
        for ( int x = 0; x < getXLength(); x++ ) {
            for ( int y = 0; y < getYLength(); y++ ) {
                Coordinates coordinates = createCoords( x, y );
                coordinates.value = value;
                if ( getValue( coordinates ) == value ) {
                    positions.add( coordinates );
                    if ( firstOnly ) {
                        break;
                    }
                }
            }
        }
        return positions;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( int y = 0; y < getYLength(); y++ ) {
            for ( int x = 0; x < getXLength(); x++ ) {
                builder.append( printMap.get( getValue( createCoords( x, y ) ) ) );
            }
            builder.append( "\n" );
        }
        return builder.toString();
    }
}
