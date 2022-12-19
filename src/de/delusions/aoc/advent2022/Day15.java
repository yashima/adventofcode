package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Coordinates;
import de.delusions.aoc.util.Day;
import de.delusions.aoc.util.Interval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day15
    extends Day<Integer> {
    record SensorBeacon(Coordinates sensor, Coordinates beacon) {
    }

    static int MAX_DIM = 4000000;

    Day15() {
        super( 15, "Beacon Exclusion Zone" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        long time = System.currentTimeMillis();
        int size = collectCoveredIntervals( parse( input ), 2000000, true ).stream().map( Interval::length ).reduce( 0, Integer::sum );
        System.out.println( "This took : " + ( System.currentTimeMillis() - time ) + "ms" );
        return size;
    }

    @Override
    public Integer part2( Stream<String> input ) {
        long time = System.currentTimeMillis();
        List<SensorBeacon> data = parse( input );
        Coordinates distressBeacon = null;
        for ( int y = 0; y <= MAX_DIM; y++ ) {
            List<Interval> coveredCoordinates = collectCoveredIntervals( data, y, false );
            if ( coveredCoordinates.size() > 1 ) {
                distressBeacon = new Coordinates( coveredCoordinates.get( 0 ).getUpper() + 1, y );
                break;
            }
        }
        return BigInteger.valueOf( distressBeacon.x ).multiply( BigInteger.valueOf( 4000000 ) ).add(
            BigInteger.valueOf( distressBeacon.y ) ).intValue();
    }

    List<SensorBeacon> parse( Stream<String> input ) {
        Pattern compile = Pattern.compile( "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)" );

        List<SensorBeacon> data = input.map( line -> {
            Matcher m = compile.matcher( line );
            return m.matches() ? new SensorBeacon( new Coordinates( new String[]{m.group( 1 ), m.group( 2 )}, 3 ),
                                                   new Coordinates( new String[]{m.group( 3 ), m.group( 4 )}, 4 ) ) : null;
        } ).filter( Objects::nonNull ).toList();
        return data;
    }

    List<Interval> collectCoveredIntervals( List<SensorBeacon> sensorBeacons, int y, boolean noMaxDim ) {
        LinkedList<Interval> intervals = new LinkedList<>( sensorBeacons.stream()//
                                                               .map( sb -> findCoveredInterval( sb.sensor, sb.beacon, y, noMaxDim ) )//
                                                               .filter( Objects::nonNull ).sorted().toList() );
        List<Interval> result = new ArrayList<>();
        if ( intervals.isEmpty() ) {
            return result;
        }
        Interval current = intervals.pop();
        while ( !intervals.isEmpty() ) {
            Interval next = intervals.pop();
            if ( current.overlap( next ) ) {
                current = current.union( next );
            }
            else {
                result.add( current );
                current = next;
            }
        }
        result.add( current );
        return result;
    }

    Interval findCoveredInterval( Coordinates sensor, Coordinates beacon, int yToCheck, boolean noMaxDim ) {
        int refDistance = sensor.manhattanDistance( new Coordinates( sensor.x, yToCheck ) );
        int beaconDist = sensor.manhattanDistance( beacon );
        if ( refDistance < beaconDist ) {
            int radius = beaconDist - refDistance;
            int lower = sensor.x - radius;
            int upper = sensor.x + radius;
            return new Interval( noMaxDim ? lower : Math.max( 0, lower ), noMaxDim ? upper : Math.min( MAX_DIM, upper ) );
        }
        return null;
    }
}
