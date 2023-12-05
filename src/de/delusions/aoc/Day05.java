package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day05 extends Day<Long> {

    List<Long> startingSeeds = new ArrayList<>();

    List<Category> almanach = new ArrayList<>();

    public Day05( Long... expected ) {
        super( 5, "", expected );
    }

    private String printAlmanach() {
        StringBuilder result = new StringBuilder();
        result.append( "\n--- The Almanach ---\n\n" );
        for ( Category category : almanach ) {
            result.append( category.header ).append( "\n" );
            for ( MapLine mapline : category.mapLines ) {
                result.append( mapline ).append( "\n" );
            }
            result.append( "\n" );
        }
        return result.toString();
    }

    private void readAlmanach( Stream<String> input ) {
        startingSeeds = new ArrayList<>();
        almanach = new ArrayList<>();
        List<MapLine> current = new ArrayList<>();
        AtomicReference<String> header = new AtomicReference<>();
        input.forEach( line -> {
            //first line contains the seeds
            if ( startingSeeds.isEmpty() ) {
                String[] seeds = line.substring( line.indexOf( ":" ) + 1 ).trim().split( " " );
                startingSeeds.addAll( Arrays.stream( seeds ).map( Long::parseLong ).toList() );
            }
            else if ( line.contains( ":" ) ) {

                if ( !current.isEmpty() ) {
                    //catch-all that maps values to themselves
                    current.add( new MapLine( null, null ) );
                    almanach.add( new Category( header.get(), new ArrayList<>( current ) ) );
                    current.clear();

                }
                header.set( line );
            }
            else if ( !line.isBlank() ) {
                String[] parts = line.split( " " );
                long destinationLower = Long.parseLong( parts[0] );
                long sourceLower = Long.parseLong( parts[1] );
                long length = Long.parseLong( parts[2] );
                current.add(
                    new MapLine( new Interval( sourceLower, sourceLower + length ), new Interval( destinationLower, destinationLower + length ) ) );
            }
            else {
                //ignore empty lines in input
            }
        } );
        current.add( new MapLine( null, null ) );
        almanach.add( new Category( header.get(), new ArrayList<>( current ) ) );
    }

    @Override
    public Long part0( Stream<String> input ) {
        readAlmanach( input );
        List<Long> source = new ArrayList<>( startingSeeds );
        List<Long> destinations = new ArrayList<>();

        long start = System.currentTimeMillis();
        for ( Category category : almanach ) {
            System.out.println( category.header() + "\t" + source + "\t " + ( System.currentTimeMillis() - start ) + "ms" );
            for ( long value : source ) {
                for ( MapLine map : category.mapLines() ) {
                    if ( map.isMapped( value ) ) {
                        destinations.add( map.translate( value ) );
                        break;
                    }
                }
            }
            source = new ArrayList<>( destinations );
            destinations.clear();
        }
        return source.stream().mapToLong( i -> i ).min().orElse( -1 );
    }

    @Override
    public Long part1( Stream<String> input ) {
        return null;
    }

    record MapLine(Interval source, Interval destination) {
        boolean isMapped( long value ) {
            return source == null || source.contains( value );
        }

        long translate( long value ) {
            if ( source == null ) {
                return value;
            }
            if ( !source.contains( value ) ) {
                throw new IllegalStateException( "Can only translate values contained in source interval" );
            }
            else if ( source.length() != destination.length() ) {
                throw new IllegalStateException( "Can only translate between intervals of equal length" );
            }
            return destination.getLower() + ( value - source.getLower() );
        }
    }

    record Category(String header, List<MapLine> mapLines) {}
}
