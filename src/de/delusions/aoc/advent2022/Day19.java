package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

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

    Integer solve( List<Blueprint> blueprints ) {
        //TODO I am using a limit, dont forget
        return blueprints.stream().limit( 1 ).map( this::runBluePrint ).reduce( 0, Integer::sum );
    }

    /**
     * Runs a search for the best run for the given blueprint. Choices are in each round which bot to build next (or keep processing until one was
     * built)
     *
     * @param blueprint the blueprint to start with
     * @return the highest geode count that could be harvest with this blueprint
     */
    int runBluePrint( Blueprint blueprint ) {
        Stack<MachineState> opens = new Stack<>();
        Set<MachineState> candidates = new HashSet<>();
        opens.addAll( initializeMachine( blueprint ) );
        for ( int time = 5; time >= 0; time-- ) {
            System.out.println( time + ": opens " + opens.size() );
            while ( !opens.isEmpty() ) {
                MachineState state = opens.pop();
                candidates.addAll( runNextStep( state ) );
            }
            opens.addAll( candidates );
            System.out.println( opens.stream().map( machine -> machine.robotToBuild ).distinct().toList() );
            if ( time > 0 ) {candidates.clear();}
        }
        MachineState bestRun = candidates.stream().sorted().findFirst().orElse( null );
        return bestRun != null ? bestRun.getGeodePile() * blueprint.blueprintId : 0;
    }

    /**
     * Initializes the starting state for the machine from the blueprint. Because there is always 1 ore production it is always possible to build ORE
     * or CLAY bots from the start. So there are two choices right in the first round.
     *
     * @param blueprint blueprint to simulate
     * @return a list with 2 states: one waiting to build ORE bot, one with CLAY
     */
    List<MachineState> initializeMachine( Blueprint blueprint ) {
        int[][] startState = new int[Material.values().length - 2][Material.values().length];
        startState[ORE.ordinal()][ORE.ordinal()] = blueprint.oreRobotCostOre;
        startState[ORE.ordinal()][PROD.ordinal()] = 1;
        startState[CLAY.ordinal()][ORE.ordinal()] = blueprint.clayRobotCostOre;
        startState[OBSIDIAN.ordinal()][ORE.ordinal()] = blueprint.obsidianRobotOre;
        startState[OBSIDIAN.ordinal()][CLAY.ordinal()] = blueprint.obsidianRobotClay;
        startState[GEODE.ordinal()][ORE.ordinal()] = blueprint.geodeRobotOre;
        startState[GEODE.ordinal()][OBSIDIAN.ordinal()] = blueprint.geodeRobotObsidian;
        return List.of( new MachineState( startState, ORE ), new MachineState( startState.clone(), CLAY ) );
    }

    public Day19() {
        super( 19, "Not Enough Minerals" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return solve( parse( input ) );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    /**
     * Simulates a round of production for the given machine state and returns a list of choices what to do next. If a bot was built this step there
     * will be new choices otherwise the previous choice will remain.
     *
     * @param machine the machine state to simulate from
     * @return a list of possible next choices for
     */
    List<MachineState> runNextStep( MachineState machine ) {
        //not building is an option, so add null to the stream
        boolean builtABot = runStep( machine );
        if ( builtABot ) { //for the new states clone the matrix!
            return getBuildableBots( machine.state ).stream().map( nextBot -> new MachineState( machine.state.clone(), nextBot ) ).toList();
        }
        return List.of( machine );
    }

    /**
     * Simulate 1 step for the given machine state. Runs production and if it is ready to built the desired bot the costs will be removed from the
     * piles and the production increased.
     *
     * @param machine the machine for the simulation
     * @return true if this step a bot was build and new choices need to be made by the caller
     */
    boolean runStep( MachineState machine ) {
        boolean build = isReadyToBuild( machine );
        for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) { //run production
            if ( build ) {
                machine.pay( material );
            }
            machine.produce( material );
        }
        if ( build ) {//only add production at the end of the run.
            machine.state[machine.robotToBuild.ordinal()][PROD.ordinal()]++;
        }
        return build;
    }

    /**
     * Checks all materials if "in theory" a bot of this type could be built
     *
     * @param state matrix to check
     * @return a list of possible next builds
     */
    List<Material> getBuildableBots( int[][] state ) {
        return Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).filter( m -> isBuildableBot( state, m ) ).toList();
    }

    /**
     * Checks if a particular robot can be built "in theory" by verifying that the predecessor material has production -> ORE, CLAY and always be
     * built. OBSIDIAN only when CLAY exists and GEODE only when OBSIDIAN exists
     *
     * @param state        the matrix to check
     * @param robotToBuild the robot type to check
     * @return a list of bots that could be built
     */
    boolean isBuildableBot( int[][] state, Material robotToBuild ) {
        return robotToBuild == ORE || robotToBuild == CLAY || ( robotToBuild == OBSIDIAN && state[CLAY.ordinal()][PROD.ordinal()] > 0 ) ||
            ( robotToBuild == GEODE && state[OBSIDIAN.ordinal()][PROD.ordinal()] > 0 );
    }

    /**
     * Checks if for all materials the cost for the desired robot is "covered" by the existing stockpile
     *
     * @param state the machine state to check for readiness
     * @return true if all costs can be covered, false if there is not enough of at least 1 material to pay for the bot (cost > pile)
     */
    boolean isReadyToBuild( MachineState state ) {
        return !List.of( ORE, CLAY, OBSIDIAN, GEODE ).stream().filter( material -> state.getCost( material ) > state.getPile( material ) ).findFirst()
            .isPresent();
    }

    /**
     * Parse the input into Blueprint records using a regular expression
     *
     * @param input a bunch of strings
     * @return a bunch of blueprints
     */
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

    enum Material {GEODE, OBSIDIAN, CLAY, ORE, PROD, PILE;}

    record Blueprint(int blueprintId, int oreRobotCostOre, int clayRobotCostOre, int obsidianRobotOre, int obsidianRobotClay, int geodeRobotOre,
                     int geodeRobotObsidian) {}

    record MachineState(int[][] state, Material robotToBuild) implements Comparable<MachineState> {

        @Override
        public int compareTo( MachineState o ) {

            return Integer.compare( this.getGeodePile(), o.getGeodePile() );
        }

        int getGeodePile() {
            return state[GEODE.ordinal()][PILE.ordinal()];
        }

        int getCost( Material material ) {
            return state[robotToBuild.ordinal()][material.ordinal()];
        }

        int getPile( Material material ) {
            return state[material.ordinal()][PILE.ordinal()];
        }

        int getProd( Material material ) {
            return state[material.ordinal()][PROD.ordinal()];
        }

        void produce( Material material ) {
            state[material.ordinal()][PILE.ordinal()] = getPile( material ) + getProd( material );
        }

        void pay( Material material ) {
            state[material.ordinal()][PILE.ordinal()] = getPile( material ) - getCost( material );

        }

    }

}
