package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19 extends Day<Integer> {

    private static final String INPUT_REGEX =
        "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";

    private static final int TIME = 24;

    private static final Pattern PATTERN = Pattern.compile( INPUT_REGEX );

    Integer solve( List<Blueprint> blueprints ) {
        return blueprints.stream().map( this::runBluePrint ).reduce( 0, Integer::sum );
    }

    public Day19() { //2153 was too low
        super( 19, "Not Enough Minerals" );
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
        opens.add( new MachineState( 0, 1, 0, 0, 0, 0, 0, 0, 0 ) );
        //track various states
        Map<String, MachineState> candidates = new HashMap<>();
        List<MachineState> done = new ArrayList<>();

        //the time loop
        while ( !opens.isEmpty() ) {
            MachineState current = opens.pop();
            if ( current.time == TIME ) {
                done.add( current ); //weed out those that are worse
            }
            else {
                List<MachineState> next = current.getNext( blueprint );
                next.forEach( state -> {
                    if ( !candidates.containsKey( state.key() ) ||
                        candidates.get( state.key() ).totalValue( blueprint ) < state.totalValue( blueprint ) ) {
                        candidates.put( state.key(), state );
                        opens.add( state );
                    }
                } );
            }

        }

        MachineState bestRun = done.stream().sorted().findFirst().orElse( null );
        //done.stream().map(m -> m.geoPile).max( Integer::compareTo ).get();
        int result;
        result = bestRun != null ? bestRun.geoPile * blueprint.blueprintId : 0;
        System.out.println( "-------> " + result + " " + bestRun + " " + blueprint );
        return result;
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

    record Blueprint(int blueprintId, int oreCOre, int clayCOre, int obsCOre, int obsCClay, int geoCOre, int geoCObs) {}

    record MachineState(int time, int oreProd, int clayProd, int obsProd, int geoProd, int orePile, int clayPile, int obsPile, int geoPile)
        implements Comparable<MachineState> {
        MachineState doNothing() {
            return new MachineState( time + 1, oreProd, clayProd, obsProd, geoProd, orePile + oreProd, clayPile + clayProd, obsPile + obsProd,
                                     geoPile + geoProd );
        }

        MachineState buildOre( Blueprint blueprint ) {
            return new MachineState( time + 1, oreProd + 1, clayProd, obsProd, geoProd, orePile + oreProd - blueprint.oreCOre, clayPile + clayProd,
                                     obsPile + obsProd, geoPile + geoProd );
        }

        MachineState buildClay( Blueprint blueprint ) {
            return new MachineState( time + 1, oreProd, clayProd + 1, obsProd, geoProd, orePile + oreProd - blueprint.clayCOre, clayPile + clayProd,
                                     obsPile + obsProd, geoPile + geoProd );
        }

        MachineState buildObsidian( Blueprint blueprint ) {
            return new MachineState( time + 1, oreProd, clayProd, obsProd + 1, geoProd, orePile + oreProd - blueprint.obsCOre,
                                     clayPile + clayProd - blueprint.obsCClay, obsPile + obsProd, geoPile + geoProd );
        }

        MachineState buildGeode( Blueprint blueprint ) {
            return new MachineState( time + 1, oreProd, clayProd, obsProd, geoProd + 1, orePile + oreProd - blueprint.geoCOre, clayPile + clayProd,
                                     obsPile + obsProd - blueprint.geoCObs, geoPile + geoProd );
        }

        boolean canBuildOre( Blueprint blueprint ) {
            return orePile >= blueprint.oreCOre;
        }

        boolean canBuildClay( Blueprint blueprint ) {
            return orePile >= blueprint.clayCOre;
        }

        boolean canBuildObsidian( Blueprint blueprint ) {
            return orePile >= blueprint.obsCOre && clayPile >= blueprint.obsCClay;
        }

        boolean canBuildGeode( Blueprint blueprint ) {
            return orePile >= blueprint.geoCOre && obsPile >= blueprint.geoCObs;
        }

        String key() {return String.format( "%s,%s,%s,%s,%s", time, oreProd, clayProd, obsProd, geoProd );}

        int totalValue( Blueprint blueprint ) {
            return ( orePile + oreProd ) * blueprint.oreCOre  //
                + ( clayPile + clayProd ) * blueprint.clayCOre   //
                + ( obsPile + obsProd ) * ( blueprint.obsCOre + blueprint.obsCClay * blueprint.clayCOre ) //
                + ( geoPile + geoProd ) * ( blueprint.geoCOre + blueprint.geoCObs * blueprint.obsCClay * blueprint.clayCOre ); //
        }

        List<MachineState> getNext( Blueprint blueprint ) {
            List<MachineState> machineStates = new ArrayList<>();
            if ( canBuildGeode( blueprint ) ) {
                machineStates.add( buildGeode( blueprint ) );
            }
            if ( canBuildObsidian( blueprint ) ) {
                machineStates.add( buildObsidian( blueprint ) );
            }
            if ( canBuildClay( blueprint ) ) {
                machineStates.add( buildClay( blueprint ) );
            }
            if ( canBuildOre( blueprint ) ) {
                machineStates.add( buildOre( blueprint ) );
            }
            machineStates.add( doNothing() );
            return machineStates;
        }

        @Override
        public int compareTo( MachineState o ) {
            return Integer.compare( o.geoPile, this.geoPile );
        }
    }


}
