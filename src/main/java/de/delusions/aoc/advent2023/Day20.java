package de.delusions.aoc.advent2023;

import de.delusions.util.Day;

import java.util.ArrayList;
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

import static de.delusions.aoc.advent2023.Day20.Pulse.HIGH;
import static de.delusions.aoc.advent2023.Day20.Pulse.LOW;

public class Day20 extends Day<Long> {

    public static final String BUTTON = "button";

    public static final String BROADCASTER = "broadcaster";


    public static final String CONJUNCTION = "rs";


    public Day20( Long... expected ) {super( 20, "Pulse Propagations", expected );}

    final Pattern p = Pattern.compile( "^([%&])*([a-z]+) -> (.*)$" );

    @Override
    public Long part0( Stream<String> input ) {
        Map<String, Module> moduleMap = parseInput( input );
        pressButton( moduleMap, mm -> mm.get( BUTTON ).getLow() < 1000 );
        long lowSum = moduleMap.values().stream().mapToLong( Module::getLow ).sum();
        long highSum = moduleMap.values().stream().mapToLong( Module::getHigh ).sum();
        return lowSum * highSum;
    }

    static final Map<String, List<HighState>> TRACK_HIGH = new HashMap<>();

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
        moduleMap.values().stream().filter( m -> m instanceof Conjunction ).forEach( conjunction -> moduleMap.values().stream().filter(in -> in.receivers.contains( conjunction.id ) ).forEach(in -> ( (Conjunction) conjunction ).addInput( in.id )));

        return moduleMap;
    }

    static final AtomicLong RX_COUNTER = new AtomicLong( 0 );

    @Override
    public Long part1( Stream<String> input ) {
        if ( isTestMode() ) {
            return 0L;
        }
        Map<String, Module> moduleMap = parseInput( input );
        pressButton( moduleMap, mm -> !( TRACK_HIGH.size() == 4 && TRACK_HIGH.values().stream().noneMatch( List::isEmpty ) ) );
        pressButton( moduleMap, mm -> mm.get( BUTTON ).getLow() < 23000 );
        TRACK_HIGH.forEach( ( a, b ) -> System.out.printf( "%s : %s%n", a, b ) );
        List<List<Long>> list = TRACK_HIGH.values().stream().map( l -> l.stream().map( HighState::rxCount ).distinct().sorted().toList() ).toList();
        //System.out.println("RX Cycles "+list);
        List<Long> cycles = TRACK_HIGH.values().stream().map( List::getFirst ).map( HighState::cycle ).toList();
        return cycles.stream().reduce( 1L, ( a, b ) -> a * b );
    }

    void pressButton( Map<String, Module> moduleMap, Function<Map<String, Module>, Boolean> keepPressing ) {
        Stack<PulseState> stack = new Stack<>();

        while ( keepPressing.apply( moduleMap ) ) {
            RX_COUNTER.set( 0 );
            stack.push( new PulseState( null, BUTTON, LOW ) );
            while ( !stack.isEmpty() ) {
                PulseState current = stack.pop();
                Module module = moduleMap.get( current.to );
                if ( CONJUNCTION.equals( module.id ) ) {trackHigh( moduleMap );}
                List<PulseState> next = module.sendAndGetReceivers( current.from, current.p );

                stack.addAll( next );
            }
        }
    }

    void trackHigh( Map<String, Module> moduleMap ) {
        Conjunction rx = (Conjunction) moduleMap.get( CONJUNCTION );
        RX_COUNTER.getAndIncrement();
        long cycle = moduleMap.get( BUTTON ).getLow();
        rx.inputs.forEach( ( key, value ) -> {
            if ( value == HIGH ) {
                Module module = moduleMap.get( key );
                TRACK_HIGH.putIfAbsent( key, new ArrayList<>() );
                TRACK_HIGH.get( key ).add( new HighState( RX_COUNTER.get(), cycle, module.getLow(), module.getHigh() ) );
            }
        } );
    }

    record HighState(long rxCount, long cycle, long low, long high) {}

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

        final Map<String, Pulse> inputs = new HashMap<>();

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
