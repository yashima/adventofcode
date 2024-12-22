package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
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
                .map(code -> new RobotsAllTheWayDown(code,26))
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

    static class RobotsAllTheWayDown {
        Map<String, String> cursorMap = new HashMap<>();
        Map<String, String> numPad = new HashMap<>();
        Map<String, String>[] metaMap;
        char[] positions;
        int maxLevel;
        State state;

        RobotsAllTheWayDown(String code) {
            this(code,3);
        }

        RobotsAllTheWayDown(String code, int maxLevel) {
            this.state = State.createFromCode(code);
            this.maxLevel = maxLevel;
            metaMap = new Map[maxLevel];
            positions = "A".repeat(maxLevel).toCharArray();
            hardCodedMapsInitializationMagic();
        }

        State pressSomeButtons() {
            while (state.level < maxLevel) {
                StringBuilder sb = new StringBuilder();
                String target = state.target;
                //LOG.debug("Level {} with target {}", state.level, target);
                while (target.length() > 0) {
                    char position = positions[state.level];
                    char nextChar = target.charAt(0);
                    String inputNeeded = metaMap[state.level].get(String.format("%s:%s", position, nextChar));
                    if (inputNeeded == null) {
                        LOG.warn("No neededButtonPresses found for {}:{}", position, nextChar);
                    }
                    sb.append(inputNeeded).append('A');
                    positions[state.level] = nextChar;
                    target = target.substring(1);
                }
                state = new State(state.number, sb.toString(), "", state.level + 1);
            }
            //LOG.debug("Level {} with target {}", state.level, state.target);
            return state;
        }

        void hardCodedMapsInitializationMagic() {
            cursorMap.put("A:^", "<");
            cursorMap.put("A:>", "v");
            cursorMap.put("A:v", "v<");
            cursorMap.put("A:<", "v<<");
            cursorMap.put("A:A", "");

            cursorMap.put("^:A", ">");
            //cursorMap.put("^:v", "v");
            cursorMap.put("^:>", "v>");
            cursorMap.put("^:<", "v<");
            cursorMap.put("^:^", "");

            cursorMap.put(">:>", "");
            cursorMap.put(">:A", "^");
            cursorMap.put(">:^", "<^");
            cursorMap.put(">:v", "<");
            //cursorMap.put(">:<", "<<");

            cursorMap.put("v:A", ">^");
            cursorMap.put("v:>", ">");
            cursorMap.put("v:<", "<");
            //cursorMap.put("v:^", "^");
            cursorMap.put("v:v", "");

            cursorMap.put("<:v", ">");
            //cursorMap.put("<:>", ">>");
            cursorMap.put("<:A", ">>^");
            cursorMap.put("<:^", ">^");
            cursorMap.put("<:<", "");

            //ex1
            numPad.put("A:0", "<");
            numPad.put("0:2", "^");
            numPad.put("2:9", ">^^");
            numPad.put("9:A", "vvv");
            //ex2
            numPad.put("A:9", "^^^");
            numPad.put("9:8", "<");
            numPad.put("8:0", "vvv");
            numPad.put("0:A", ">");
            //ex3
            numPad.put("A:1", "^<<");
            numPad.put("1:7", "^^");
            numPad.put("7:9", ">>");
            //ex4
            numPad.put("A:4", "^^<<");
            numPad.put("4:5", ">");
            numPad.put("5:6", ">");
            numPad.put("6:A", "vv");
            //ex5
            numPad.put("A:3", "^");
            numPad.put("3:7", "<<^^");

            //083a
            numPad.put("0:8", "^^^");
            numPad.put("8:3", "vv>");
            numPad.put("3:A", "v");

            //935a
            numPad.put("9:3", "vv");
            numPad.put("3:5", "<^");
            numPad.put("5:A", "vv>");
            //964A
            numPad.put("9:6", "v");
            numPad.put("6:4", "<<");
            numPad.put("4:A", ">>vv");

            //149A
            numPad.put("1:4", "^");
            numPad.put("4:9", ">>^");
            //789A
            numPad.put("A:7", "^^^<<");
            numPad.put("7:8", ">");
            numPad.put("8:9", ">");

            metaMap[0] = numPad;
            for (int i = 1; i < maxLevel; i++) {
                metaMap[i] = cursorMap;
            }
        }

    }

}

