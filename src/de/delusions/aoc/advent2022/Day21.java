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

    @Override
    public Long part1( Stream<String> input ) {
        mathMonkeys = parse( input );
        mathMonkeys.values().forEach( m -> m.fetchMonkeys( mathMonkeys ) );
        return mathMonkeys.get( "root" ).doMath();
    }

    @Override
    public Long part2( Stream<String> input ) {
        MathMonkey root = mathMonkeys.get( "root" );
        MathMonkey human = mathMonkeys.get( "humn" );

        human.operation = null; //because I can
        try {root.doMath();}
        catch ( NullPointerException e ) {
            try {
                root.doHumanMath( 0L );
            }
            catch ( HumanDetection hd ) {
                return hd.result;
            }
        }
        //if we get here...
        return 0L;
    }

    Map<String, MathMonkey> parse( Stream<String> input ) {
        return input.map( MathMonkey::new ).collect( Collectors.toMap( m -> m.name, Function.identity() ) );
    }

    enum Operation {
        PLUS, MINUS, MULTIPLY, DIVIDE, CONSTANT
    }

    static class HumanDetection extends RuntimeException {
        long result;

        HumanDetection( long result ) {
            super();
            this.result = result;
        }
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


        long doHumanMath( Long expected ) {
            if ( name.equals( "humn" ) ) {
                throw new HumanDetection( expected );
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
                case PLUS -> humanBranch.doHumanMath( expected - monkeyBranchResult );
                case MINUS -> humanBranch.doHumanMath( expected + monkeyBranchResult );
                case MULTIPLY -> humanBranch.doHumanMath( expected / monkeyBranchResult );
                case DIVIDE -> humanBranch.doHumanMath( expected * monkeyBranchResult );
                case CONSTANT -> 0L; //never happens
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
                case PLUS -> String.format( "(%s+%s)", mLeft.print( map ), mRight.print( map ) );
                case MINUS -> String.format( "(%s-%s)", mLeft.print( map ), mRight.print( map ) );
                case MULTIPLY -> String.format( "(%s*%s)", mLeft.print( map ), mRight.print( map ) );
                case DIVIDE -> String.format( "(%s/%s)", mLeft.print( map ), mRight.print( map ) );
                case CONSTANT -> String.format( "%s", constant );
            };
        }

    }
}

