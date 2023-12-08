package de.delusions.aoc;

import de.delusions.util.Day;

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

public class Day08 extends Day<Integer> {
    private static final String START = "AAA";

    private final Pattern pNode = Pattern.compile( "^([\\dA-Z]{3}) = .([\\dA-Z]{3}), ([\\dA-Z]{3}).$" );

    public Day08( Integer... expected ) {
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

    private TreeNode readTreeNode( String line ) {
        Matcher matcher = pNode.matcher( line );
        if ( matcher.matches() ) {
            String id = matcher.group( 1 );
            return new TreeNode( id, matcher.group( 2 ), matcher.group( 3 ) );
        }
        else {
            return null;
        }
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Map<String, TreeNode> map = new HashMap<>();
        List<Command> commandSequence = new ArrayList<>();
        readInput( input, commandSequence, map );
        return calculateSteps( commandSequence, List.of( map.get( START ) ), map, "ZZZ" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Map<String, TreeNode> map = new HashMap<>();
        List<Command> commandSequence = new ArrayList<>();
        readInput( input, commandSequence, map );
        return calculateSteps( commandSequence, getStartingNodes( map ), map, "Z" );
    }

    private int calculateSteps( List<Command> commandSequence, List<TreeNode> currentNodes, Map<String, TreeNode> map, String end ) {
        LinkedList<Command> commands = new LinkedList<>( commandSequence );
        int steps = 0;
        while ( enRoute( currentNodes, end ) ) {
            steps++;
            Command command = commands.pop();
            currentNodes = currentNodes.stream().map( node -> node.follow( command, map ) ).toList();
            if ( commands.isEmpty() ) {
                commands.addAll( commandSequence );
            }
        }
        return steps;
    }

    private void readInput( Stream<String> input, List<Command> commandSequence, Map<String, TreeNode> map ) {
        input.forEach( line -> {
            if ( commandSequence.isEmpty() ) {
                commandSequence.addAll( readCommands( line ) );
            }
            else if ( !line.isBlank() ) {
                TreeNode node = readTreeNode( line );
                if ( node != null ) {
                    map.put( node.id(), node );
                }
            }
        } );
    }

    private boolean enRoute( List<TreeNode> current, String end ) {
        return !current.stream().allMatch( n -> n.id().endsWith( end ) );
    }

    private List<TreeNode> getStartingNodes( Map<String, TreeNode> map ) {
        return map.values().stream().filter( n -> n.id().endsWith( "A" ) ).toList();
    }

    enum Command {LEFT, RIGHT}

    record TreeNode(String id, String left, String right) {
        TreeNode follow( Command command, Map<String, TreeNode> map ) {
            String nextNodeId = ( command == LEFT ) ? this.left() : this.right();
            return nextNodeId == null ? this : map.get( nextNodeId );
        }
    }

}
