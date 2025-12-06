package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Day 6: Trash Compactor.
 * Part 1 took me longer than it strictly had to, but I feel the need to use proper object-oriented programming.
 * I find it makes my code more readable and easier to maintain.
 * Part 2: is a parsing problem.
 */
@Slf4j
public class Day06 extends Day<Long> {
    public Day06() {
        super("Trash Compactor", 4277556L,3263827L,7098065460541L,13807151830618L);
    }

    /** Enum representing the 2 operators we have */
    enum Operator {
        ADD, MUL;
        static Operator fromChar(char c){
            return c == '+' ? ADD : MUL;
        }
    }

    /** Represents one of the cephalopod math problems to solve */
    record Problem(List<String> operands, Operator op){

        static Problem create(List<String> operands,char op){
            return new Problem(operands,Operator.fromChar(op));
        }

        /** Solve the problem either by adding or multiplying the operands */
        Long solve(){
           Matrix m = Matrix.createFromStream(operands.stream());
           List<Long> opsAsNumbers = m.rows().map(Matrix::toCharString).filter(Predicate.not(String::isBlank)).mapToLong(l -> Long.parseLong(l.trim())).boxed().toList();
           return solve(opsAsNumbers);
        }

        private Long solve(List<Long> operandsParam){
            if (op == Operator.ADD) {
                return operandsParam.stream().reduce(0L, (a, b) -> a + b);
            } else {
                return operandsParam.stream().reduce(1L, (a, b) -> a * b);
            }
        }

        Long solveVertical(){
            Matrix m = Matrix.createFromStream(operands.stream());
            List<Long> opsAsNumbers = m.transposeLeft().rows().map(Matrix::toCharString).filter(Predicate.not(String::isBlank)).mapToLong(l -> Long.parseLong(l.trim())).boxed().toList();
            return solve(opsAsNumbers);
        }
    }

    @Override
    public Long part0(Stream<String> input) {
        return createProblems(input).stream().mapToLong(Problem::solve).sum();
    }

    static List<Problem> createProblems(Stream<String> input) {
        List<String> lines = input.toList();
        String operators = lines.getLast();
        List<Problem> problems = new ArrayList<>();
        int problemStartIdx = 0;
        for(int pos=0;pos<operators.length()-1;pos++){
            if(List.of('+','*').contains(operators.charAt(pos))) {
                if(pos>0){
                    int finalProblemStartIdx = problemStartIdx;
                    int finalPos = pos;
                    List<String> operands = lines.stream().limit(lines.size() - 1).map(l -> l.substring(finalProblemStartIdx, finalPos)).toList();
                    problems.add(Problem.create(operands,operators.charAt(problemStartIdx)));
                    problemStartIdx = pos;
                }
            } else if (pos==operators.length()-2){
                int finalProblemStartIdx = problemStartIdx;
                List<String> operands = lines.stream().limit(lines.size() - 1).map(l -> l.substring(finalProblemStartIdx)).toList();
                problems.add(Problem.create(operands,operators.charAt(problemStartIdx)));
            }
        }
        return problems;
    }

    @Override
    public Long part1(Stream<String> input) {
        return createProblems(input).stream().mapToLong(Problem::solveVertical).sum();
    }
}
