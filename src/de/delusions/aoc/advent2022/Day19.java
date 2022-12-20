package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19 extends Day<Integer> {

    private static final String INPUT_REGEX = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";
    private static final Pattern PATTERN = Pattern.compile( INPUT_REGEX );

    record Blueprint(int oreRobotCostOre,
                     int clayRobotCostOre,
                     int obsidianRobotOre,
                     int obsidianRobotClay,
                     int geodeRobotOre,
                     int geodeRobotClay){};

    public Day19() {
        super( 19, "Not Enough Minerals" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        List<Blueprint> parse = parse( input );
        return null;
    }

    @Override
    public Integer part2( Stream<String> input ) {
        return null;
    }

    List<Blueprint> parse(Stream<String> input){
        return input.map(line -> {
            Matcher matcher = PATTERN.matcher( line );
            if( matcher.matches() ) {
                return new Blueprint( Integer.parseInt(matcher.group(1)),
                               Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)),
                               Integer.parseInt(matcher.group(4)),
                               Integer.parseInt(matcher.group(5)),
                               Integer.parseInt(matcher.group(6)));
            }
            return null;
        }).filter( Objects::nonNull ).peek( System.out::println ).toList();
    }

}
