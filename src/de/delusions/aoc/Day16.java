package de.delusions.aoc;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Direction;
import de.delusions.util.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import static de.delusions.aoc.Day16.Mirror.EMPTY;
import static de.delusions.util.Direction.*;

public class Day16 extends Day<Integer> {
    public Day16( Integer... expected ) {
        super( 16, "The Floor will be Lava", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        Matrix mirrors = Matrix.createFromStream( input );
        //split beams will split:
        Stack<Coordinates> beams = new Stack<>();
        Map<Coordinates, List<Direction>> energized = new HashMap<>();
        beams.push( new Coordinates( 0, 0, east ) );
        while ( !beams.isEmpty() ) {
            Coordinates current = beams.pop();
            //needs to prevent bouncing beams that just go in circles and set your processor on fire:
            if ( mirrors.isInTheMatrix( current ) && !energized.getOrDefault( current, new ArrayList<>() ).contains( current.getFacing() ) ) {
                Mirror mirror = Mirror.getBySymbol( (char) mirrors.getValue( current ) );
                mirror.getNextLights( current ).forEach( beams::push );
                if ( mirror == EMPTY ) {
                    mirrors.setValue( current, current.getFacing().getSymbol().charAt( 0 ) );
                }
                energized.putIfAbsent( current, new ArrayList<>() );
                energized.get( current ).add( current.getFacing() );
            }
        }
        System.out.println( mirrors );
        return energized.size();
    }

    enum Mirror {
        DIVIDER_HORIZONTAL( '-' ) {
            @Override
            List<Coordinates> getNextLights( Coordinates coordinates ) {
                if ( List.of( north, south ).contains( coordinates.getFacing() ) ) {
                    return List.of( coordinates.moveTo( east, 1 ), coordinates.moveTo( west, 1 ) );
                }
                return super.getNextLights( coordinates );
            }
        }, DIVIDER_VERTICAL( '|' ) {
            @Override
            List<Coordinates> getNextLights( Coordinates coordinates ) {
                if ( List.of( east, west ).contains( coordinates.getFacing() ) ) {
                    return List.of( coordinates.moveTo( north, 1 ), coordinates.moveTo( south, 1 ) );
                }
                return super.getNextLights( coordinates );
            }
        }, SLASH( '/' ) {
            @Override
            List<Coordinates> getNextLights( Coordinates coordinates ) {
                Coordinates next = switch ( coordinates.getFacing() ) {
                    case north -> coordinates.moveTo( east, 1 );
                    case south -> coordinates.moveTo( west, 1 );
                    case east -> coordinates.moveTo( north, 1 );
                    case west -> coordinates.moveTo( south, 1 );
                    default -> throw new IllegalStateException( "We're not moving diagonally" );
                };
                return List.of( next );
            }
        }, BACKSLASH( '\\' ) {
            @Override
            List<Coordinates> getNextLights( Coordinates coordinates ) {
                Coordinates next = switch ( coordinates.getFacing() ) {
                    case north -> coordinates.moveTo( west, 1 );
                    case south -> coordinates.moveTo( east, 1 );
                    case east -> coordinates.moveTo( south, 1 );
                    case west -> coordinates.moveTo( north, 1 );
                    default -> throw new IllegalStateException( "We're not moving diagonally" );
                };
                return List.of( next );
            }
        }, EMPTY( '.' );

        final char symbol;

        Mirror( char symbol ) {
            this.symbol = symbol;
        }

        static Mirror getBySymbol( char symbol ) {
            return Arrays.stream( values() ).filter( m -> m.symbol == symbol ).findFirst().orElse( EMPTY );
        }

        List<Coordinates> getNextLights( Coordinates coordinates ) {
            return List.of( coordinates.moveToNext() );
        }
    }

    @Override
    public Integer part1( Stream<String> input ) {
        return null;
    }
}
