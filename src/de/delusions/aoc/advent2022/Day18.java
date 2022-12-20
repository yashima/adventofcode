package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 extends Day<Integer> {

    public static final int MAX_DIM = 25;

    public static final int DROPLET = 1;

    public static final int OUTSIDE = 2;

    private int[][][] droplet;

    Day18() {
        super( 18, "Boiling Boulders" );
    }

    enum Dimension {X, Y, Z}

    @Override
    public Integer part1( Stream<String> input ) {
        droplet = parse( input );
        //fillNegativeSpace();
        AtomicInteger surface = new AtomicInteger( 0 );
        IntStream.range( 0, MAX_DIM ).forEach( x -> IntStream.range( 0, MAX_DIM ).forEach(
            y -> IntStream.range( 0, MAX_DIM ).forEach( z -> surface.addAndGet( countSurface( x, y, z ) ) ) ) );
        return surface.get();
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }


    int countSurface( int x, int y, int z ) {
        if ( droplet[x][y][z] !=DROPLET ) {
            return 0;
        }
        int count = 0;

        count += surface( x+1, y, z);
        count += surface( x-1, y, z);
        count += surface( x, y+1, z);
        count += surface( x, y-1, z);
        count += surface( x, y, z+1);
        count += surface( x, y, z-1);
        //System.out.printf( "(%s,%s,%s)=%s%n", x, y, z, count );
        return count;
    }

    int surface( int x, int y, int z){
        if(x<0 || y<0 || z<0){
            return 1;
        }
       return droplet[x][y][z]!=DROPLET ? 1 : 0;
    }

    int isOutside( Dimension dim, boolean inverted, int p, int other, int another ) {
        return OUTSIDE == getValue( dim, inverted, p + 1, other, another ) ? 1 : 0;
    }

    void fillNegativeSpace() {
        for ( Dimension dim : Dimension.values() ) {
            fillNegativeSpace( dim, true );
            fillNegativeSpace( dim, false );
        }
        IntStream.range( 0, MAX_DIM ).forEach( slice -> System.out.println( print( Dimension.X, slice ) ) );
    }

    void fillNegativeSpace( Dimension dim, boolean inverted ) {

        IntStream.range( 0, MAX_DIM ).forEach( other -> IntStream.range( 0, MAX_DIM ).forEach( another -> {
            for ( int position = 0; position < MAX_DIM; position++ ) {
                if ( getValue( dim, inverted, position, other, another ) != DROPLET ) {
                    setValue( dim, inverted, position, other, another, OUTSIDE );
                }
                else {
                    break;
                }
            }
        } ) );
    }

    int getValue( Dimension dimension, boolean inverted, int p, int other, int another ) {
        return switch ( dimension ) {
            case X -> droplet[position( p, inverted )][other][another];
            case Y -> droplet[other][position( p, inverted )][another];
            case Z -> droplet[other][another][position( p, inverted )];
        };
    }

    void setValue( Dimension dimension, boolean inverted, int p, int other, int another, int value ) {
        switch ( dimension ) {
            case X:
                droplet[position( p, inverted )][other][another] = value;
            case Y:
                droplet[other][position( p, inverted )][another] = value;
            case Z:
                droplet[other][another][position( p, inverted )] = value;
        }
    }

    int position( int p, boolean invert ) {
        return invert ? MAX_DIM - p - 1 : p;
    }

    /**
     * Parses the 3D coordinates for the droplet from the iput
     *
     * @param input inputfile as a stream
     * @return a 3D matrix with all the parsed positions set to 1
     */
    int[][][] parse( Stream<String> input ) {
        int[][][] droplet = new int[MAX_DIM][MAX_DIM][MAX_DIM]; //rather make 1 bigger here
        input.sorted().forEach( line -> {
            String[] split = line.split( "," );
            droplet[Integer.parseInt( split[0] )][Integer.parseInt( split[1] )][Integer.parseInt( split[2] )] = DROPLET;
        } );
        return droplet;
    }

    String print( Dimension dimension, int slice ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "---------" ).append( dimension ).append( slice ).append( "---------\n" );
        IntStream.range( 0, MAX_DIM ).forEach( other -> IntStream.range( 0, MAX_DIM ).forEach( another -> {
            int value = getValue( dimension, false, slice, other, another );
            builder.append( " " ).append( value == DROPLET ? "#" : value == OUTSIDE ? "." : "o" ).append( " " );
            if ( another == MAX_DIM - 1 ) {
                builder.append( "\n" );
            }
        } ) );
        builder.append( "--------------------\n" );
        return builder.toString();
    }
}
