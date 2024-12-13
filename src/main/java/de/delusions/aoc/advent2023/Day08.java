package main.java.de.delusions.aoc.advent2023;

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

import static main.java.de.delusions.aoc.advent2023.Day08.Command.LEFT;
import static main.java.de.delusions.aoc.advent2023.Day08.Command.RIGHT;

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


    /**
     * Parses the command sequence from the first line of the input
     *
     * @param line the first line of the input
     * @return a list of commands (to be repeated as necessary)
     */
    private List<Command> readCommands( String line ) {
        return line.chars().mapToObj( Day08::getByChar ).toList();
    }

    /**
     * Read a single node from the input
     *
     * @param line a line containing the description for a node
     * @return
     */
    private Node readNode( String line ) {
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
        return calculateSteps( commandSequence, List.of( map.get( START ) ), map, "ZZZ" ).getFirst().toString();
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

    /**
     * Calculates a path through the graph for each starting node until it has reached an end node.
     * @param commandSequence the command sequence from the input
     * @param currentNodes the list of starting nodes
     * @param map the map containing the graph
     * @param end the "end" condition
     * @return a list of steps per starting node that were taken
     */
    private List<Integer> calculateSteps( List<Command> commandSequence, List<Node> currentNodes, Map<String, Node> map, String end ) {
        LinkedList<Command> commands = new LinkedList<>( commandSequence );
        List<Integer> periods = new ArrayList<>();
        for ( Node currentNode : currentNodes ) {
            int steps = 0;
            Node node = currentNode;
            while ( enRoute( node, end ) ) {
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
                Node node = readNode( line );
                if ( node != null ) {
                    map.put( node.id(), node );
                }
            }
        } );
    }

    /**
     * @param current the current node of the graph
     * @param end     the end condition
     * @return true if the current node does not (yet) match the end condition
     */
    private boolean enRoute( Node current, String end ) {
        return current.id.endsWith( end );
    }

    /**
     * Finds all the starting nodes ending with an 'A' for part 2
     * @param map
     * @return
     */
    private List<Node> getStartingNodes( Map<String, Node> map ) {
        return map.values().stream().filter( n -> n.id().endsWith( "A" ) ).toList();
    }

    /**
     * The types of commands that determine which node will be chosen next
     */
    enum Command {LEFT, RIGHT}

    /**
     * A node in the graphs
     * @param id the name of the node
     * @param left the name of the next node for the "LEFT" command
     * @param right the name of the next node for the "RIGHT" command
     */
    record Node(String id, String left, String right) {
        /**
         * Given a command returns the next node
         * @param command the command to follow
         * @param map the graph
         * @return node following this one on the graph with the given command
         */
        Node follow( Command command, Map<String, Node> map ) {
            String nextNodeId = ( command == LEFT ) ? this.left() : this.right();
            return nextNodeId == null ? this : map.get( nextNodeId );
        }
    }

}
