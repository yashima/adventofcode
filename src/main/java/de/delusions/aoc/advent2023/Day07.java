package de.delusions.aoc.advent2023;

import de.delusions.util.Day;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day07 extends Day<Long> {
    static final Map<Character, Card> CARDS_BY_ID = Arrays.stream( Card.values() ).collect( Collectors.toMap( Card::getId, Function.identity() ) );

    static boolean jokerMode = false;

    public Day07( Long... expected ) {
        super( 7, "Camel Cards", expected );
    }

    static Comparator<Card> cardComparator() {
        return Comparator.comparingInt( Card::getRank );
    }

    static Comparator<Hand> handComparator() {
        return Comparator.comparingInt( foo -> foo.ordinal() + 1 );
    }

    private static long calculateWinnings( List<CamelHand> camelhands ) {
        long totalWinnings = 0;
        for ( int rank = 0; rank < camelhands.size(); rank++ ) {
            totalWinnings += camelhands.get( rank ).bid() * ( rank + 1 );
        }
        return totalWinnings;
    }

    CamelHand createHand( String line, Function<Map<Card, Long>, Hand> findHand ) {
        String[] split = line.split( " " );
        List<Card> cards = split[0].chars().mapToObj( c -> CARDS_BY_ID.get( (char) c ) ).toList();
        Map<Card, Long> cardCounts = cards.stream().collect( Collectors.groupingByConcurrent( Function.identity(), Collectors.counting() ) );
        Hand hand = findHand.apply( cardCounts );
        return new CamelHand( cards, hand, Long.parseLong( split[1] ) );
    }

    @Override
    public Long part0( Stream<String> input ) {
        jokerMode = false;
        List<CamelHand> camelhands = input.map( line -> createHand( line, Hand::findHand ) ).sorted().toList();
        return calculateWinnings( camelhands );
    }

    @Override
    public Long part1( Stream<String> input ) {
        jokerMode = true;
        List<CamelHand> camelhands = input.map( line -> createHand( line, Hand::findHandWithJokers ) ).sorted().toList();
        return calculateWinnings( camelhands );
    }


    enum Card {
        TWO( '2', 2 ),//
        THREE( '3', 3 ),//
        FOUR( '4', 4 ), //
        FIVE( '5', 5 ), //
        SIX( '6', 6 ), //
        SEVEN( '7', 7 ), //
        EIGHT( '8', 8 ), //
        NINE( '9', 9 ),//
        TEN( 'T', 10 ),//
        JACK( 'J', 11 ) {
            @Override
            public int getRank() {
                return jokerMode ? 0 : super.getRank();
            }
        }, //
        QUEEN( 'Q', 12 ),//
        KING( 'K', 13 ), //
        ACE( 'A', 14 );

        final char id;

        final int rank;

        Card( char id, int rank ) {
            this.id = id;
            this.rank = rank;
        }

        public Character getId() {
            return id;
        }

        public int getRank() {
            return this.rank;
        }
    }

    enum Hand {

        HIGH_CARD {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 5;
            }
        }, ONE_PAIR {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 4;
            }
        }, TWO_PAIR {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 3 && cardCounts.values().stream().anyMatch( count -> count == 2 );
            }
        }, THREE_OF_A_KIND {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 3 && cardCounts.values().stream().anyMatch( count -> count == 3 );
            }
        }, FULL_HOUSE {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 2 && cardCounts.values().stream().anyMatch( count -> count == 3 );
            }
        }, FOUR_OF_A_KIND {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 2 && cardCounts.values().stream().anyMatch( count -> count == 4 );
            }
        }, FIVE_OF_A_KIND {
            @Override
            public boolean match( Map<Card, Long> cardCounts ) {
                return cardCounts.size() == 1;
            }
        };


        static Hand findHandWithJokers( Map<Card, Long> cardCounts ) {
            Hand hand = findHand( cardCounts );
            return cardCounts.containsKey( Card.JACK ) ? switch ( hand ) {
                case FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE -> FIVE_OF_A_KIND;
                case THREE_OF_A_KIND -> FOUR_OF_A_KIND;
                case TWO_PAIR -> cardCounts.get( Card.JACK ) == 2 ? FOUR_OF_A_KIND : FULL_HOUSE;
                case ONE_PAIR -> THREE_OF_A_KIND;
                case HIGH_CARD -> ONE_PAIR;
            } : hand;
        }

        static Hand findHand( Map<Card, Long> cardCounts ) {
            return Arrays.stream( values() ).filter( h -> h.match( cardCounts ) ).findFirst().orElse( FIVE_OF_A_KIND );
        }

        abstract boolean match( Map<Card, Long> cardCounts );

    }

    record CamelHand(List<Card> cards, Hand hand, long bid) implements Comparable<CamelHand> {

        @Override
        public int compareTo( CamelHand o ) {
            if ( this.hand != o.hand ) {
                return handComparator().compare( this.hand, o.hand );
            }
            for ( int i = 0; i < 5; i++ ) {
                Card foo = this.cards.get( i );
                Card bar = o.cards.get( i );
                if ( foo != bar ) {
                    return cardComparator().compare( foo, bar );
                }
            }
            return 0;
        }

    }
}
