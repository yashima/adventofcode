package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.delusions.aoc.advent2024.Day07.Operator.*;

public class Day07 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day07.class);

    public Day07() {
        super("Bridge Repair", 3749L, 11387L, 4555081946288L, 227921760109726L);
    }

    static final Pattern REGEX = Pattern.compile("(\\d+)");

    enum Operator {
        ADD {
            Long operate(Long a, Long b) {
                return a + b;
            }

            Long deoperate(Long a, Long b) {
                return a > b ? a - b : null;
            }
        }, MUL {
            @Override
            Long operate(Long a, Long b) {
                return a * b;
            }

            Long deoperate(Long a, Long b) {
                return a % b == 0 ? a / b : null;
            }
        }, CON {
            @Override
            Long operate(Long a, Long b) {
                return Long.parseLong(a.toString() + b.toString());
            }

            Long deoperate(Long a, Long b) {
                long powerOf10 = BigDecimal.valueOf(Math.pow(10, (long) b.toString().length())).longValue();
                return a >= b && (a - b) % powerOf10 == 0 ? (a - b) / powerOf10 : null;
            }
        };

        abstract Long deoperate(Long a, Long b);

        abstract Long operate(Long a, Long b);
    }

    record Equation(String input, long solution, List<Long> operands) {
        static Equation fromString(String line) {
            List<Long> list = REGEX.matcher(line).results().map(m -> Long.parseLong(m.group(1))).toList();
            return new Equation(line, list.getFirst(), list.subList(1, list.size()));
        }

        boolean solveByFirst(List<Operator> operators) {
            Stack<Equation> solutions = new Stack<>();
            solutions.add(new Equation(this.input, this.operands.getFirst(), this.operands.subList(1, this.operands.size())));
            while (!solutions.isEmpty()) {
                Equation candidate = solutions.pop();
                if (candidate.operands.isEmpty()) {
                    if (candidate.solution == this.solution) return true;
                    continue;
                }
                for (Operator op : operators) {
                    Long newSolution = op.operate(candidate.solution, candidate.operands.getFirst());
                    if (newSolution <= this.solution) {
                        solutions.add(new Equation(this.input, newSolution, candidate.operands.subList(1, candidate.operands.size())));
                    }
                }
            }
            return false;
        }

        boolean solveByLast(List<Operator> operators) {
            Stack<Equation> solutions = new Stack<>();
            solutions.add(this);
            while (!solutions.isEmpty()) {
                Equation candidate = solutions.pop();
                if (candidate.operands.size() == 1) { //the end
                    if (candidate.operands.getFirst() == candidate.solution) return true;
                    //else keep going
                    continue;
                }
                for (Operator op : operators) {
                    Long newSolution = op.deoperate(candidate.solution, candidate.operands.getLast());
                    if (newSolution != null) {
                        solutions.add(new Equation(this.input, newSolution, candidate.operands.subList(0, candidate.operands.size() - 1)));
                    }
                }
            }
            return false;
        }

    }

    @Override
    public Long part0(Stream<String> input) {
        List<Equation> equations = input.map(Equation::fromString).toList();
        return equations.stream().filter(e -> e.solveByFirst(List.of(ADD, MUL))).mapToLong(e -> e.solution).sum();
    }

    //too low: 28760150883702
    @Override
    public Long part1(Stream<String> input) {
        List<Equation> equations = input.map(Equation::fromString).toList();
        return equations.stream().filter(e -> e.solveByLast(List.of(ADD, MUL, CON))).mapToLong(e -> (e.solution)).sum();
    }
}
