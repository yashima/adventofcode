package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 extends Day<Integer> {
    private Map<String, MathMonkey> mathMonkeys;

    public Day21() {
        super( 21, "Monkey Math" );
    }

    //two low: 945240480
    @Override
    public Integer part1( Stream<String> input ) {
        mathMonkeys = parse( input );
        mathMonkeys.values().forEach( m -> m.finishParsing( mathMonkeys ) );
        return mathMonkeys.get( "root" ).doMath();
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }


    Map<String, MathMonkey> parse( Stream<String> input ) {
        return input.map( MathMonkey::new ).collect( Collectors.toMap( m -> m.name, Function.identity() ) );
    }

    static class MathMonkey {
        String name;

        String operation;

        int constant;

        MathMonkey lefty;

        MathMonkey right;


        MathMonkey( String line ) {
            String[] split = line.split( ":" );
            this.name = split[0].trim();
            this.operation = split[1].trim();
        }

        int doMath() {
            return switch ( operation ) {
                case "+" -> lefty.doMath() + right.doMath();
                case "-" -> lefty.doMath() - right.doMath();
                case "*" -> lefty.doMath() * right.doMath();
                case "/" -> lefty.doMath() / right.doMath();
                default -> constant;
            };
        }

        void finishParsing( Map<String, MathMonkey> map ) {
            String[] split = this.operation.split( " " );
            if ( split.length == 1 ) {
                constant = Integer.parseInt( split[0] );
            }
            else {
                lefty = map.get( split[0] );
                right = map.get( split[2] );
                operation = split[1];
            }

        }
    }
}
