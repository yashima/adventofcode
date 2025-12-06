package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import de.delusions.util.Matrix;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Day 6: Trash Compactor.
 * Part 1 took me longer than it strictly had to, but I feel the need to use proper object-oriented programming.
 * I find it makes my code more readable and easier to maintain.
 * Part 2: is a parsing problem. After solving it, I realized I could have transposed the whole input for an even easier solution.
 * And I can also get back to using regular expressions to help with the parsing: yay!
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
    record Problem(List<Long> operands, Operator op){

        /** I am parsing the input into a matrix for part 1 */
        static Problem create(int[] column){
            return new Problem(
                    Arrays.stream(column).limit(column.length-1).mapToLong(i -> Long.valueOf(i)).boxed().toList(),
                    Operator.fromChar((char)column[column.length-1]));
        }

        /** For part 2 I have already created a list of numbers */
        static Problem createFromLong(List<Long> operands,char op){
            return new Problem(operands,Operator.fromChar(op));
        }

        /** Now solve the problem and return the sum or product */
        Long solve(){
            if (op == Operator.ADD) {
                return operands.stream().reduce(0L, (a, b) -> a + b);
            } else {
                return operands.stream().reduce(1L, (a, b) -> a * b);
            }
        }

    }

    static Pattern lineParserRegex = Pattern.compile("(\\d+)|([+*])");

    /**
     * We actually need to do some parsing today, as there are lines that are formatted differently than the others.
     * @param line lines can contain either numbers split by spaces or a single operator.
     * @return an array of integers representing the parsed line for us in a matrix, operators are represented by their char value
     */
    public static int[] parseLine(String line){
        Matcher matcher = lineParserRegex.matcher(line);
        return matcher.results().mapToInt(m -> {
            if (m.group(2) != null) {
                return m.group(2).charAt(0);
            } else {
                return Integer.parseInt(m.group(1));
            }
        }).toArray();
    }

    static Pattern lineParserRegex2 = Pattern.compile("(\\d+)\\s*([+*]?)");

    static List<Problem> createTransposedProblems(List<String> lines){
        List<String> linesTransposed = Matrix.createFromStream(lines.stream()).transposeLeft().rows().map(Matrix::toCharString).toList();
        List<Problem> problems = new ArrayList<>();
        List<Long> operands = new ArrayList<>();

        linesTransposed.forEach(l ->{
            Matcher matcher = lineParserRegex2.matcher(l.trim());
            if(matcher.matches()){ //ignores empty lines
                operands.add(Long.parseLong(matcher.group(1)));
                if(matcher.group(2)!=null && !matcher.group(2).isBlank()){
                    char op = matcher.group(2).charAt(0);
                    problems.add(Problem.createFromLong(new ArrayList<>(operands), op));
                    operands.clear();
                }
            }
        });
        return problems;
    }

    @Override
    public Long part0(Stream<String> input) {
        return Matrix
                .createFromStream(input,Day06::parseLine)
                .columns()
                .map(Problem::create)
                .mapToLong(Problem::solve)
                .sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return createTransposedProblems(input.toList()).stream().mapToLong(Problem::solve).sum();
    }
}
