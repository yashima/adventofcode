package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19 extends Day<Long> {
    static final char ACCEPT = 'A';

    static final char REJECT = 'R';

    static final String START = "in";

    static final Pattern pWorkflow = Pattern.compile( "([a-z]+)\\{(.*)}" );

    static final Pattern pRule = Pattern.compile( "([xmas])([<>])(\\d+):([a-zRA]+)" );

    static final Pattern pPart = Pattern.compile( "x.(\\d+).m.(\\d+).a.(\\d+).s.(\\d+)." );

    public Day19( Long... expected ) {super( 19, "Aplenty", expected );}

    static Function<Part, Integer> fXmas( char c ) {
        return switch ( c ) {
            case 'x' -> Part::x;
            case 'm' -> Part::m;
            case 'a' -> Part::a;
            case 's' -> Part::s;
            default -> throw new IllegalStateException( "This is not xmas" );
        };
    }

    Map<String, Workflow> workflows = new HashMap<>();

    List<Part> parts;

    @Override
    public Long part0( Stream<String> input ) {

        parts = parseInput( input );
        AtomicLong sum = new AtomicLong( 0 );
        parts.forEach( part -> {
            Workflow w = workflows.get( START );
            String target = process( part, w );
            if ( target.charAt( 0 ) == ACCEPT ) {
                sum.addAndGet( part.sum() );
            }
        } );
        return sum.get();
    }

    @Override
    public Long part1( Stream<String> input ) {
        return null;
    }

    String process( Part p, Workflow w ) {
        String target = w.rules().stream().filter( r -> r.satisfiedBy( p ) ).map( Rule::target ).findFirst().orElse( null );
        if ( target == null ) {
            throw new IllegalStateException( "Bug Alarm: no matching rule for " + p + " in " + w );
        }
        return switch ( target.charAt( 0 ) ) {
            case ACCEPT, REJECT -> target;
            default -> process( p, workflows.get( target ) );
        };
    }

    record Part(int x, int m, int a, int s) {
        int sum() {return x + m + a + s;}
    }

    record Rule(Character xmas, boolean greater, int threshold, String target) {
        boolean satisfiedBy( Part part ) {
            if ( xmas == null ) {return true;}
            Integer cat = fXmas( xmas ).apply( part );
            return greater ? cat > threshold : cat < threshold;
        }
    }

    record Workflow(String id, List<Rule> rules) {
        boolean accept( Part p ) {
            return true;
        }
    }

    private List<Part> parseInput( Stream<String> input ) {
        List<Part> parts = new ArrayList<>();
        input.forEach( line -> {
            Matcher m1 = pWorkflow.matcher( line );
            Matcher m2 = pPart.matcher( line );
            if ( m2.find() ) {
                parts.add( new Part( Integer.parseInt( m2.group( 1 ) ),
                                     Integer.parseInt( m2.group( 2 ) ),
                                     Integer.parseInt( m2.group( 3 ) ),
                                     Integer.parseInt( m2.group( 4 ) ) ) );
            }
            else if ( m1.find() ) {
                String id = m1.group( 1 );
                String[] r = m1.group( 2 ).split( "," );

                List<Rule> rules = Arrays.stream( r ).map( rpart -> {
                    Matcher m3 = pRule.matcher( rpart );
                    if ( m3.find() ) {
                        return new Rule( m3.group( 1 ).charAt( 0 ), m3.group( 2 ).equals( ">" ), Integer.parseInt( m3.group( 3 ) ), m3.group( 4 ) );
                    }
                    return new Rule( null, true, 0, rpart );
                } ).toList();
                workflows.put( id, ( new Workflow( id, rules ) ) );
            }
            else {
                System.err.println( line );
            }
        } );
        return parts;
    }
}
