package de.delusions.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Matrix {
    //y = cols , -1 = more west, +1 more east
    //x = rows , -1 = more north, +1 more south
    int[][] matrix; //int rows | cols

    int xOffset;

    int yOffset;

    Map<Integer, String> printMap = new HashMap<>();
    //Map.of( 0, ".", 1, "#", 2, "o", 3, "S", 4, "B" );

    public static Matrix createFromString( String input, String divider ) {
        return new Matrix( Arrays.stream( input.split( divider ) ).map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
    }

    public static Matrix createFromStream( Stream<String> input ) {
        return new Matrix( input.map( line -> line.chars().toArray() ).toArray( int[][]::new ) );
    }

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

    public void setPrintMap( Map<Integer, String> printMap ) {
        this.printMap = printMap;
    }

    public int cleanup() {
        AtomicInteger max = new AtomicInteger( 0 );
        if ( Arrays.stream( matrix ).map( row -> row.length ).peek( l -> max.set( Math.max( max.get(), l ) ) ).distinct().count() > 1 ) {
            for ( int x = 0; x < getXLength(); x++ ) {
                int[] row = getRow( x );
                if ( row.length < max.get() ) {
                    matrix[x] = new int[max.get()];
                    System.arraycopy( row, 0, matrix[x], 0, row.length );
                }
            }
        }
        return max.get();
    }

    public String rowToString( int idx ) {
        return toCharString( getRow( idx ) );
    }

    public int rowToBinary( int idx, Map<Character, Integer> mapping ) {
        String rowToString = rowToString( idx );
        if ( rowToString == null ) {
            return -1;
        }
        return Integer.parseUnsignedInt( rowToString.chars().mapToObj( c -> mapping.get( (char) c ) + "" ).collect( Collectors.joining() ), 2 );
    }

    public IntStream rowAsStream( int x ) {
        return Arrays.stream( getRow( x ) );
    }

    public void setValue( Coordinates coordinates ) {
        setValue( coordinates, coordinates.getValue() );
    }

    public void setValue( Coordinates coordinates, int value ) {
        int x = coordinates.x - xOffset;
        int y = coordinates.y - yOffset;
        matrix[x][y] = value;
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

    public void setAllValues( int value ) {
        for ( int[] ints : matrix ) {
            Arrays.fill( ints, value );
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

    public static String toCharString( int[] array ) {
        if ( array == null ) {
            return null;
        }
        return Arrays.stream( array ).mapToObj( c -> (char) c + "" ).collect( Collectors.joining() );
    }

    public int[] getRow( int x ) {
        if ( x < 0 || x > matrix.length - 1 ) {
            return null;
        }
        return matrix[x];
    }

    public String colToString( int idx ) {
        return toCharString( getColumn( idx ) );
    }

    public int[] getColumn( int y ) {
        int[] column = new int[matrix.length];
        for ( int x = 0; x < getXLength(); x++ ) {
            column[x] = matrix[x][y];
        }
        return column;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( int x = 0; x < getXLength(); x++ ) {
            for ( int y = 0; y < getYLength(); y++ ) {
                if ( printMap.isEmpty() ) {
                    builder.append( Character.valueOf( (char) getValue( createCoords( x, y ) ) ) );
                }
                else {
                    builder.append( printMap.get( getValue( createCoords( x, y ) ) ) );
                }
            }
            builder.append( "\n" );
        }
        return builder.toString();
    }

    public IntStream colAsStream( int y ) {
        return Arrays.stream( getColumn( y ) );
    }

    public Matrix transpose() {
        return new Matrix( this.columns().toList().toArray( new int[0][0] ) );
    }

    public Stream<int[]> rows() {
        return Arrays.stream( matrix );
    }

    public Stream<int[]> columns() {
        List<int[]> columns = new ArrayList<>();
        for ( int y = 0; y < getYLength(); y++ ) {
            columns.add( getColumn( y ) );
        }
        return columns.stream();
    }

}
