package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Day 1: Stupid Auto Completion apparently either knows the puzzle or is too good at this.
 * It wishes it was the latter (or so the Auto Completion system says).
 *
 * I am using lombok now for some of the lazy annotations it provides.
 */
@Slf4j
public class Day01 extends Day<Integer> {

    public Day01() {
        super("Secret Entrance", 3,6,1172,6932);
    }

    static Pattern regex = Pattern.compile("(L|R)(\\d+)");

    record Turn(char direction, int steps) {

        /** Calculate the number of steps to turn to the left or right, ignore multiple turns */
        int getSteps(int max) {
            return ((direction == 'L') ? -steps : steps) % max;
        }

        int getSteps() {
            return ((direction == 'L') ? -steps : steps);
        }

        static Turn parse(String turn) {
            Matcher m = regex.matcher(turn);
            if (m.matches()) {
                return new Turn(m.group(1).charAt(0), Integer.parseInt(m.group(2)));
            }
            return null;
        }
    }

    @AllArgsConstructor
    static class Dial{

        int max;
        int value;

        /** Execute all turns and the number of times the dial turned to zero */
        int turnAndCountZeroStops(List<Turn> turns) {
            return turns.stream().map(this::turnAndCountZeroStops).reduce(0, Integer::sum);
        }

        int turnAndCountZeroClicks(List<Turn> turns) {
            return turns.stream().map(this::turnAndCountZeroClicks).reduce(0, Integer::sum);
        }

        /** Execute a turn and return 1 if the dial is set to zero */
        int turnAndCountZeroStops(Turn turn) {
            value += turn.getSteps(max);
            if (value < 0) value += max;
            else if (value >= max) value -= max;
            //log.debug("Turned {} {} steps to {}", turn.direction, turn.steps, value );
            return value==0 ? 1 : 0;
        }

        /** Execute a turn and return the sum of all the times the dial clicked through zero */
        int turnAndCountZeroClicks(Turn turn) {
            int zeroCount = turn.steps / max;
            zeroCount += value==0 && turn.direction=='L' ? -1 : 0;
            value += turn.getSteps(max);
            if(value <= 0 || value>=max) zeroCount++;
            if (value < 0) value += max;
            else if (value >= max) value -= max;
            //log.debug("Turned {} {} steps to {}, zero clicked={}", turn.direction, turn.steps, value,zeroCount );
            return zeroCount;
        }
    }


    @Override
    public Integer part0(Stream<String> input) {
        return new Dial(100,50).turnAndCountZeroStops(input.map(Turn::parse).toList());
    }

    @Override
    public Integer part1(Stream<String> input) {
        return new Dial(100,50).turnAndCountZeroClicks(input.map(Turn::parse).toList());
    }
}
