package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final int TIME = 24;

    private static final Pattern PATTERN = Pattern.compile( INPUT_REGEX );

    Integer solve( List<Blueprint> blueprints ) {
        //TODO I am using a limit, dont forget
        return blueprints.stream().limit( 2 ).map( this::runBluePrint ).reduce( 0, Integer::sum );
    }

    /**
     * Runs a search for the best run for the given blueprint. Choices are in each round which bot to build next (or keep processing until one was
     * built)
     *
     * @param blueprint the blueprint to start with
     * @return the highest geode count that could be harvested with this blueprint
     */
    int runBluePrint( Blueprint blueprint ) {
        Stack<MachineState> opens = new Stack<>();
        Set<MachineState> candidates = new HashSet<>();
        opens.addAll( List.of( new MachineState( blueprint, ORE ), new MachineState( blueprint, CLAY ) ) );
        for ( int time = TIME; time >= 0; time-- ) {
            System.out.println( time + ": opens " + opens.size() );
            while ( !opens.isEmpty() ) {
                final int currentTimeLeft = time;
                MachineState state = opens.pop();
                if ( state.isValidChoice( currentTimeLeft ) ) {
                    state.produce( 1 );
                    if ( state.isReadyToBuild() ) {
                        state.build();
                    }
                    List<MachineState> machineStates = new ArrayList<>();
                    if ( state.robotToBuild == null && !state.doneBuilding ) { //for the new states copy the matrix!
                        machineStates.addAll( Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).map( next -> state.copyMachine( next ) )
                                                  .filter( m1 -> m1.isValidChoice( currentTimeLeft ) ).toList() );
                    }
                    else {
                        machineStates.add( state );
                    }
                    machineStates.forEach( m -> m.checkDone( currentTimeLeft ) );
                    //TODO: prune machineStates against candidates and remove as needed
                    candidates.addAll( machineStates );
                }
            }
            if ( time > 0 ) {
                opens.addAll( candidates );
                candidates.clear();
            }
        }
        MachineState bestRun = candidates.stream().sorted().findFirst().orElse( null );

        int result = bestRun != null ? bestRun.getGeodePile() * blueprint.blueprintId : 0;
        System.out.println( "-------> " + result + " " + bestRun );
        return result;
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

    enum Material {GEODE, OBSIDIAN, CLAY, ORE, PROD, PILE}

    record Blueprint(int blueprintId, int oreRobotCostOre, int clayRobotCostOre, int obsidianRobotOre, int obsidianRobotClay, int geodeRobotOre,
                     int geodeRobotObsidian) {}

    static class MachineState implements Comparable<MachineState> {
        static final int CANNOT_BUILD = 1000;

        boolean doneBuilding = false;

        Material robotToBuild = ORE;

        int[][] state;

        MachineState( Blueprint blueprint ) {
            this( initState( blueprint ), ORE, false );
        }

        MachineState( Blueprint blueprint, Material robotToBuild ) {
            this( initState( blueprint ), robotToBuild, false );
        }

        MachineState( int[][] state, Material robotToBuild, boolean doneBuilding ) {
            this.state = state;
            this.robotToBuild = robotToBuild;
            this.doneBuilding = doneBuilding;
        }

        private static int[][] initState( Blueprint blueprint ) {
            int[][] initState = new int[Material.values().length - 2][Material.values().length];
            initState[ORE.ordinal()][ORE.ordinal()] = blueprint.oreRobotCostOre;
            initState[CLAY.ordinal()][ORE.ordinal()] = blueprint.clayRobotCostOre;
            initState[OBSIDIAN.ordinal()][ORE.ordinal()] = blueprint.obsidianRobotOre;
            initState[OBSIDIAN.ordinal()][CLAY.ordinal()] = blueprint.obsidianRobotClay;
            initState[GEODE.ordinal()][ORE.ordinal()] = blueprint.geodeRobotOre;
            initState[GEODE.ordinal()][OBSIDIAN.ordinal()] = blueprint.geodeRobotObsidian;
            initState[ORE.ordinal()][PROD.ordinal()] = 1;
            return initState;
        }

        int steps( int cost, int pile, int prod ) {
            int needed = cost - pile;
            if ( prod == 0 && cost > 0 ) {
                return CANNOT_BUILD;
            }
            return prod == 0 || needed <= 0 ? 0 : needed / prod + ( needed % prod == 0 ? 0 : 1 );
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

        void setPile( Material material, int value ) {
            state[material.ordinal()][PILE.ordinal()] = value;
        }

        int getProd( Material material ) {
            return state[material.ordinal()][PROD.ordinal()];
        }

        void setProduction( Material material, int value ) {
            state[material.ordinal()][PROD.ordinal()] = value;
        }

        void produce( Material material ) {
            produce( material, 1 );
        }

        void produce( Material material, int steps ) {
            state[material.ordinal()][PILE.ordinal()] = getPile( material ) + steps * getProd( material );
        }

        void produce( int steps ) {
            for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) {
                produce( material, steps );
            }
        }

        /**
         * Checks if for all materials the cost for the desired robot is "covered" by the existing stockpile
         *
         * @return true if all costs can be covered, false if there is not enough of at least 1 material to pay for the bot (cost > pile)
         */
        boolean isReadyToBuild() {
            return Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).filter( material -> getCost( material ) > getPile( material ) ).findFirst().isEmpty();
        }

        void pay( Material material ) {
            state[material.ordinal()][PILE.ordinal()] = getPile( material ) - getCost( material );
        }

        boolean build() {
            if ( isReadyToBuild() && !doneBuilding ) {
                for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) {
                    pay( material );
                }
                setProduction( robotToBuild, getProd( robotToBuild ) + 1 );
                this.robotToBuild = null;
            }
            return robotToBuild == null;
        }

        int stepsUntilBuild() {
            return Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).map( m -> steps( getCost( m ), getPile( m ), getProd( m ) ) ).max( Integer::compareTo )
                .orElse( -1 );
        }

        void checkDone( int timeLeft ) {
            if ( stepsUntilBuild() > timeLeft - 1 ) {
                doneBuilding = true;
            }
        }

        boolean isValidChoice( int timeLeft ) {
            boolean result = false;
            if ( robotToBuild == ORE ) {
                int oreCostOre = getCost( ORE );
                int[] oreCosts =
                    {state[CLAY.ordinal()][ORE.ordinal()], state[OBSIDIAN.ordinal()][ORE.ordinal()], state[GEODE.ordinal()][ORE.ordinal()]};
                int maxCost = Arrays.stream( oreCosts ).max().getAsInt();
                boolean oreIsMostExpensive = oreCostOre > maxCost; //this makes it an invalid choice
                result = !oreIsMostExpensive && ( getProd( ORE ) <= maxCost / oreCostOre );
            }
            else if ( robotToBuild == CLAY ) {
                result = true;
            }
            else {
                int obsProd = getProd( OBSIDIAN );
                if ( robotToBuild == OBSIDIAN ) {
                    int obsCost = state[GEODE.ordinal()][OBSIDIAN.ordinal()];
                    int steps = stepsUntilBuild();
                    boolean lohntNoch = ( obsCost - getPile( OBSIDIAN ) - steps * obsProd ) / ( obsProd + 1 ) + steps < timeLeft;
                    result = getProd( CLAY ) > 0 && lohntNoch;
                }
                else if ( robotToBuild == GEODE ) {
                    result = obsProd > 0 && stepsUntilBuild() < timeLeft;
                }
            }
            return result;
        }


        MachineState copyMachine( Material robotToBuild ) {
            int[][] copy = new int[4][6]; //because I can
            for ( int x = 0; x < 4; x++ ) {
                System.arraycopy( state[x], 0, copy[x], 0, 6 );
            }
            return new MachineState( copy, robotToBuild, doneBuilding );
        }

        @Override
        public int compareTo( MachineState o ) {
            return Integer.compare( o.getGeodePile(), getGeodePile() );
        }

        @Override
        public String toString() {
            return String.format( "Machine oreBots=%s clayBots=%s obsBots=%s geoBots=%s | geodes=%s", getProd( ORE ), getProd( CLAY ),
                                  getProd( OBSIDIAN ), getProd( GEODE ), getPile( GEODE ) );
        }
    }

}
