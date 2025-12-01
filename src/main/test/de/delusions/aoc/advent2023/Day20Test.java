package de.delusions.aoc.advent2023;

import de.delusions.aoc.advent2023.Day20;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.delusions.aoc.advent2023.Day20.*;
import static de.delusions.aoc.advent2023.Day20.Pulse.HIGH;
import static de.delusions.aoc.advent2023.Day20.Pulse.LOW;
import static org.assertj.core.api.Assertions.assertThat;

public class Day20Test {

    @Test
    public void testButton() {
        Button button = new Button();
        assertThat( button.receivers ).containsExactly( BROADCASTER );
        List<PulseState> pulseStates = button.sendAndGetReceivers( null, null );
        assertThat( pulseStates.stream().allMatch( p -> p.p() == LOW ) ).isTrue();
        assertThat( button.getLow() ).isEqualTo( 1L );
        assertThat( button.getHigh() ).isEqualTo( 0L );

    }

    @Test
    public void testBroadaster() {
        Broadcaster broadcaster = new Broadcaster( List.of( "a", "b", "c" ) );
        assertThat( broadcaster.receivers ).containsExactly( "a", "b", "c" );
        List<PulseState> pulseStates = broadcaster.sendAndGetReceivers( "button", LOW );
        assertThat( pulseStates.stream().allMatch( p -> p.p() == LOW ) ).isTrue();
        assertThat( pulseStates.stream().map( PulseState::to ).toList() ).containsExactly( "a", "b", "c" );
        assertThat( pulseStates.stream().allMatch( p -> p.from().equals( broadcaster.id ) ) ).isTrue();
        assertThat( pulseStates.size() ).isEqualTo( 3 );
        assertThat( broadcaster.getLow() ).isEqualTo( 3L );
        assertThat( broadcaster.getHigh() ).isEqualTo( 0L );
    }

    @Test
    public void testConjunction() {
        Conjunction conjunction = new Conjunction( "inv", List.of( "a" ) );
        assertThat( conjunction.inputs ).isEmpty();
        List<PulseState> pulseStates = conjunction.sendAndGetReceivers( "broadcaster", LOW );
        assertThat( conjunction.inputs.size() ).isEqualTo( 1 );
        assertThat( pulseStates.getFirst() ).isEqualTo( new PulseState( "inv", "a", HIGH ) );
        assertThat( conjunction.getLow() ).isEqualTo( 0L );
        assertThat( conjunction.getHigh() ).isEqualTo( 1L );
        pulseStates = conjunction.sendAndGetReceivers( "b", LOW );
        assertThat( conjunction.inputs.size() ).isEqualTo( 2 );
        assertThat( pulseStates.getFirst() ).isEqualTo( new PulseState( "inv", "a", HIGH ) );
        assertThat( conjunction.getLow() ).isEqualTo( 0L );
        assertThat( conjunction.getHigh() ).isEqualTo( 2L );
        pulseStates = conjunction.sendAndGetReceivers( "b", HIGH );
        assertThat( conjunction.inputs.size() ).isEqualTo( 2 );
        assertThat( pulseStates.getFirst() ).isEqualTo( new PulseState( "inv", "a", HIGH ) );
        assertThat( conjunction.getLow() ).isEqualTo( 0L );
        assertThat( conjunction.getHigh() ).isEqualTo( 3L );
        pulseStates = conjunction.sendAndGetReceivers( "broadcaster", HIGH );
        assertThat( pulseStates.getFirst() ).isEqualTo( new PulseState( "inv", "a", LOW ) );
        assertThat( conjunction.getLow() ).isEqualTo( 1L );
        assertThat( conjunction.getHigh() ).isEqualTo( 3L );

    }

    @Test
    public void testFlipFlop() {
        FlipFlop flipFlop = new FlipFlop( "a", List.of( "b" ) );
        assertThat( flipFlop.receivers ).containsExactly( "b" );
        assertThat( flipFlop.state ).isFalse();
        List<PulseState> pulseStates = flipFlop.sendAndGetReceivers( "broadcaster", HIGH );
        assertThat( flipFlop.state ).isFalse();
        assertThat( flipFlop.getLow() ).isEqualTo( 0L );
        assertThat( flipFlop.getHigh() ).isEqualTo( 0L );
        pulseStates = flipFlop.sendAndGetReceivers( "c", LOW );
        assertThat( flipFlop.state ).isTrue();
        assertThat( flipFlop.getLow() ).isEqualTo( 0L );
        assertThat( flipFlop.getHigh() ).isEqualTo( 1L );
        pulseStates = flipFlop.sendAndGetReceivers( "c", HIGH );
        assertThat( flipFlop.state ).isTrue();
        assertThat( flipFlop.getLow() ).isEqualTo( 0L );
        assertThat( flipFlop.getHigh() ).isEqualTo( 1L );
        pulseStates = flipFlop.sendAndGetReceivers( "c", LOW );
        assertThat( flipFlop.state ).isFalse();
        assertThat( flipFlop.getLow() ).isEqualTo( 1L );
        assertThat( flipFlop.getHigh() ).isEqualTo( 1L );

    }

    @Test
    public void testEndpoint() {
        Endpoint endpoint = new Endpoint( "end" );
        assertThat( endpoint.receivers ).isEmpty();
        List<PulseState> pulseStates = endpoint.sendAndGetReceivers( "a", LOW );
        assertThat( pulseStates ).isEmpty();
    }

    @Test
    public void testButtonPress() {
        Map<String, Day20.Module> moduleMap = new HashMap<>();
        moduleMap.put( "button", new Button() );
        moduleMap.put( "broadcaster", new Broadcaster( List.of( "a" ) ) );
        moduleMap.put( "a", new FlipFlop( "a", List.of( "inv", "con" ) ) );
        moduleMap.put( "inv", new Conjunction( "inv", List.of( "b" ) ).addInput( "a" ) );
        moduleMap.put( "b", new FlipFlop( "b", List.of( "con" ) ) );
        moduleMap.put( "con", new Conjunction( "con", List.of( "output" ) ).addInput( "a" ).addInput( "b" ) );
        moduleMap.put( "output", new Endpoint( "output" ) );

        new Day20().pressButton( moduleMap, mm -> mm.get( "button" ).getLow() < 1 );
        assertThat( moduleMap.get( "button" ).getLow() ).isEqualTo( 1 );
        assertThat( moduleMap.get( "button" ).getHigh() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "broadcaster" ).getLow() ).isEqualTo( 1 );
        assertThat( moduleMap.get( "broadcaster" ).getHigh() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "a" ).getLow() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "a" ).getHigh() ).isEqualTo( 2 );
        assertThat( moduleMap.get( "b" ).getLow() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "inv" ).getLow() ).isEqualTo( 1 );
        assertThat( moduleMap.get( "inv" ).getHigh() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "output" ).getLow() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "output" ).getHigh() ).isEqualTo( 0 );
        assertThat( moduleMap.get( "b" ).getHigh() ).isEqualTo( 1 );
        assertThat( moduleMap.get( "con" ).getLow() ).isEqualTo( 1 );
        assertThat( moduleMap.get( "con" ).getHigh() ).isEqualTo( 1 );

    }
}