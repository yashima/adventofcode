package de.delusions.aoc.advent2025;

import de.delusions.util.Day;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Part 1: convert various parts of the input into bitmasks that toggle bits until a desired result is found with bfs / Breitensuche
 * Part 2: do the same search but now add numbers instead of toggling bits.
 */
public class Day10 extends Day<Long> {
    public Day10() {
        super("tag", 7L, 0L, 466L, 0L);
    }

    static Pattern pattern = Pattern.compile("\\(([\\d,]+)\\)");

    static boolean isBitSet(int value, int pos) {
        return (value & (1 << pos)) != 0;
    }

    record LightsOn(int value, int depth) {
    }

    record Joltages(List<Integer> values,int depth) {
        Joltages pressButton(Integer button){
            List<Integer> higherValues = new ArrayList<>();
            for(int pos=0;pos<values.size();pos++) {
                //we're starting with the target value and end when everything is zero. same thing really.
                higherValues.add( values.get(pos)-(isBitSet(button,pos)? 1 : 0));
            }
            return new Joltages(higherValues,depth()+1);
        }
        String key(){
            return values.stream().map(Object::toString).reduce("",(a,b)->a+b+",");
        }

        boolean done(){
            return values.stream().allMatch(i -> i == 0);
        }
        boolean oops() { return values.stream().anyMatch(i -> i < 0);}
    }



    record Machine(int result, List<Integer> buttons,List<Integer> joltage) {
        static Machine parse(String line) {
            String lights = line.substring(1,line.indexOf(']'));
            String joltage = line.substring(line.indexOf('{')+1,line.indexOf('}'));
            return new Machine(
                    convertLightsToBitmask(lights),
                    pattern.matcher(line).results().map(m -> convertButtonsToBitmask(m.group(1), ",",lights.length())).toList(),
                    Arrays.stream(joltage.split(",")).mapToInt(Integer::parseInt).boxed().toList().reversed());
        }

        int numberOfLightButtonPresses() {
            Queue<LightsOn> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();

            queue.add(new LightsOn(0, 0));
            visited.add(0);

            while (!queue.isEmpty()) {
                LightsOn cur = queue.remove();

                List<Integer> neighbors = buttons.stream().map(b -> cur.value ^ b).toList();

                for (Integer next : neighbors) {
                    if(next.equals(result)) { return cur.depth() + 1; }
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(new LightsOn(next, cur.depth() + 1));
                    }
                }

            }
            return -1;
        }

        int numberOfJoltageButtonPresses() {
            Queue<Joltages> queue = new ArrayDeque<>();
            Set<String> visited = new HashSet<>();

            Joltages initial = new Joltages(new ArrayList<Integer>(joltage), 0);
            queue.add(initial);
            visited.add(initial.key());

            while (!queue.isEmpty()) {
                Joltages cur = queue.remove();

                List<Joltages> neighbors = buttons.stream().map(b -> cur.pressButton(b)).filter(Predicate.not(Joltages::oops)).toList();

                for (Joltages next : neighbors) {
                    if(next.done()) { return cur.depth() + 1; }
                    if (!visited.contains(next.key())) {
                        visited.add(next.key());
                        queue.add(next);
                    }
                }

            }
            return -1;
        }
    }

    static int convertLightsToBitmask(String lights) {

        return Integer.parseInt(lights.replace('.', '0').replace('#', '1'), 2);
    }

    static int convertButtonsToBitmask(String numbers, String splitBy,int size) {
        char[] button = new char[size];
        Arrays.fill(button, '0');
        Arrays.stream(numbers.split(splitBy)).forEach(n -> button[Integer.parseInt(n)] = '1');
        return Integer.parseInt(new String(button), 2);
    }

    @Override
    public Long part0(Stream<String> input) {
        return input
                .map(Machine::parse)
                .mapToLong(Machine::numberOfLightButtonPresses)
                .sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return input
                .map(Machine::parse)
                .peek(System.out::println)
                .mapToLong(Machine::numberOfJoltageButtonPresses)
                .peek(System.out::println)
                .sum();
    }
}
