package de.delusions.aoc;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day02 extends Day<Long> {

    Pattern T = Pattern.compile( "(\\d+) (green|blue|red)" );

    public Day02( Long... expected ) {
        super( 2, "Cube Conundrum", expected );
    }

    Game parseGame( String line ) {
        int id = Integer.parseInt( line.substring( 5, line.indexOf( ":" ) ) );
        Game game = new Game( id );
        String[] turns = line.substring( line.indexOf( ":" ) ).split( ";" );
        for ( String turn : turns ) {
            game.add( parseTurn( turn ) );
        }
        return game;
    }

    Turn parseTurn( String turn ) {
        Map<Color, Integer> stones = new HashMap<>();
        Matcher matcher = T.matcher( turn );
        while ( matcher.find() ) {
            stones.put( Color.valueOf( matcher.group( 2 ).toUpperCase() ), Integer.parseInt( matcher.group( 1 ) ) );
        }
        return new Turn( stones );
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

        final int number;

        Color( int number ) {this.number = number;}

    }

    record Turn(Map<Color, Integer> stones) {
        boolean isPossible() {
            return stones.entrySet().stream().allMatch( entry -> entry.getValue() <= entry.getKey().number );
        }
    }

    static class Game {
        int id;

        List<Turn> turns = new ArrayList<>();

        Game( int id ) {
            this.id = id;
        }

        int getId() {return this.id;}

        void add( Turn turn ) {turns.add( turn );}

        boolean isPossible() {
            return turns.stream().allMatch( Turn::isPossible );
        }

        int power() {
            //for each color find the maximum drawn on any turn, then multiply those numbers
            return Arrays.stream( Color.values() )
                .mapToInt( color -> turns.stream().mapToInt( t -> t.stones().getOrDefault( color, 0 ) ).max().orElse( 0 ) )
                .reduce( ( a, b ) -> a * b ).orElse( 0 );
        }
    }
}
