package de.delusions.aoc.advent2025;

import de.delusions.util.Day;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
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

    record GaussMachine(double[][] matrix,double[] joltages) {
        static GaussMachine parse(String line) {
            double[] joltages = splitIntoInt(line.substring(line.indexOf('{') + 1, line.indexOf('}'))).stream().mapToDouble( i -> Double.valueOf(i+"")).toArray();
            List<List<Integer>> buttons = pattern.matcher(line).results().map(m -> splitIntoInt(m.group(1))).toList();
            int maxbit = joltages.length;

            double[][] gauss = new double[maxbit][buttons.size()];
            IntStream.range(0,maxbit).forEach( zeile -> Arrays.fill(gauss[zeile], 0));

            for (int spalte = 0; spalte < buttons.size(); spalte++) {
                int finalSpalte = spalte;
                buttons.get(spalte).forEach(i -> gauss[i][finalSpalte]=1);
            }
            return new GaussMachine(gauss,joltages);
        }

        @Override
        public String toString() {
            return "GaussMachine[ joltages="+Arrays.toString(joltages())+", matrix="+ Arrays.deepToString(matrix)+"]";
        }

        int solve() {
            // solveGauss(matrix,joltages);
            return 0;
        }
    }

  static  double[] solveGauss(double[][] A, double[] b) {
        int n = b.length;

        // Vorwärtselimination
        for (int p = 0; p < n; p++) {

            // Pivot-Suche (optional, aber sinnvoll)
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            // Zeilen tauschen in A und b
            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double t = b[p];      b[p] = b[max]; b[max] = t;

            // jetzt A[p][p] als Pivot benutzen
            for (int i = p + 1; i < n; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < n; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // Rückwärtseinsetzen
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
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
        return input
                .map(GaussMachine::parse)
                .peek(System.out::println)
                .mapToLong(GaussMachine::solve)
                .sum();

    }
}
