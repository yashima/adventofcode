package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

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
        mathMonkeys.values().stream().forEach( m -> m.fetchMonkeys( mathMonkeys ) );
        return mathMonkeys.get( "root" ).doMath();
    }

    @Override
    public Long part2( Stream<String> input ) {
        MathMonkey root = mathMonkeys.get( "root" );
        MathMonkey human = mathMonkeys.get( "humn" );

        human.operation = null;
        try {root.doMath();}
        catch ( NullPointerException e ) {
            long count = mathMonkeys.values().stream().filter( m -> m.humanBranch ).count();
            System.out.println( "Found branch monkeys=" + count );
        }
        root.doHumanMath( 0L );
        return 0L;
    }


    Map<String, MathMonkey> parse( Stream<String> input ) {
        return input.map( MathMonkey::new ).collect( Collectors.toMap( m -> m.name, Function.identity() ) );
    }

    enum Operation {
        PLUS, MINUS, MULTIPLY, DIVIDE, CONSTANT
    }

    static class MathMonkey {
        static Pattern OP_REGEX = Pattern.compile( "([a-z]{4}) (.) ([a-z]{4})" );

        String name;

        String mathString;

        int constant = -1;

        String lefty;

        String righty;

        Operation operation;

        boolean humanBranch = false;

        MathMonkey mLeft;

        MathMonkey mRight;

        MathMonkey caller;

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
                    righty = matcher.group( 3 );
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

        void fetchMonkeys( Map<String, MathMonkey> map ) {
            mLeft = map.get( lefty );
            mRight = map.get( righty );
        }

        long doHumanMath( Long expectedFromCaller ) {
            if ( operation == CONSTANT ) {
                System.out.println( "Heureka? " + expectedFromCaller );
            }
            Long monkeyBranchResult;
            MathMonkey humanBranch;
            if ( !( mLeft.humanBranch || mRight.humanBranch ) ) {
                return doMath();
            }
            else if ( mLeft.humanBranch ) {
                monkeyBranchResult = mRight.doMath();
                humanBranch = mLeft;
            }
            else {
                monkeyBranchResult = mLeft.doMath();
                humanBranch = mRight;
            }
            return switch ( operation ) {
                case PLUS -> humanBranch.doHumanMath( expectedFromCaller - monkeyBranchResult );
                case MINUS -> humanBranch.doHumanMath( expectedFromCaller + monkeyBranchResult );
                case MULTIPLY -> humanBranch.doHumanMath( expectedFromCaller / monkeyBranchResult );
                case DIVIDE -> humanBranch.doHumanMath( expectedFromCaller * monkeyBranchResult );
                case CONSTANT -> 0L;
            };

        }

        long doMath() {
            try {
                return switch ( operation ) {
                    case CONSTANT -> constant;
                    case PLUS -> mLeft.doMath() + mRight.doMath();
                    case MINUS -> mLeft.doMath() - mRight.doMath();
                    case MULTIPLY -> mLeft.doMath() * mRight.doMath();
                    case DIVIDE -> mLeft.doMath() / mRight.doMath();
                };
            }
            catch ( NullPointerException e ) {
                this.humanBranch = true;
                throw e;
            }
        }


        String print( Map<String, MathMonkey> map ) {
            return switch ( operation ) {
                case PLUS -> String.format( "(%s+%s)", map.get( lefty ).print( map ), map.get( righty ).print( map ) );
                case MINUS -> String.format( "(%s-%s)", map.get( lefty ).print( map ), map.get( righty ).print( map ) );
                case MULTIPLY -> String.format( "(%s*%s)", map.get( lefty ).print( map ), map.get( righty ).print( map ) );
                case DIVIDE -> String.format( "(%s/%s)", map.get( lefty ).print( map ), map.get( righty ).print( map ) );
                case CONSTANT -> String.format( "%s", constant );
            };
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}

