package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 extends Day<Integer> {
    static final char OUTSIDE = 'O';

    public Day18( Integer... expected ) {super( 18, "Lavaduct Lagoon", expected );}

    static final char INSIDE = 'I';

    static final char TRENCH = '#';

    static final char EMPTY = '.';

    static Pattern p = Pattern.compile( "^([RDLU]{1}) ([0-9]{1,2}).*([a-z,0-9]{6})" );

    @Override
    public Integer part0( Stream<String> input ) {
        List<DigCommands> digPlan = input.map( Day18::parseFrom ).toList();
        List<Coordinates> loop = new ArrayList<>();
        AtomicReference<Coordinates> digger = new AtomicReference<>( new Coordinates( 0, 0 ) );
        AtomicInteger maxX = new AtomicInteger( 0 );
        AtomicInteger maxY = new AtomicInteger( 0 );
        AtomicInteger minX = new AtomicInteger( 0 );
        AtomicInteger minY = new AtomicInteger( 0 );
        digPlan.forEach( command -> {

            IntStream.range( 0, command.meters ).forEach( i -> {
                loop.add( digger.get() );
                digger.set( digger.get().moveTo( command.d, command.color ) );
            } );
            maxX.set( Math.max( maxX.get(), digger.get().getX() ) );
            maxY.set( Math.max( maxY.get(), digger.get().getY() ) );
            minX.set( Math.min( minX.get(), digger.get().getX() ) );
            minY.set( Math.min( minY.get(), digger.get().getY() ) );
        } );
        System.out.printf( "%d x %d : %d x %d%n", minX.get(), minY.get(), maxX.get(), maxY.get() );
        //adding 1 space around the matrix
        Matrix matrix = new Matrix( maxX.get() - minX.get() + 1 + 2, maxY.get() - minY.get() + 1 + 2, minX.get() - 1, minY.get() - 1 );
        matrix.setAllValues( EMPTY );
        loop.forEach( trench -> {
            matrix.setValue( trench, TRENCH );
        } );
        //matrix.setValue( 0, 0, 'S' );
        flood( matrix, new Coordinates( minX.get() - 1, minY.get() - 1 ) );
        System.out.println( matrix );
        return matrix.findValues( EMPTY, false ).size() + matrix.findValues( TRENCH, false ).size();
    }

    private void flood( Matrix matrix, Coordinates start ) {
        Stack<Coordinates> nodes = new Stack<>();
        Set<Coordinates> visited = new HashSet<>();
        nodes.push( start );
        while ( !nodes.empty() ) {
            Coordinates node = nodes.pop();
            if ( !matrix.isInTheMatrix( node ) ) {
                continue;
            }
            visited.add( node );
            int value = matrix.getValue( node );
            if ( value == OUTSIDE ) {
                //nothing new to see here: we've been here before and added the neighbors
            }
            else if ( value == INSIDE ) {
                //nothing to see here: actually also do nothing? because that is part of the structure we're trying to isolate
            }
            else if ( value == EMPTY ) {
                //go get the neighbors:
                matrix.setValue( node, OUTSIDE );
                Direction.getBasic().stream().map( node::moveTo ).filter( Predicate.not( visited::contains ) ).toList().forEach( nodes::push );
            }
        }
    }

    static DigCommands parseFrom( String line ) {
        Matcher m = p.matcher( line );
        if ( m.find() ) {
            return new DigCommands( getDirection( m.group( 1 ).charAt( 0 ) ),
                                    Integer.parseInt( m.group( 2 ) ),
                                    Integer.parseInt( m.group( 3 ), 16 ) );
        }
        throw new IllegalStateException( "Line '" + line + "' could not be parsed" );
    }

    static Direction getDirection( char indicator ) {
        return switch ( indicator ) {
            case 'R' -> Direction.east;
            case 'D' -> Direction.south;
            case 'L' -> Direction.west;
            case 'U' -> Direction.north;
            default -> throw new IllegalStateException( "Direction '" + indicator + "' not recognized" );
        };
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }

    record DigCommands(Direction d, int meters, int color) {}
}
