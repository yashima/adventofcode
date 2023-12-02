package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day02 extends Day<Long> {

    Pattern R = Pattern.compile( "(\\d+) (green|blue|red)" );

    public Day02( Long... expected ) {
        super( 2, "Cube Conundrum", expected );
    }

    Game parseGame( String line ) {
        int id = Integer.parseInt( line.substring( 5, line.indexOf( ":" ) ) );
        Game game = new Game( id );
        Matcher matcher = R.matcher( line );
        while ( matcher.find() ) {
            game.put( Color.valueOf( matcher.group( 2 ).toUpperCase() ), Integer.parseInt( matcher.group( 1 ) ) );
        }
        return game;
    }

    @Override
    public Long part0( Stream<String> input ) {
        return input.map( this::parseGame ).filter( Game::isPossible ).mapToInt( Game::getId ).asLongStream().sum();
    }

    @Override
    public Long part1( Stream<String> input ) {
        return (long) input.map( this::parseGame ).mapToInt( Game::power ).sum();
    }

    enum Color {
        RED( 12 ), GREEN( 13 ), BLUE( 14 );

        final int stones;

        Color( int stones ) {this.stones = stones;}

    }


    static class Game {
        int id;

        Map<Color, Integer> maxStones = new HashMap<>();

        Game( int id ) {
            this.id = id;
        }

        int getId() {return this.id;}

        void put( Color c, int stones ) {
            if ( !maxStones.containsKey( c ) || maxStones.get( c ) < stones ) {
                maxStones.put( c, stones );
            }
        }

        boolean isPossible() {
            return maxStones.entrySet().stream().allMatch( e -> e.getValue() <= e.getKey().stones );
        }

        int power() {
            return maxStones.values().stream().reduce( ( a, b ) -> a * b ).orElse( 0 );
        }
    }
}
