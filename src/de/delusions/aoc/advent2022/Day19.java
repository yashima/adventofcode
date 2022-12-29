package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        return blueprints.stream().map( this::runBluePrint ).reduce( 0, Integer::sum );
    }

    /**
     * Runs a search for the best run for the given blueprint. Choices are in each round which bot to beginBuild next (or keep processing until one
     * was built)
     *
     * @param blueprint the blueprint to start with
     * @return the highest geode count that could be harvested with this blueprint
     */
    int runBluePrint( Blueprint blueprint ) {
        //init the first round
        Stack<MachineState> opens = new Stack<>();
        opens.addAll( List.of( new MachineState( blueprint, ORE ), new MachineState( blueprint, CLAY ) ) );
        //track various states
        Map<String, MachineState> candidates = new HashMap<>();
        List<MachineState> waiting = new ArrayList<>();
        List<MachineState> done = new ArrayList<>();

        //the time loop
        for ( int time = TIME; time >= 0; time-- ) {
            System.out.println( time + ": opens " + opens.size() + "| done " + done.size() );
            final int currentTime = time;
            //check all the states for this round
            while ( !opens.isEmpty() ) {
                MachineState current = opens.pop();
                List<MachineState> machineStates = current.run( time );
                if ( machineStates.isEmpty() ) {
                    //no followers, we're done
                    current.produce( time - 1 );
                    done.add( current );
                }
                else if ( machineStates.size() == 1 && machineStates.get( 0 ) == current ) {
                    //only self: we're waiting to produce
                    waiting.add( current );
                }
                else {
                    //check the candidates against existing ones with the magic number
                    machineStates.forEach( candidate -> {
                        String identifier = candidate.getIdentifier();
                        if ( !candidates.containsKey( identifier ) ||
                            ( candidates.get( identifier ).getMagicNumber( currentTime - 1 ) < candidate.getMagicNumber( currentTime - 1 ) ) ) {
                            candidates.put( identifier, candidate );
                        }
                    } );
                }
            }
            //clean up and go again
            if ( time > 0 ) {
                opens.addAll( waiting );
                opens.addAll( candidates.values() );
                waiting.clear();
                candidates.clear();
            }
        }

        MachineState bestRun = done.stream().sorted().findFirst().orElse( null );

        int result;
        result = bestRun != null ? bestRun.getPile( GEODE ) * blueprint.blueprintId : 0;
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

        boolean doneBuilding;

        Material robotToBuild;

        int[][] state;

        int totalValue;

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
            this.totalValue = 0;
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

        int steps( int cost, Material material ) {
            return steps( cost, getPile( material ), getProd( material ) );
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

        void setPile( Material material, int value ) {
            state[material.ordinal()][PILE.ordinal()] = value;
        }

        void setProduction( Material material, int value ) {
            state[material.ordinal()][PROD.ordinal()] = value;
        }

        void produce( Material material, int steps ) {
            int produced = steps * getProd( material );
            state[material.ordinal()][PILE.ordinal()] = getPile( material ) + produced;
            totalValue = totalValue + produced * getMaterialValue( material );
        }

        int getMagicNumber( int timeLeft ) {
            return totalValue;
            //  Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).map( m -> getMaterialValue( m ) * getProd( m ) * timeLeft ).reduce( 0, Integer::sum );
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
            Optional<Material> costNotCovered =
                Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).filter( material -> getCost( material ) > getPile( material ) ).findFirst();
            return costNotCovered.isEmpty();
        }

        void pay( Material material ) {
            int pile = getPile( material );
            int cost = getCost( material );
            if ( cost > pile ) {throw new IllegalStateException( "Not enough " + material );}
            state[material.ordinal()][PILE.ordinal()] = pile - cost;
        }

        int getMaterialValue( Material material ) {
            return switch ( material ) {
                case ORE -> 1;
                case CLAY -> state[CLAY.ordinal()][ORE.ordinal()];
                case OBSIDIAN -> state[OBSIDIAN.ordinal()][ORE.ordinal()] + state[OBSIDIAN.ordinal()][CLAY.ordinal()] * getMaterialValue( CLAY );
                case GEODE -> state[GEODE.ordinal()][ORE.ordinal()] + state[GEODE.ordinal()][OBSIDIAN.ordinal()] * getMaterialValue( OBSIDIAN );
                case PILE -> 0;
                case PROD -> 0;
            };
        }


        boolean beginBuild() {
            if ( isReadyToBuild() ) {
                for ( Material material : List.of( ORE, CLAY, OBSIDIAN, GEODE ) ) {
                    pay( material );
                }
                return true;
            }
            return false;
        }

        void finishBuild() {
            setProduction( robotToBuild, getProd( robotToBuild ) + 1 );
            this.robotToBuild = null;
        }

        int stepsUntilBuild() {
            return Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).map( m -> steps( getCost( m ), m ) ).max( Integer::compareTo ).orElse( -1 );
        }

        int stepsUntilBuild( Material bot ) {
            return Stream.of( ORE, CLAY, OBSIDIAN, GEODE ).map( m -> steps( state[bot.ordinal()][m.ordinal()], m ) ).max( Integer::compareTo )
                .orElse( -1 );
        }

        boolean isInTime( Material bot, int timeLeft ) {
            return stepsUntilBuild( bot ) < timeLeft - 1; //this includes prerequisite production check
        }

        boolean isValid( Material bot, int timeLeft ) {
            return true; //TODO find heuristics that limit choices.
        }

        List<Material> getNextChoices( int timeLeft ) {
            List<Material> materials = Stream.of( ORE, CLAY, OBSIDIAN, GEODE )//
                .filter( bot -> isInTime( bot, timeLeft ) )//
                .filter( bot -> isValid( bot, timeLeft ) )//
                .toList();
            if ( materials.isEmpty() ) {
                doneBuilding = true;
            }
            return materials;
        }

        MachineState copyMachine( Material robotToBuild ) {
            int[][] copy = new int[4][6]; //because I can
            for ( int x = 0; x < 4; x++ ) {
                System.arraycopy( state[x], 0, copy[x], 0, 6 );
            }
            return new MachineState( copy, robotToBuild, doneBuilding );
        }

        List<MachineState> run( int timeLeft ) {
            List<MachineState> next = new ArrayList<>();
            boolean build = isReadyToBuild();
            if ( build ) {
                beginBuild();
            }
            produce( 1 );
            if ( build ) {
                finishBuild();
                next.addAll( getNextChoices( timeLeft ).stream().map( this::copyMachine ).toList() );
            }
            else {
                next.add( this );
            }
            return next;
        }


        String getIdentifier() {
            return String.format( "%s|%s|%s|%s|%s", robotToBuild, getProd( ORE ), getProd( CLAY ), getProd( OBSIDIAN ), getProd( GEODE ) );
        }

        @Override
        public int compareTo( MachineState o ) {
            return Integer.compare( o.getPile( GEODE ), getPile( GEODE ) );
        }

        @Override
        public String toString() {
            return String.format( "Machine oreBots=%s clayBots=%s obsBots=%s geoBots=%s | geodes=%s", getProd( ORE ), getProd( CLAY ),
                                  getProd( OBSIDIAN ), getProd( GEODE ), getPile( GEODE ) );
        }
    }

}
