package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day05 extends Day<Long> {

    List<Long> startingSeeds = new ArrayList<>();

    List<Category> almanach = new ArrayList<>();

    public Day05( Long... expected ) {
        super( 5, "If You Give A Seed A Fertilizer", expected );
    }

    private void readAlmanach( Stream<String> input ) {
        startingSeeds = new ArrayList<>();
        almanach = new ArrayList<>();
        List<Rule> current = new ArrayList<>();
        AtomicReference<String> header = new AtomicReference<>();
        input.forEach( line -> {
            //first line contains the seeds
            if ( startingSeeds.isEmpty() ) {
                String[] seeds = line.substring( line.indexOf( ":" ) + 1 ).trim().split( " " );
                startingSeeds.addAll( Arrays.stream( seeds ).map( Long::parseLong ).toList() );
            }
            else if ( line.contains( ":" ) ) {
                if ( !current.isEmpty() ) {
                    almanach.add( createCategory( current, header ) );
                }
                header.set( line );
            }
            else if ( !line.isBlank() ) {
                String[] parts = line.split( " " );
                long destinationLower = Long.parseLong( parts[0] );
                long sourceLower = Long.parseLong( parts[1] );
                long length = Long.parseLong( parts[2] );
                current.add( new Rule( new Interval( sourceLower, sourceLower + length - 1 ),
                                       new Interval( destinationLower, destinationLower + length - 1 ) ) );
            }
            else {
                //ignore empty lines in input
            }
        } );
        almanach.add( createCategory( current, header ) );


    }

    private Category createCategory( List<Rule> current, AtomicReference<String> header ) {
        List<Rule> sorted = new ArrayList<>( current.stream().sorted( Comparator.comparing( Rule::source ) ).toList() );
        //catch-all that maps values to themselves
        sorted.add( new Rule( null, null ) );
        current.clear();
        return new Category( header.get(), new ArrayList<>( sorted ) );
    }

    @Override
    public Long part0( Stream<String> input ) {
        readAlmanach( input );
        List<Long> source = new ArrayList<>( startingSeeds );
        List<Long> destinations = new ArrayList<>();

        for ( Category category : almanach ) {
            for ( long value : source ) {
                for ( Rule rule : category.rules() ) {
                    if ( rule.matches( value ) ) {
                        destinations.add( rule.translate( value ) );
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
        readAlmanach( input );
        LinkedList<Interval> stash = new LinkedList<>();
        LinkedList<Interval> next = new LinkedList<>();
        for ( int i = 0; i < startingSeeds.size(); i = i + 2 ) {
            Long lower = startingSeeds.get( i );
            long upper = lower + startingSeeds.get( i + 1 ) - 1;
            stash.push( new Interval( lower, upper ) );
        }
        System.out.println( stash );
        for ( Category category : almanach ) {
            while ( !stash.isEmpty() ) {
                Interval seedChunk = stash.pop();
                for ( Rule rule : category.rules() ) {
                    if ( rule.matches( seedChunk ) ) {
                        if ( rule.source() == null ) {
                            next.add( seedChunk );
                        }
                        else if ( rule.source().contains( seedChunk ) ) {
                            next.add( rule.translate( seedChunk ) );
                        }
                        else {
                            //Mach Drei Stücke Obelix:
                            next.add( rule.translate( rule.source().intersect( seedChunk ) ) );
                            if ( seedChunk.getLower() < rule.source().getLower() ) {
                                stash.push( new Interval( seedChunk.getLower(), rule.source().getLower() - 1 ) );
                            }
                            if ( seedChunk.getUpper() > rule.source().getUpper() ) {
                                stash.push( new Interval( rule.source().getUpper() + 1, seedChunk.getUpper() ) );
                            }
                        }
                        break;
                    }
                }
            }

            stash.addAll( next );
            next.clear();
        }
        return stash.stream().mapToLong( Interval::getLower ).min().orElse( -1 );
    }

    record Rule(Interval source, Interval destination) {
        boolean matches( long value ) {
            return source == null || source.contains( value );
        }

        boolean matches( Interval interval ) {
            return source == null || source.overlap( interval );
        }

        Interval translate( Interval chunk ) {
            return new Interval( translate( chunk.getLower() ), translate( chunk.getUpper() ) );
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

    record Category(String header, List<Rule> rules) {}
}
