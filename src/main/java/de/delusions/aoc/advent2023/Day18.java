package de.delusions.aoc.advent2023;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day18 extends Day<String> {

    static final Pattern p = Pattern.compile( "([RDLU]{1}) ([0-9]+) .#([a-f,0-9]{5})(.)" );


    public Day18( String... expected ) {super( 18, "Lavaduct Lagoon", expected );}

    @Override
    public String part0( Stream<String> input ) {
        return solve( input, DigCommands::meters, DigCommands::dir );
    }

    @Override
    public String part1( Stream<String> input ) {
        return solve( input, DigCommands::hexMeters, DigCommands::hexDir );
    }

    String solve( Stream<String> input, Function<DigCommands, Integer> meters, Function<DigCommands, Direction> direction ) {
        List<DigCommands> digPlan = input.map( Day18::parseFrom ).toList();
        AtomicReference<Coordinates> digger = new AtomicReference<>( new Coordinates( 0, 0 ) );
        List<Coordinates> vertices = digPlan.stream().map( c -> c.getThere( digger, direction, meters ) ).toList();
        return pickTheorem( shoelace( vertices ), boundaryPoints( digPlan, meters ) ).toString();
    }

    //this produces the correct "sequence" for the test input as compared to website
    static DigCommands parseFrom( String line) {
        Matcher m = p.matcher( line );
        if ( m.find() ) {
            int lastHexDigit = Integer.parseInt( m.group( 4 ), 16 );
            return new DigCommands( getDirection( m.group( 1 ).charAt( 0 ) ),
                                    Integer.parseInt( m.group( 2 ) ),
                                    getDirection( Integer.toString(lastHexDigit).charAt( 0 ) ),
                                    Integer.parseInt( m.group( 3 ), 16 ));
        }
        throw new IllegalStateException( "Line '" + line + "' could not be parsed" );
    }

    /**
     * For reasons, Pick's Theorem which originally goes area = shoelaceresult + (boundary/2) - 1 needs to modified to +1 instead, I figured it out
     * and then saw someone on reddit attempting a proof of why that is which I couldn't quite follow, but +1 works
     */
    BigInteger pickTheorem( BigInteger shoelace, BigInteger boundary ) {
        return shoelace.add( boundary.divide( BigInteger.TWO ) ).add( BigInteger.ONE );
    }

    /**
     * BigInt to protect us from overflows and in general produce safer math
     */
    public BigInteger shoelace( List<Coordinates> vertices ) {
        int numberOfVertices = vertices.size();

        BigInteger sum1 = multiply( vertices.getLast(), vertices.getFirst() );
        BigInteger sum2 = multiply( vertices.getFirst(), vertices.getLast() );

        for ( int i = 0; i < numberOfVertices - 1; i++ ) {
            sum1 = sum1.add( multiply( vertices.get( i ), vertices.get( i + 1 ) ) );
            sum2 = sum2.add( multiply( vertices.get( i + 1 ), vertices.get( i ) ) );
        }

        return sum1.subtract( sum2 ).abs().divide( BigInteger.TWO );
    }

    BigInteger boundaryPoints( List<DigCommands> digplan, Function<DigCommands, Integer> mapper ) {
        return BigInteger.valueOf( ( (Integer) digplan.stream().mapToInt( mapper::apply ).sum() ).longValue() );
    }

    static Direction getDirection( char indicator ) {
        return switch ( indicator ) {
            case 'R', '0' -> Direction.east;
            case 'D', '1' -> Direction.south;
            case 'L', '2' -> Direction.west;
            case 'U', '3' -> Direction.north;
            default -> throw new IllegalStateException( "Direction '" + indicator + "' not recognized" );
        };
    }

    BigInteger multiply( Coordinates forX, Coordinates forY ) {
        Function<Coordinates, Integer> xF = Coordinates::getX;
        Function<Coordinates, Integer> yF = Coordinates::getY;
        return convert( forX, xF ).multiply( convert( forY, yF ) );
    }

    BigInteger convert( Coordinates c, Function<Coordinates, Integer> coordinateFunc ) {
        return BigInteger.valueOf( coordinateFunc.apply( c ).longValue() );
    }

    /**
     * This solves part 0/1 easily but part 2 would run out of heapspace with the size of the matrix -> still keeping it
     */
    @Deprecated
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
            if ( value == 'O' ) {
                //nothing new to see here: we've been here before and added the neighbors
            }
            else if ( value == 'I' ) {
                //nothing to see here: actually also do nothing? because that is part of the structure we're trying to isolate
            }
            else if ( value == '.' ) {
                //go get the neighbors:
                matrix.setValue( node, 'O' );
                Direction.getBasic().stream().map( node::moveTo ).filter( Predicate.not( visited::contains ) ).toList().forEach( nodes::push );
            }
        }
    }

    record DigCommands(Direction dir, int meters, Direction hexDir, int hexMeters) {

        Coordinates getThere( AtomicReference<Coordinates> from,
                              Function<DigCommands, Direction> dirFunc,
                              Function<DigCommands, Integer> meterFunc ) {
            Coordinates diggerCurrent = from.get();
            Coordinates diggerNext = diggerCurrent.moveTo( dirFunc.apply( this ), meterFunc.apply( this ), 1 );
            from.set( diggerNext );
            return diggerNext;
        }
    }
}
