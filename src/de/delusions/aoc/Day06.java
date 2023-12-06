package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day06 extends Day<Long> {

    public Day06( Long... expected ) {
        super( 6, "Wait For It", expected );
    }

    @Override
    public Long part0( Stream<String> input ) {
        List<Race> races = readRaceData( input );
        return races.stream().mapToLong( Race::calculateWinningMoves ).reduce( ( a, b ) -> a * b ).orElse( -1 );
    }

    @Override
    public Long part1( Stream<String> input ) {
        Race race = readRaceDataPart1( input );
        return race.calculateWinningMoves();
    }

    private List<Race> readRaceData( Stream<String> input ) {
        List<String[]> rawData = input.map( line -> line.substring( line.indexOf( ":" ) + 1 ).trim().split( "\\s+" ) ).toList();
        List<Long> times = Arrays.stream( rawData.get( 0 ) ).map( Long::parseLong ).toList();
        List<Long> distances = Arrays.stream( rawData.get( 1 ) ).map( Long::parseLong ).toList();
        List<Race> result = new ArrayList<>();
        for ( int idx = 0; idx < times.size(); idx++ ) {
            result.add( new Race( times.get( idx ), distances.get( idx ), 1 ) );
        }
        return result;
    }

    private Race readRaceDataPart1( Stream<String> input ) {
        List<String> lines = input.toList();
        return new Race( extractNumber( lines.get( 0 ) ), extractNumber( lines.get( 1 ) ), 1 );
    }

    private long extractNumber( String line ) {
        return Long.parseLong( line.substring( line.indexOf( ":" ) + 1 ).replaceAll( " ", "" ) );
    }

    record Race(long time, long distance, long speed) {
        long calculateWinningMoves() {
            long press = 0;
            while ( loses( press ) ) {
                press++;
            }
            long lower = press;
            press = time;
            while ( loses( press ) ) {
                press--;
            }
            long upper = press;
            return upper - lower + 1;
        }

        boolean loses( long press ) {
            return distance >= ( time - press ) * ( press * speed );
        }
    }
}
