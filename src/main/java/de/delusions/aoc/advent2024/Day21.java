package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day21 extends Day<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(Day21.class);

    public Day21() {
        super("", 126384L, 0L, 203734L, 0L);
    }

    @Override
    public Long part0(Stream<String> input) {
        return input
                .map(RobotsAllTheWayDown::new)
                .map(RobotsAllTheWayDown::pressSomeButtons)
                .mapToLong(State::complexity)
                .sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        //TODO write another f...ing memoization cache
        return input
                .map(code -> new RobotsAllTheWayDown(code, 15))
                .map(RobotsAllTheWayDown::pressSomeButtons)
                .mapToLong(State::complexity)
                .sum();
    }


    //I was planning on doing something recursive with this and then I didn't:
    record State(int number, String target, String neededButtonPresses, int level) {
        static State createFromCode(String code) {
            return new State(Integer.parseInt(code.substring(0, 3)), code, "", 0);
        }

        long complexity() {
            return number * target.length();
        }

    }

    record Edge(char from, char to) {
        static Edge create(int f, int t) {
            return new Edge((char) f, (char) t);
        }
    }

    static class RobotsAllTheWayDown {
        Map<Edge, String> cursorMap = new HashMap<>();
        Map<Edge, String> numPad = new HashMap<>();
        Map<Edge, String>[] metaMap;
        AtomicInteger[] positions;
        int maxLevel;
        State state;

        RobotsAllTheWayDown(String code) {
            this(code, 3);
        }

        RobotsAllTheWayDown(String code, int maxLevel) {
            this.state = State.createFromCode(code);
            this.maxLevel = maxLevel;
            metaMap = new Map[maxLevel];
            positions = new AtomicInteger[maxLevel];
            IntStream.range(0, maxLevel).forEach(i -> positions[i] = new AtomicInteger('A'));
            hardCodedMapsInitializationMagic();
        }

        State pressSomeButtons() {
            while (state.level < maxLevel) {
                this.state = nextState(this.state);
            }
            return state;
        }

        static Map<String, String> CACHE = new HashMap<>();

        private State nextState(State state) {
            if (state.level >= maxLevel) {
                return state;
            }
            String result = CACHE.computeIfAbsent(
                    state.target,
                    s -> s.chars()
                            .mapToObj(this::selectOutput)
                            .collect(Collectors.joining()));

            CACHE.put(state.target, result);
            return new State(state.number, result, "", state.level + 1);
        }


        private String selectOutput(int nextChar) {
            return metaMap[state.level].get(Edge.create(positions[state.level].getAndSet(nextChar), nextChar));
        }


        void hardCodedMapsInitializationMagic() {
            cursorMap.put(new Edge('A', '^'), "<A");
            cursorMap.put(new Edge('A', '>'), "vA");
            cursorMap.put(new Edge('A', 'v'), "v<A");
            cursorMap.put(new Edge('A', '<'), "v<<A");
            cursorMap.put(new Edge('A', 'A'), "A");
            cursorMap.put(new Edge('^', 'A'), ">A");
            cursorMap.put(new Edge('^', '>'), "v>A");
            cursorMap.put(new Edge('^', '<'), "v<A");
            cursorMap.put(new Edge('^', '^'), "A");
            cursorMap.put(new Edge('>', '>'), "A");
            cursorMap.put(new Edge('>', 'A'), "^A");
            cursorMap.put(new Edge('>', '^'), "<^A");
            cursorMap.put(new Edge('>', 'v'), "<A");
            cursorMap.put(new Edge('v', 'A'), ">^A");
            cursorMap.put(new Edge('v', '>'), ">A");
            cursorMap.put(new Edge('v', '<'), "<A");
            cursorMap.put(new Edge('v', 'v'), "A");
            cursorMap.put(new Edge('<', 'v'), ">A");
            cursorMap.put(new Edge('<', 'A'), ">>^A");
            cursorMap.put(new Edge('<', '^'), ">^A");
            cursorMap.put(new Edge('<', '<'), "A");

            numPad.put(new Edge('A', '0'), "<A");
            numPad.put(new Edge('0', '2'), "^A");
            numPad.put(new Edge('2', '9'), ">^^A");
            numPad.put(new Edge('9', 'A'), "vvvA");
            numPad.put(new Edge('A', '9'), "^^^A");
            numPad.put(new Edge('9', '8'), "<A");
            numPad.put(new Edge('8', '0'), "vvvA");
            numPad.put(new Edge('0', 'A'), ">A");
            numPad.put(new Edge('A', '1'), "^<<A");
            numPad.put(new Edge('1', '7'), "^^A");
            numPad.put(new Edge('7', '9'), ">>A");
            numPad.put(new Edge('A', '4'), "^^<<A");
            numPad.put(new Edge('4', '5'), ">A");
            numPad.put(new Edge('5', '6'), ">A");
            numPad.put(new Edge('6', 'A'), "vvA");
            numPad.put(new Edge('A', '3'), "^A");
            numPad.put(new Edge('3', '7'), "<<^^A");
            numPad.put(new Edge('0', '8'), "^^^A");
            numPad.put(new Edge('8', '3'), "vv>A");
            numPad.put(new Edge('3', 'A'), "vA");
            numPad.put(new Edge('9', '3'), "vvA");
            numPad.put(new Edge('3', '5'), "<^A");
            numPad.put(new Edge('5', 'A'), "vv>A");
            numPad.put(new Edge('9', '6'), "vA");
            numPad.put(new Edge('6', '4'), "<<A");
            numPad.put(new Edge('4', 'A'), ">>vvA");
            numPad.put(new Edge('1', '4'), "^A");
            numPad.put(new Edge('4', '9'), ">>^A");
            numPad.put(new Edge('A', '7'), "^^^<<A");
            numPad.put(new Edge('7', '8'), ">A");
            numPad.put(new Edge('8', '9'), ">A");

            metaMap[0] = numPad;
            for (int i = 1; i < maxLevel; i++) {
                metaMap[i] = cursorMap;
            }
        }

    }

}

