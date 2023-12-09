package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.Day08.Command.LEFT;
import static de.delusions.aoc.Day08.Command.RIGHT;

public class Day08 extends Day<String> {
    private static final String START = "AAA";

    private final Pattern pNode = Pattern.compile( "^([\\dA-Z]{3}) = .([\\dA-Z]{3}), ([\\dA-Z]{3}).$" );

    public Day08( String... expected ) {
        super( 8, "Haunted Wasteland", expected );
    }

    static Command getByChar( int character ) {
        return switch ( character ) {
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            default -> null;
        };
    }


    private List<Command> readCommands( String line ) {

        return line.chars().mapToObj( Day08::getByChar ).toList();
    }

    private Node readTreeNode( String line ) {
        Matcher matcher = pNode.matcher( line );
        if ( matcher.matches() ) {
            String id = matcher.group( 1 );
            return new Node( id, matcher.group( 2 ), matcher.group( 3 ) );
        }
        else {
            return null;
        }
    }

    @Override
    public String part0( Stream<String> input ) {
        Map<String, Node> map = new HashMap<>();
        List<Command> commandSequence = new ArrayList<>();
        readInput( input, commandSequence, map );
        return calculateSteps( commandSequence, List.of( map.get( START ) ), map, "ZZZ" ).get( 0 ).toString();
    }

    @Override
    public String part1( Stream<String> input ) {
        Map<String, Node> map = new HashMap<>();
        List<Command> commandSequence = new ArrayList<>();
        readInput( input, commandSequence, map );
        List<Integer> periods = calculateSteps( commandSequence, getStartingNodes( map ), map, "Z" );
        List<List<Integer>> primeFactors = periods.stream()//
            .peek( System.out::print )//
            .map( MathUtil::calculatePrimeFactors )//
            .peek( System.out::println )//
            .toList();
        return MathUtil.calculateSmallestCommonMultiple( primeFactors ).toString();
    }

    private List<Integer> calculateSteps( List<Command> commandSequence, List<Node> currentNodes, Map<String, Node> map, String end ) {
        LinkedList<Command> commands = new LinkedList<>( commandSequence );
        List<Integer> periods = new ArrayList<>();
        for ( Node currentNode : currentNodes ) {
            int steps = 0;
            Node node = currentNode;
            while ( enRoute( List.of( node ), end ) ) {
                steps++;
                Command command = commands.pop();
                node = node.follow( command, map );
                if ( commands.isEmpty() ) {
                    commands.addAll( commandSequence );
                }
            }
            periods.add( steps );
        }
        return periods;
    }

    private void readInput( Stream<String> input, List<Command> commandSequence, Map<String, Node> map ) {
        input.forEach( line -> {
            if ( commandSequence.isEmpty() ) {
                commandSequence.addAll( readCommands( line ) );
            }
            else if ( !line.isBlank() ) {
                Node node = readTreeNode( line );
                if ( node != null ) {
                    map.put( node.id(), node );
                }
            }
        } );
    }

    private boolean enRoute( List<Node> current, String end ) {
        return !current.stream().allMatch( n -> n.id().endsWith( end ) );
    }

    private List<Node> getStartingNodes( Map<String, Node> map ) {
        return map.values().stream().filter( n -> n.id().endsWith( "A" ) ).toList();
    }

    enum Command {LEFT, RIGHT}

    record Node(String id, String left, String right) {
        Node follow( Command command, Map<String, Node> map ) {
            String nextNodeId = ( command == LEFT ) ? this.left() : this.right();
            return nextNodeId == null ? this : map.get( nextNodeId );
        }
    }

}
