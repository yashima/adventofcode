package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day11 extends Day<Long> {

    public Day11() {
        super( 11, "Monkey in the Middle" );
    }

    @Override
    public Long part1( Stream<String> input ) {
        List<String> lines = input.map( String::trim ).filter( line -> !line.isEmpty() ).toList();
        List<Monkey> monkeys = new ArrayList<>();
        int monkeyDef = 6;
        for ( int idx = 0; idx < 8; idx++ ) {
            int start = idx * monkeyDef;
            Monkey monkey = Monkey.parseFromStrings( lines.subList( start, start + monkeyDef ), monkeys );
            monkeys.add( monkey );
            System.out.println( "Init: " + monkey );
        }
        for ( int round = 0; round < 10000; round++ ) {
            monkeys.forEach( Monkey::inspect );
        }
        return monkeys.stream()//
                      .peek( System.out::println )//
                      .map( Monkey::getBusiness )//
                      .sorted( Comparator.reverseOrder() )//
                      .limit( 2 )//
                      .reduce( 1L, ( a, b ) -> a * b );
    }

    @Override
    public Long part2( Stream<String> input ) {
        //TODO solved it but
        return null;
    }


    static class Monkey {
        BigInteger MAGIC = BigInteger.valueOf( 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 );

        Stack<BigInteger> items = new Stack<>();

        Function<BigInteger, BigInteger> monkeyFingers;

        BigInteger monkeyTest;

        int trueMonkey;

        int falseMonkey;

        long business = 0;

        List<Monkey> monkeys;

        static Monkey parseFromStrings( List<String> monkeyString, List<Monkey> monkeys ) {
            Monkey monkey = new Monkey();
            Matcher itemMatcher = Pattern.compile( "(\\d+)" ).matcher( monkeyString.get( 1 ) );
            while ( itemMatcher.find() ) {
                monkey.items.push( BigInteger.valueOf( Long.parseLong( itemMatcher.group( 1 ) ) ) );
            }
            Matcher operationMatcher = Pattern.compile( "Operation: new = old ([\\*\\+]) (\\d+|old)" ).matcher( monkeyString.get( 2 ) );
            if ( operationMatcher.matches() ) {
                boolean sum = operationMatcher.group( 1 ).equals( "+" );
                String operand = operationMatcher.group( 2 );
                if ( !operand.equals( "old" ) ) {
                    BigInteger op = BigInteger.valueOf( Long.parseLong( operand ) );
                    monkey.monkeyFingers = item -> sum ? item.add( op ) : item.multiply( op );
                }
                else {
                    monkey.monkeyFingers = item -> item.multiply( item );
                }
            }
            Matcher testMatcher = Pattern.compile( "Test: divisible by (\\d+)" ).matcher( monkeyString.get( 3 ) );
            if ( testMatcher.matches() ) {
                monkey.monkeyTest = BigInteger.valueOf( Long.parseLong( testMatcher.group( 1 ) ) );
            }
            Pattern pattern = Pattern.compile( "If (true|false): throw to monkey (\\d+)" );
            Matcher trueMonkeyMatcher = pattern.matcher( monkeyString.get( 4 ) );
            Matcher falseMonkeyMatcher = pattern.matcher( monkeyString.get( 5 ) );
            if ( trueMonkeyMatcher.matches() ) {
                monkey.trueMonkey = Integer.parseInt( trueMonkeyMatcher.group( 2 ) );
            }
            if ( falseMonkeyMatcher.matches() ) {
                monkey.falseMonkey = Integer.parseInt( falseMonkeyMatcher.group( 2 ) );
            }
            monkey.monkeys = monkeys;
            return monkey;
        }

        BigInteger lowerAnxiety( BigInteger currentAnxiety ) {
            return currentAnxiety.mod( MAGIC );
        }

        public Long getBusiness() {
            return business;
        }

        void inspect() {
            while ( !items.isEmpty() ) {
                BigInteger newItemAnxietyLevel = lowerAnxiety( monkeyFingers.apply( items.pop() ) );
                if ( newItemAnxietyLevel.mod( monkeyTest ).equals( BigInteger.ZERO ) ) {
                    monkeys.get( trueMonkey ).items.push( newItemAnxietyLevel );
                }
                else {
                    monkeys.get( falseMonkey ).items.push( newItemAnxietyLevel );
                }
                business++;
            }
        }

        public String toString() {
            return String.format( "Monkey[items=%s,div=%s, true=%s,false=%s, busy=%s]", items, monkeyTest, trueMonkey, falseMonkey, business );
        }
    }
}
