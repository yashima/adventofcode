package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Part 1: convert various parts of the input into bitmasks that toggle bits until a desired result is found with bfs / Breitensuche
 * Part 2: do the same search but now add numbers instead of toggling bits.
 */
@Slf4j
public class Day10 extends Day<Long> {
    public Day10() {
        super("tag", 7L, 33L, 466L, 0L);
    }

    static Pattern pattern = Pattern.compile("\\(([\\d,]+)\\)");

    record LightsOn(int value, int depth) {
    }


    record LightMachine(int result, List<Integer> buttons) {
        static LightMachine parse(String line) {
            return new LightMachine(
                    convertLightsToBitmask(line.substring(1, line.indexOf(']'))),
                    pattern.matcher(line).results().map(m -> convertButtonsToBitmask(m.group(1), ",", line.substring(1, line.indexOf(']')).length())).toList());
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
                    if (next.equals(result)) {
                        return cur.depth() + 1;
                    }
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(new LightsOn(next, cur.depth() + 1));
                    }
                }
            }
            return -1;
        }
    }

    static int convertLightsToBitmask(String lights) {

        return Integer.parseInt(lights.replace('.', '0').replace('#', '1'), 2);
    }

    static int convertButtonsToBitmask(String numbers, String splitBy, int size) {
        char[] button = new char[size];
        Arrays.fill(button, '0');
        Arrays.stream(numbers.split(splitBy)).forEach(n -> button[Integer.parseInt(n)] = '1');
        return Integer.parseInt(new String(button), 2);
    }

    static List<Integer> splitIntoInt(String input) {
        return Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).boxed().toList();
    }

    record LinearEquationMachine(String line, double[][] matrix, double[] joltages) {
        static LinearEquationMachine parse(String line) {
            double[] joltages = splitIntoInt(line.substring(line.indexOf('{') + 1, line.indexOf('}'))).stream().mapToDouble(i -> Double.valueOf(i + "")).toArray();
            List<List<Integer>> buttons = pattern.matcher(line).results().map(m -> splitIntoInt(m.group(1))).toList();

            int maxbit = joltages.length;
            double[][] gauss = new double[maxbit][buttons.size()];
            IntStream.range(0, maxbit).forEach(row -> Arrays.fill(gauss[row], 0));

            for (int col = 0; col < buttons.size(); col++) {
                int finalSpalte = col;
                buttons.get(col).forEach(i -> gauss[i][finalSpalte] = 1);
            }
            return new LinearEquationMachine(line,gauss, joltages);
        }

        double[] solveLeastSquares() {
            try {
                DecompositionSolver solver = new SingularValueDecomposition(new Array2DRowRealMatrix(matrix, false)).getSolver();
                RealVector solution = solver.solve(new ArrayRealVector(joltages, false));
                return solution.toArray();
            } catch (SingularMatrixException e){
                log.error("Matrix is truly singular, no solution exists", e);
                return new double[0];
            }

        }

        long solve() {
            double[] doubles = solveLeastSquares();
            //log.info("Solution: {}", Arrays.toString(doubles));
            return Arrays.stream(doubles).mapToLong(i ->Math.round(i)).sum();
        }


        @Override
        public String toString() {
            return "GaussMachine{" +
                    "line='" + line + '\'' +
                    ", matrix=" + Arrays.deepToString(matrix) +
                    ", joltages=" + Arrays.toString(joltages) +
                    '}';
        }
    }

    @Override
    public Long part0(Stream<String> input) {
        return input
                .map(LightMachine::parse)
                .mapToLong(LightMachine::numberOfLightButtonPresses)
                .sum();
    }


    @Override
    public Long part1(Stream<String> input) {
        //16946 too low
        //17503 too high
        return input
                .map(LinearEquationMachine::parse)
                .peek(System.out::println)
                .mapToLong(LinearEquationMachine::solve)
                .sum();

    }
}
