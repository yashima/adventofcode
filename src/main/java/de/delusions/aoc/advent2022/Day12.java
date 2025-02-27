package de.delusions.aoc.advent2022;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Day12 extends Day<Integer> {

    static Matrix HILL;

    static final List<Coordinates> GOALS = new ArrayList<>();

    Day12() {
        super( 12, "Hill Climbing Algorithm" );
    }

    static boolean hasReachedGoal( Path path, List<Coordinates> goals ) {
        return goals.contains( path.pos );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return null;
    }

    @Override
    public Integer part1( Stream<String> input ) {
        HILL = new Matrix( input.map( line -> line.chars().map( c -> c - 96 ).toArray() ).toArray( int[][]::new ) );
        GOALS.addAll( HILL.findValues( ( (int) 'a' ) - 96, false ) );
        Coordinates startPos = HILL.findValues( ( (int) 'E' ) - 96, true ).getFirst();

        Map<Coordinates, Path> openList = new HashMap<>();
        Map<Coordinates, Path> closedList = new HashMap<>();

        Path start = new Path( startPos.x, startPos.y, null, "o" );
        openList.put( start.pos, start );

        List<Path> candidates = new ArrayList<>();
        while ( !openList.isEmpty() ) {

            Path path = openList.values().stream().min( Comparator.comparing( Path::getFnord ) ).get();
            openList.remove( path.pos );

            List<Path> children = Stream.of( Direction.north, Direction.south, Direction.east, Direction.west )//
                                        .map( path::move )//
                                        .filter( Objects::nonNull )//
                                        .filter( Path::isLegal ) //
                                        .toList();

            children.forEach( child -> {
                if ( hasReachedGoal( child, GOALS ) ) {
                    candidates.add( child );
                    return;
                }
                if ( ( !openList.containsKey( child.pos ) || openList.get( child.pos ).fnord > child.fnord ) &&
                    ( !closedList.containsKey( child.pos ) || closedList.get( child.pos ).fnord > child.fnord ) ) {
                    openList.put( child.pos, child );
                }
            } );
            closedList.put( path.pos, path );
        }

        return candidates.stream().map( path -> path.steps ).min( Integer::compareTo ).get();
    }

    static class Path {
        final Path parent;

        final Coordinates pos;

        final String move;

        final int height;

        final int cost;

        final int fnord;

        final int steps;


        Path( int x, int y, Path parent, String move ) {
            this.pos = new Coordinates( x, y );
            this.move = move;
            this.parent = parent;
            this.height = parent == null ? 0 : HILL.getMatrix()[x][y];
            this.cost = 1 + ( parent == null ? 0 : parent.cost );
            this.fnord = cost + lineOfSight();
            this.steps = parent == null ? 0 : parent.steps + 1;
        }

        int lineOfSight() {
            return GOALS.stream().map( g -> Math.abs( pos.x - g.x ) + Math.abs( pos.y - g.y ) ).min( Integer::compareTo ).get();
        }

        boolean isLegal() {
            return parent.height - height <= 1;
        }

        public String toString() {
            return parent + " -> " + cost + "[" + pos.x + ":" + pos.y + "]";
        }

        public int getFnord() {
            return fnord;
        }

        Path move( Direction move ) {
            try {
                return switch ( move ) {
                    case west -> new Path( this.pos.x, this.pos.y - 1, this, "<" );
                    case east -> new Path( this.pos.x, this.pos.y + 1, this, ">" );
                    case north -> new Path( this.pos.x - 1, this.pos.y, this, "^" );
                    case south -> new Path( this.pos.x + 1, this.pos.y, this, "v" );
                    default -> null;
                };
            }
            catch ( ArrayIndexOutOfBoundsException e ) {
                return null;
            }
        }

        String prettyPrint() {
            Map<Coordinates, Path> positions = new HashMap<>();
            Path current = this;
            while ( current != null ) {
                positions.put( current.pos, current );
                current = current.parent;
            }
            StringBuilder builder = new StringBuilder();
            for ( int x = 0; x < HILL.getXLength(); x++ ) {
                for ( int y = 0; y < HILL.getYLength(); y++ ) {
                    Coordinates currentPos = new Coordinates( x, y );
                    if ( GOALS.contains( currentPos ) ) {
                        builder.append( "X" );
                    }
                    else if ( positions.containsKey( currentPos ) ) {
                        builder.append( positions.get( currentPos ).move );
                    }
                    else {
                        builder.append( "_" );
                    }
                }
                builder.append( "\n" );
            }
            builder.append( "Steps=" );
            builder.append( this.steps );
            return builder.toString();
        }
    }

}
