package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.advent2022.Day19.Material.*;

public class Day19 extends Day<Integer> {

    private static final String INPUT_REGEX =
        "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";

    private static final Pattern PATTERN = Pattern.compile( INPUT_REGEX );

    public Day19() {
        super( 19, "Not Enough Minerals" );
    }

//    static void log( String method, Machine machine, String format, Object... args ) {
//        System.out.println( TIMER.get() + ":" + method + ":bp=" + machine.blueprint.blueprintId + ":" + String.format( format, args ) );
//    }

    @Override
    public Integer part1( Stream<String> input ) {
        return solve( parse( input ) );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    Integer solve( List<Blueprint> blueprints ) {
        return blueprints.stream().map( this::runBluePrint ).reduce( 0, Integer::sum );
    }

    int runBluePrint( Blueprint blueprint ) {
        int[][] start = initializeMachine( blueprint );
        Stack<int[][]> opens = new Stack<>();
        Set<int[][]> candidates = new HashSet<>();
        opens.push( start );
        for ( int time = 24; time >= 0; time-- ) {
            while ( !opens.isEmpty() ) {
                int[][] state = opens.pop();
                candidates.addAll( runNextStep( state ) );
            }
            opens.addAll( candidates );
            if ( time > 0 ) {candidates.clear();}
        }
        return candidates.stream().map( state -> state[GEODE.ordinal()][PILE.ordinal()] ).max( Integer::compareTo ).orElse( 0 ) *
            blueprint.blueprintId;
    }

    List<Blueprint> parse( Stream<String> input ) {
        return input.map( line -> {
            Matcher matcher = PATTERN.matcher( line );
            if ( matcher.matches() ) {
                return new Blueprint( Integer.parseInt( matcher.group( 1 ) ), Integer.parseInt( matcher.group( 2 ) ),
                                      Integer.parseInt( matcher.group( 3 ) ), Integer.parseInt( matcher.group( 4 ) ),
                                      Integer.parseInt( matcher.group( 5 ) ), Integer.parseInt( matcher.group( 6 ) ),
                                      Integer.parseInt( matcher.group( 7 ) ) );
            }
            return null;
        } ).filter( Objects::nonNull ).peek( System.out::println ).toList();
    }

    enum Material {GEODE, OBSIDIAN, CLAY, ORE, PROD, PILE}

    record Blueprint(int blueprintId, int oreRobotCostOre, int clayRobotCostOre, int obsidianRobotOre, int obsidianRobotClay, int geodeRobotOre,
                     int geodeRobotObsidian) {}


    int[][] initializeMachine( Blueprint blueprint ) {
        int[][] startState = new int[Material.values().length - 2][Material.values().length];
        startState[ORE.ordinal()][ORE.ordinal()] = blueprint.oreRobotCostOre;
        startState[ORE.ordinal()][PROD.ordinal()] = 1;
        startState[CLAY.ordinal()][ORE.ordinal()] = blueprint.clayRobotCostOre;
        startState[OBSIDIAN.ordinal()][ORE.ordinal()] = blueprint.obsidianRobotOre;
        startState[OBSIDIAN.ordinal()][CLAY.ordinal()] = blueprint.obsidianRobotClay;
        startState[GEODE.ordinal()][ORE.ordinal()] = blueprint.geodeRobotOre;
        startState[GEODE.ordinal()][OBSIDIAN.ordinal()] = blueprint.geodeRobotObsidian;
        return startState;
    }

    List<int[][]> runNextStep( int[][] state ) {
        //not building is an option, so add null to the stream
        return canBuild( state ).stream().map( robotToBuild -> runStep( state, robotToBuild ) ).toList();
    }

    int[][] runStep( int[][] state, Material robotToBuild ) {
        int[][] copy = state.clone();
        for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) {
            int cost = 0;
            if ( robotToBuild != null ) { //since we only put buildable robots in here it doesn't matter when we add resources
                cost = copy[robotToBuild.ordinal()][material.ordinal()];
                copy[robotToBuild.ordinal()][PROD.ordinal()]++;
            }
            int production = copy[material.ordinal()][PROD.ordinal()];
            int pile = copy[material.ordinal()][PILE.ordinal()];
            copy[material.ordinal()][PILE.ordinal()] = pile + production - cost;
        }
        return copy;
    }

    List<Material> canBuild( int[][] state ) {
        List<Material> canBuild = Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).filter( m -> canBuild( state, m ) ).toList();
        List<Material> canBuildOrNot = new ArrayList<>( canBuild );
        canBuildOrNot.add( null ); //or not
        return canBuildOrNot;
    }

    boolean canBuild( int[][] state, Material robotToBuild ) {
        for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) {
            int cost = state[robotToBuild.ordinal()][material.ordinal()];
            int pile = state[material.ordinal()][PILE.ordinal()];
            //TODO check if this is an actual optimization or a bug
            if ( cost != pile ) { //if we decided not to build this last round, not building it this round
                return false;
            }
        }
        return true;
    }


}
