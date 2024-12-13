package de.delusions.aoc.advent2022;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

public class Day03 extends Day<Integer> {
    Day03() {
        super( 3, "Rucksack Reorganization" );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return input.map( backpack -> backpack.substring( backpack.length() / 2 ).chars().distinct()//
                .filter( type -> backpack.substring( 0, backpack.length() / 2 ).chars() //
                    .filter( c -> c == type ).findFirst().isPresent() ) //
                .map( this::priority ) //ascii offset
                .sum() ) //
                    .reduce( 0, Integer::sum );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Stack<List<String>> elfGroups = new Stack<>();
        elfGroups.push( new ArrayList<>() );
        input.forEach( elf -> {
            if ( elfGroups.isEmpty() || elfGroups.peek().size() == 3 ) {
                elfGroups.push( new ArrayList<>() );
            }
            elfGroups.peek().add( elf );
        } );
        return elfGroups.stream().map(
            group -> group.get( 0 ).chars().filter( type -> isInPack( group.get( 1 ), type ) && isInPack( group.get( 2 ), type ) ).map(
                this::priority ).findFirst().orElseGet( null ) ).reduce( 0, Integer::sum );


    }

    int priority( int type ) {
        return type < 97 ? type - 64 + 26 : type - 96;
    }

    boolean isInPack( String backpack, int type ) {
        return backpack.chars().filter( type1 -> type1 == type ).findFirst().isPresent();
    }
}
