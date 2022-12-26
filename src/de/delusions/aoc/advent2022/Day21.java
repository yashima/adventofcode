package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.delusions.aoc.advent2022.Day21.Operation.*;

public class Day21 extends Day<Long> {
    private Map<String, MathMonkey> mathMonkeys;

    public Day21() {
        super( 21, "Monkey Math" );
    }

    //two low: 945240480
    @Override
    public Long part1( Stream<String> input ) {
        mathMonkeys = parse( input );
        //mathMonkeys.values().forEach( m -> m.finishParsing( mathMonkeys ) );
        List<String> operations = mathMonkeys.values().stream().map( m -> m.mathString ).distinct().toList();
        System.out.println( operations );
        System.out.println( "Monkey Count=" + mathMonkeys.size() );
        System.out.println( mathMonkeys.get( "root" ).print( mathMonkeys ) );
        return mathMonkeys.get( "root" ).doMath( mathMonkeys );
    }

    @Override
    public Long part2( Stream<String> input ) {
        return null;
    }


    Map<String, MathMonkey> parse( Stream<String> input ) {
        return input.map( MathMonkey::new ).collect( Collectors.toMap( m -> m.name, Function.identity() ) );
    }

    enum Operation {
        PLUS, MINUS, MULTIPLY, DIVIDE, CONSTANT
    }

    static class MathMonkey {
        String name;

        static Pattern OP_REGEX = Pattern.compile( "([a-z]{4}) (.) ([a-z]{4})" );

        String mathString;

        int constant = -1;

        String lefty;

        String right;

        Operation operation;


        MathMonkey( String line ) {
            String[] split = line.split( ":" );
            this.name = split[0].trim();
            this.mathString = split[1].trim();
            try {
                constant = Integer.parseInt( mathString );
                operation = Operation.CONSTANT;
            }
            catch ( NumberFormatException e ) {
                Matcher matcher = OP_REGEX.matcher( mathString );
                if ( matcher.matches() ) {
                    lefty = matcher.group( 1 );
                    right = matcher.group( 3 );
                    operation = switch ( matcher.group( 2 ) ) {
                        case "+" -> PLUS;
                        case "-" -> MINUS;
                        case "*" -> MULTIPLY;
                        case "/" -> DIVIDE;
                        default -> null;
                    };
                }
            }
        }

        long doMath( Map<String, MathMonkey> map ) {
            return switch ( operation ) {
                case PLUS -> map.get( lefty ).doMath( map ) + map.get( right ).doMath( map );
                case MINUS -> map.get( lefty ).doMath( map ) - map.get( right ).doMath( map );
                case MULTIPLY -> map.get( lefty ).doMath( map ) * map.get( right ).doMath( map );
                case DIVIDE -> map.get( lefty ).doMath( map ) / map.get( right ).doMath( map );
                case CONSTANT -> constant;
            };
        }

        String print( Map<String, MathMonkey> map ) {
            return switch ( operation ) {
                case PLUS -> String.format( "(%s+%s)", map.get( lefty ).print( map ), map.get( right ).print( map ) );
                case MINUS -> String.format( "(%s-%s)", map.get( lefty ).print( map ), map.get( right ).print( map ) );
                case MULTIPLY -> String.format( "(%s*%s)", map.get( lefty ).print( map ), map.get( right ).print( map ) );
                case DIVIDE -> String.format( "(%s/%s)", map.get( lefty ).print( map ), map.get( right ).print( map ) );
                case CONSTANT -> String.format( "%s", constant );
            };
        }
    }
}

