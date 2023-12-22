package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.Day20.Pulse.HIGH;
import static de.delusions.aoc.Day20.Pulse.LOW;

public class Day20 extends Day<Long> {

    public static final String BUTTON = "button";

    public static final String BROADCASTER = "broadcaster";

    public Day20( Long... expected ) {super( 20, "Pulse Propagations", expected );}

    Map<String, Module> moduleMap = new HashMap<>();

    Pattern p = Pattern.compile( "^([%&])*([a-z]+) -> (.*)$" );

    @Override
    public Long part0( Stream<String> input ) {
        parseInput( input );
        pressButton( 1000 );
        long lowSum = moduleMap.values().stream().mapToLong( Module::getLow ).sum();
        long highSum = moduleMap.values().stream().mapToLong( Module::getHigh ).sum();
        return lowSum * highSum;
    }

    @Override
    public Long part1( Stream<String> input ) {
        parseInput( input );
        return null;
    }

    private void parseInput( Stream<String> input ) {
        moduleMap.clear();

        input.forEach( line -> {
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                char type = m.group( 1 ) == null ? '0' : m.group( 1 ).charAt( 0 );
                String id = m.group( 2 );
                List<String> receivers = Arrays.stream( m.group( 3 ).split( "," ) ).map( String::trim ).toList();
                switch ( type ) {
                    case '&':
                        moduleAdd( new Conjunction( id, receivers ) );
                    case '%':
                        moduleAdd( new FlipFlop( id, receivers ) );
                    default:
                        moduleAdd( new Broadcaster( receivers ) );
                }
            }
            else {
                System.err.println( "No match: " + line );
            }
        } );

        //add start
        moduleAdd( new Button() );

        //find any modules referenced as receivers but not defined:
        List<Endpoint> endpoints = moduleMap.values()
                                            .stream()
                                            .map( Module::getReceivers )
                                            .flatMap( List::stream )
                                            .filter( m -> !moduleMap.containsKey( m ) )
                                            .distinct()
                                            .map( Endpoint::new )
                                            .toList();
        //needs to be after the processing or concurrent modification:
        endpoints.forEach( this::moduleAdd );
    }

    private void pressButton( int buttonPresses ) {
        Stack<PulseState> stack = new Stack<>();

        while ( !stack.isEmpty() || moduleMap.get( BUTTON ).getLow() <= buttonPresses ) {
            if ( stack.isEmpty() ) {
                stack.push( new PulseState( null, BUTTON, LOW ) );
            }
            PulseState current = stack.pop();
            Module module = moduleMap.get( current.to );
            if ( module == null ) {
                throw new IllegalStateException( "Module '" + current.to + "' not found" );
            }
            List<PulseState> next = module.sendAndGetReceivers( current.from, current.p );
            stack.addAll( next );
        }
    }

    void moduleAdd( Module module ) {
        this.moduleMap.put( module.id, module );
    }

    enum Pulse {HIGH, LOW}

    record PulseState(String from, String to, Pulse p) {}

    static class Button extends Module {

        Button() {
            super( BUTTON, List.of( BROADCASTER ) );
        }

        @Override
        Pulse receiveAndSend( String from, Pulse receive ) {
            return LOW;
        }
    }

    static class Conjunction extends Module {

        Map<String, Pulse> inputs = new HashMap<>();

        Conjunction( String id, List<String> targets ) {
            super( id, targets );
        }

        @Override
        Pulse receiveAndSend( String from, Pulse receive ) {
            inputs.put( from, receive );
            return inputs.values().stream().allMatch( p -> p == HIGH ) ? LOW : HIGH;
        }
    }

    static class FlipFlop extends Module {
        boolean state = false;

        FlipFlop( String id, List<String> targets ) {
            super( id, targets );
        }

        Pulse receiveAndSend( String from, Pulse receive ) {
            if ( receive == HIGH ) {
                return null;
            }
            state = !state;
            return state ? HIGH : LOW;
        }
    }

    static class Broadcaster extends Module {

        Broadcaster( List<String> targets ) {
            super( BROADCASTER, targets );
        }

        @Override
        Pulse receiveAndSend( String from, Pulse receive ) {
            return receive;
        }
    }

    static class Endpoint extends Module {

        Endpoint( String id ) {
            super( id, List.of() );
        }

        @Override
        Pulse receiveAndSend( String from, Pulse receive ) {
            return null;
        }
    }


    abstract static class Module {
        final String id;

        final List<String> receivers;

        final AtomicLong lowCount = new AtomicLong( 0 );

        final AtomicLong highCount = new AtomicLong( 0 );

        Module( String id, List<String> receivers ) {
            this.id = id;
            this.receivers = receivers;
        }

        long getLow() {
            return lowCount.get();
        }

        long getHigh() {
            return highCount.get();
        }

        public List<String> getReceivers() {
            return receivers;
        }

        List<PulseState> sendAndGetReceivers( String from, Pulse receive ) {
            Pulse send = receiveAndSend( from, receive );
            if ( send == null ) {
                return List.of();
            }
            if ( send == LOW ) {lowCount.addAndGet( receivers.size() );}
            if ( send == HIGH ) {highCount.addAndGet( receivers.size() );}
            return receivers.stream().map( t -> new PulseState( this.id, t, send ) ).toList();
        }

        abstract Pulse receiveAndSend( String from, Pulse receive );

        @Override
        public String toString() {
            return "Module{" + "id='" + id + '\'' + ", targets=" + receivers + ", pulseCounter=" + lowCount + '}';
        }
    }
}
