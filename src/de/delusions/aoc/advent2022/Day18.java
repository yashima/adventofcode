package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 extends Day<Integer> {

    public static final int MAX_DIM = 22;

    public static final int DROPLET = 1;

    public static final int OUTSIDE = 2;

    private int[][][] droplet;

    Day18() {
        super( 18, "Boiling Boulders" );
    }

    enum Dimension {X, Y, Z}

    record Block(int x, int y, int z) {}

    AtomicInteger blockCounter =  new AtomicInteger();

    @Override
    public Integer part1( Stream<String> input ) {
        droplet = parse( input );
        blockCounter.set( 0 );
        return solve( block -> !isDroplet( block )  );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        blockCounter.set( 0 );
        doWithMatrix(block -> {
            if(isDroplet( block )){
                return true;
            } else {
                setToOutside( block );
                return false;
            }
        });
        return solve( this::isOutside );
    }

    /**
     * Calculates the sum of droplet cube faces that satisfy the given isEmptySpace function
     *
     * @param surfaceFunc the function that determines what cube faces are counted
     * @return the solution is the sum of these faces
     */
    int solve( Function<Block, Boolean> surfaceFunc ) {
        AtomicInteger surface = new AtomicInteger( 0 );
        doWithMatrix( Dimension.X,false, block -> {
            surface.addAndGet( countSurface( block, surfaceFunc ));
            return false;
        });
        System.out.println("Counted surfaces for blocks="+blockCounter.get());
        return surface.get();
    }

    /**
     * Tries to fill in the negative space outside the droplet to make it possible to count the surfaces for part 2
     */
    void doWithMatrix(Function<Block,Boolean> blockFunc) {
        for ( Dimension dim : Dimension.values() ) {
            doWithMatrix( dim, false, blockFunc );
            doWithMatrix( dim, true ,blockFunc);
        }
        //IntStream.range( 0, MAX_DIM ).forEach( slice -> System.out.println( print( Dimension.X, slice ) ) );
    }

    /**
     * Fills the negative space coming from a certain direction and looking as far as the
     * first droplet block coming from that direction on each of the other two dimensions.
     * Six passes needed: 2 for each dimension, 1 at forward count and 1 at inverted count
     * @param dim the dimension we're looking from
     * @param inverted or the opposite
     */
    void doWithMatrix( Dimension dim, boolean inverted, Function<Block,Boolean> blockFunc ) {
        for ( int second = 0; second < MAX_DIM; second++ ) {
            for ( int third = 0; third < MAX_DIM; third++ ) {
                for ( int first = 0; first < MAX_DIM; first++ ) {
                    int position = inverted ? MAX_DIM - first - 1 : first;
                    if(blockFunc.apply( getDimensionBlock( dim, position, second, third ) )){
                        break;
                    }
                }
            }
        }
    }

    /**
     * Count all the surfaces for a given position that satisfy the isEmptySpace function
     * Only counts surfaces for droplets. Other blocks always return 0
     *
     * @param surfaceFunc the function that is used for testing
     * @return the sum of the sides that match the isEmptySpace function
     */
    int countSurface( Block block, Function<Block, Boolean> surfaceFunc ) {
        if ( !isDroplet( block ) ) { //we're not processing anything but lava
            return 0;
        }
        blockCounter.incrementAndGet();//count dropletblogs
        int result = 0;
        //test all adjacent coordinates against the surfaceFunc
        for(Dimension dimension : Dimension.values()){
            result += surfaceFunc.apply( getNeighboringBlock( block, dimension, false ) ) ? 1 : 0;
            result += surfaceFunc.apply( getNeighboringBlock( block, dimension, true ) ) ? 1 : 0;
        }
        return result;
    }

    /**
     * Get the value at the block's position
     *
     * @param block the block describing the position
     * @return the value of the position
     */
    int getValue( Block block ) {
        return droplet[block.x][block.y][block.z];
    }

    /**
     * Sets the value at the block's position
     *
     * @param block the block describing the position
     */
    void setToOutside( Block block ) {
        droplet[block.x][block.y][block.z] = OUTSIDE;
    }

    /**
     * Get a block modified by 1 in the given dimension either forward or backward
     *
     * @param block     the block to generate this from
     * @param dimension the dimension to change
     * @param negative  if the modifier is negative or positive
     * @return a neighboring block as described by the parameters
     */
    Block getNeighboringBlock( Block block, Dimension dimension, boolean negative ) {
        int modifier = negative ? -1 : +1;
        int x = block.x + ( dimension == Dimension.X ? modifier : 0 );
        int y = block.y + ( dimension == Dimension.Y ? modifier : 0 );
        int z = block.z + ( dimension == Dimension.Z ? modifier : 0 );
        return new Block( x, y, z );
    }

    /**
     * Get a block as described by the coordinates, but "shifted"
     *
     * @param shift    the dimension that is described by the position parameter
     * @param position the position in the dimension we're looking at right now
     * @param second    the "first" of the second two dimensions
     * @param third  the "second" of the second two dimensions
     * @return a block as described by the parameters
     */
    Block getDimensionBlock( Dimension shift, int position, int second, int third ) {
        return switch ( shift ) {
            case X -> new Block( position, second, third );
            case Y -> new Block( second, position, third );
            case Z -> new Block( second, third, position );
        };
    }

    /**
     * Checks if a given block is outside the matrix coordinates (to prevent ArrayIndexOutOfBounds)
     *
     * @param block the block to check
     * @return true if it is within the dimensions
     */
    boolean isOutsideOfMatrix( Block block ) {
        return block.x < 0 || block.y < 0 || block.z < 0 || block.x >= MAX_DIM || block.y >= MAX_DIM || block.z >= MAX_DIM;
    }

    /**
     * Checks if block is part of the droplet
     * @param block the block to check
     * @return true if it is part of the droplet
     */
    boolean isDroplet(Block block){
        return !isOutsideOfMatrix( block ) && getValue( block )==DROPLET;
    }

    /**
     * Checks if block is marked as outside the droplet
     * @param block the block to check
     * @return true if it is marked as outside or is outside the matrix boundaries
     */
    boolean isOutside(Block block){
        return isOutsideOfMatrix( block ) || getValue( block )==OUTSIDE;
    }

    /**
     * Parses the 3D coordinates for the droplet from the input
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

    /**
     * Prints a slice of the matrix
     *
     * @param dimension the dimension which is fixed
     * @param slice     the position of the slice
     * @return a string representing the slice
     */
    String print( Dimension dimension, int slice ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "---------" ).append( dimension ).append( slice ).append( "---------\n" );
        IntStream.range( 0, MAX_DIM ).forEach( other -> IntStream.range( 0, MAX_DIM ).forEach( another -> {
            int value = getValue( getDimensionBlock( dimension, slice, other, another ) );
            builder.append( " " ).append( value == DROPLET ? "#" : value == OUTSIDE ? "." : "o" ).append( " " );
            if ( another == MAX_DIM - 1 ) {
                builder.append( "\n" );
            }
        } ) );
        builder.append( "--------------------\n" );
        return builder.toString();
    }
}
