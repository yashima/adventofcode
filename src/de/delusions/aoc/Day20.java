package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.Day20.Pulse.HIGH;
import static de.delusions.aoc.Day20.Pulse.LOW;

public class Day20 extends Day<Long> {

    public static final String BUTTON = "button";

    public static final String BROADCASTER = "broadcaster";

    public Day20( Long... expected ) {super( 20, "Pulse Propagations", expected );}

    Pattern p = Pattern.compile( "^([%&])*([a-z]+) -> (.*)$" );

    @Override
    public Long part0( Stream<String> input ) {
        Map<String, Module> moduleMap = parseInput( input );
        pressButton( moduleMap, mm -> mm.get( BUTTON ).getLow() < 1000 );
        long lowSum = moduleMap.values().stream().mapToLong( Module::getLow ).sum();
        long highSum = moduleMap.values().stream().mapToLong( Module::getHigh ).sum();
        return lowSum * highSum;
    }

    @Override
    public Long part1( Stream<String> input ) {
        if ( isTestMode() ) {
            return 0L;
        }
        Map<String, Module> moduleMap = parseInput( input );
        pressButton( moduleMap, mm -> mm.get( "rx" ).getLow() != 1 );
        return moduleMap.get( BUTTON ).getLow();
    }

    Map<String, Module> parseInput( Stream<String> input ) {
        Map<String, Module> moduleMap = new HashMap<>();

        input.forEach( line -> {
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                char type = m.group( 1 ) == null ? '0' : m.group( 1 ).charAt( 0 );
                String id = m.group( 2 );
                List<String> receivers = Arrays.stream( m.group( 3 ).split( "," ) ).map( String::trim ).toList();
                switch ( type ) {
                    case '&' -> moduleMap.put( id, new Conjunction( id, receivers ) );
                    case '%' -> moduleMap.put( id, new FlipFlop( id, receivers ) );
                    default -> moduleMap.put( BROADCASTER, new Broadcaster( receivers ) );
                }
            }
            else {
                System.err.println( "No match: " + line );
            }
        } );

        //add start
        moduleMap.put( BUTTON, new Button() );

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
        endpoints.forEach( e -> moduleMap.put( e.id, e ) );

        //now find me all the inputs for all the conjunctions and add them.
        moduleMap.values().stream().filter( m -> m instanceof Conjunction ).forEach( conjunction -> {
            moduleMap.values().stream().filter( in -> in.receivers.contains( conjunction.id ) ).forEach( in -> {
                ( (Conjunction) conjunction ).addInput( in.id );
            } );
        } );

        return moduleMap;
    }

    void pressButton( Map<String, Module> moduleMap, Function<Map<String, Module>, Boolean> keepPressing ) {
        Stack<PulseState> stack = new Stack<>();

        while ( keepPressing.apply( moduleMap ) ) {
            stack.push( new PulseState( null, BUTTON, LOW ) );
            if ( moduleMap.get( BUTTON ).getLow() % 10000 == 0 ) {
                System.out.println( "Nope" );
            }
            while ( !stack.isEmpty() ) {
                PulseState current = stack.pop();
                Module module = moduleMap.get( current.to );
                List<PulseState> next = module.sendAndGetReceivers( current.from, current.p );
                stack.addAll( next );
            }
        }
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

        Conjunction addInput( String input ) {
            inputs.put( input, LOW );
            return this;
        }

        @Override
        Pulse receiveAndSend( String from, Pulse receive ) {
            inputs.put( from, receive );
            return isAllHigh() ? LOW : HIGH;
        }

        boolean isAllHigh() {
            return inputs.values().stream().allMatch( p -> p == HIGH );
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

        public List<PulseState> sendAndGetReceivers( String from, Pulse receive ) {
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
            return "Module{" + "id='" + id + '\'' + ", receivers=" + receivers + ", lowCount=" + lowCount + ", highCount=" + highCount + '}';
        }
    }
}
