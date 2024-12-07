package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day7 extends Day<Long> {


    public Day7() {
        super("Bridge Repair", 3749L, 0L, 4555081946288L, 0L);
    }

    static final Pattern REGEX = Pattern.compile("(\\d+)");

    enum Operator {
        ADD('+'), MUL('*');
        private final char symbol;

        Operator(char symbol) {
            this.symbol = symbol;
        }

        Long operate(Long a, Long b) {
            switch (this) {
                case ADD:
                    return a + b;
                case MUL:
                    return a * b;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + this);
            }
        }

        Long deoperate(Long a, Long b) {
            switch (this) {
                case ADD:
                    return a > b ? a - b : null;
                case MUL:
                    return a % b == 0 ? a / b : null;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + this);
            }
        }
    }

    record Equation(long solution, List<Long> operands) {
        static Equation fromString(String line) {
            List<Long> list = REGEX.matcher(line).results().map(m -> Long.parseLong(m.group(1))).toList();
            return new Equation(list.getFirst(), list.subList(1, list.size()));
        }

        boolean solvable() {
            Stack<Equation> solutions = new Stack<>();
            solutions.add(this);
            while (!solutions.isEmpty()) {
                Equation candidate = solutions.pop();
                if (candidate.operands.size() == 1) { //the end
                    if (candidate.operands.getFirst() == candidate.solution) return true;
                    //else keep going
                    continue;
                }
                for (Operator op : Operator.values()) {
                    Long newSolution = op.deoperate(candidate.solution, candidate.operands.getLast());
                    if (newSolution != null) {
                        solutions.add(new Equation(newSolution, candidate.operands.subList(0, candidate.operands.size() - 1)));
                    }
                }
            }
            return false;
        }

    }

    @Override
    public Long part0(Stream<String> input) {
        List<Equation> equations = input.map(Equation::fromString).toList();
        return equations.stream().filter(Equation::solvable).mapToLong(e -> e.solution).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return 0L;// input.collect(Collectors.joining());
    }
}
