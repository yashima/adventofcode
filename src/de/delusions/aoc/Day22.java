package de.delusions.aoc;

import de.delusions.util.Day;
import de.delusions.util.Interval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day22 extends Day<Integer> {
    Pattern p = Pattern.compile( "([0-9]+),([0-9]+),([0-9]+)~([0-9,]+),([0-9]+),([0-9]+)" );

    public Day22( Integer... expected ) {super( 22, "Sand Slabs", expected );}

    @Override
    public Integer part0( Stream<String> input ) {
        List<Brick> stackedList = parseAndFallBricks( input );
        return (int) stackedList.stream().filter( b -> b.canDisintegrate( stackedList ) ).count();
    }


    @Override
    public Integer part1( Stream<String> input ) {
        List<Brick> stackedList = parseAndFallBricks( input );
        return stackedList.stream().mapToInt( b -> b.findConsequences( stackedList ).size() ).sum();
    }

    private List<Brick> parseAndFallBricks( Stream<String> input ) {
        AtomicInteger brickCounter = new AtomicInteger( 1 );
        List<Brick> bricks = input.map( line -> {
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                int x1 = Integer.parseInt( m.group( 1 ) );
                int y1 = Integer.parseInt( m.group( 2 ) );
                int z1 = Integer.parseInt( m.group( 3 ) );
                int x2 = Integer.parseInt( m.group( 4 ) );
                int y2 = Integer.parseInt( m.group( 5 ) );
                int z2 = Integer.parseInt( m.group( 6 ) );
                return new Brick( brickCounter.getAndIncrement(), new Dim( x1, y1, z1 ), new Dim( x2, y2, z2 ), new HashSet<>() );
            }
            return null;
        } ).sorted().toList();
        System.out.println( bricks );
        PriorityQueue<Brick> queue = new PriorityQueue<>( bricks );
        List<Brick> stackedList = new ArrayList<>();
        while ( !queue.isEmpty() ) {
            Brick brick = queue.poll();
            List<Brick> overlaps = stackedList.stream().filter( b -> b.overlapArea( brick ) ).toList();
            if ( overlaps.isEmpty() ) {
                //go to bottom
                stackedList.add( brick.fallTo( 0 ) );
            }
            else {
                int top = overlaps.stream().mapToInt( Brick::getTop ).max().getAsInt();
                Brick stacked = brick.fallTo( top + 1 );
                overlaps.stream().filter( b -> b.getTop() == top ).forEach( b -> b.supports().add( stacked ) );
                stackedList.add( stacked );
            }
        }
        return stackedList;
    }


    record Dim(int x, int y, int z) {
        Dim moveDownTo( int zz ) {
            return new Dim( x, y, zz );
        }
    }

    record Brick(int num, Dim corner1, Dim corner2, Set<Brick> supports) implements Comparable<Brick> {
        Interval vertical() {
            return new Interval( getBottom(), getTop() );
        }

        int getBottom() {return Math.min( corner1.z, corner2.z );}

        int getTop() {return Math.max( corner1.z, corner2.z );}

        //area of a brick is X-Y:
        boolean overlapArea( Brick o ) {
            return northSouth().overlap( o.northSouth() ) && eastWest().overlap( o.eastWest() );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof Brick brick ) ) {return false;}
            return num == brick.num && Objects.equals( corner1, brick.corner1 ) && Objects.equals( corner2, brick.corner2 );
        }

        @Override
        public int hashCode() {
            return Objects.hash( num, corner1, corner2 );
        }

        Interval northSouth() {
            return new Interval( getNorthEdge(), getSouthEdge() );
        }

        Interval eastWest() {
            return new Interval( getWestEdge(), getEastEdge() );
        }

        int getNorthEdge() {return Math.min( corner1.x, corner2.x );}

        int getSouthEdge() {return Math.max( corner1.x, corner2.x );}

        int getWestEdge() {return Math.min( corner1.y, corner2.y );}

        int getEastEdge() {return Math.max( corner1.y, corner2.y );}

        boolean canDisintegrate( List<Brick> bricks ) {
            if ( supports().isEmpty() ) {
                return true;
            }
            return supports().stream().allMatch( n -> bricks.stream().anyMatch( b -> b.num() != this.num() && b.supports().contains( n ) ) );
        }

        /* entry point for consequential recursion */
        List<Brick> findConsequences( List<Brick> bricks ) {
            if ( canDisintegrate( bricks ) ) {
                return List.of();
            }
            List<Brick> stillStandingBricks = new ArrayList<>( bricks );
            stillStandingBricks.remove( this );
            return findConsequencesRecursive( stillStandingBricks );
        }

        //the actual recursion checks if the supported blocks have any other supports,
        //if they don't have any, it adds them to the conseqeunces and calculates recursive consequences
        List<Brick> findConsequencesRecursive( List<Brick> stillStanding ) {
            List<Brick> consequences = new ArrayList<>();
            for ( Brick supported : supports() ) {
                Optional<Brick> any = stillStanding.stream().filter( b -> b.supports().contains( supported ) ).findAny();
                if ( any.isEmpty() ) { //block is no longer supported
                    stillStanding.remove( supported );
                    consequences.add( supported );
                    consequences.addAll( supported.findConsequencesRecursive( stillStanding ) );
                }
            }
            return consequences;
        }

        Brick fallTo( int z ) {
            int diff = Math.abs( z - this.getBottom() );
            return new Brick( num(), corner1.moveDownTo( corner1.z - diff ), corner2.moveDownTo( corner2.z - diff ), supports() );
        }

        @Override
        public int compareTo( Brick o ) {
            if ( getBottom() == o.getBottom() ) {return num < o.num ? -1 : 1;}
            //maybe it is enough to sort bricks by bottom?
            return getBottom() < o.getBottom() ? -1 : 1;
        }
    }
}
