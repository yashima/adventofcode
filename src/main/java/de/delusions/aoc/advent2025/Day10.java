package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import lombok.extern.slf4j.Slf4j;

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
            log.debug("line: {}",line);
            log.debug("maxbit: {} buttons: {}",maxbit,buttons.size());
            double[][] gauss = new double[maxbit][Math.max(maxbit,buttons.size())];
            IntStream.range(0,maxbit).forEach( row -> Arrays.fill(gauss[row], 0));

            for (int col = 0; col < buttons.size(); col++) {
                int finalSpalte = col;
                buttons.get(col).forEach(i -> gauss[i][finalSpalte]=1);
            }
            return new GaussMachine(gauss,joltages);
        }

        boolean irregular(){
            return matrix.length != matrix[0].length;
        }

        int buttonCount(){
            return matrix[0].length;
        }

        @Override
        public String toString() {
            return "GaussMachine[ joltages="+Arrays.toString(joltages())+", matrix="+ Arrays.deepToString(matrix)+"]";
        }

        /** Switches two lines in the matrix and the joltages array */
        void switchLines(int lineA,int lineB){
            //erst matrix
            double[] temp = matrix[lineA];
            matrix[lineA] = matrix[lineB];
            matrix[lineB] = temp;
            //dann joltages
            double t = joltages[lineA];
            joltages[lineA] = joltages[lineB];
            joltages[lineB] = t;
        }

        /** Finds the absolute largest value in a column starting with p */
        int findPivot(int pivotIndex){
            int max = pivotIndex;
            for (int row = pivotIndex + 1; row < joltages.length; row++) {
                if (Math.abs(matrix[row][pivotIndex]) > Math.abs(matrix[max][pivotIndex])) {
                    max = row;
                }
            }
            return max;
        }

        void usePivot(int pivotIndex){
            if(pivotIndex == buttonCount()) return;
            double pivotElementValue = matrix[pivotIndex][pivotIndex];
            //for all lines below pivot index:
            for (int row = pivotIndex + 1; row < joltages.length; row++) {
                double pivotFactorForRow = matrix[row][pivotIndex] / pivotElementValue;
                //change joltages: subtract the product of value in pivot-row and pivot-factor from current row
                joltages[row] -= pivotFactorForRow * joltages[pivotIndex];
                //change matrix row
                for (int col = pivotIndex; col < buttonCount(); col++) {
                    //for this column: subtract the product of the pivot-factor and value in pivotRow for this column
                    matrix[row][col] -= pivotFactorForRow * matrix[pivotIndex][col];
                }
            }
        }

        double[] backFill(){
            // Rückwärtseinsetzen
            double[] result = new double[joltages.length];
            for (int row = joltages.length - 1; row >= 0; row--) {
                double sum = 0.0;
                for (int col = row + 1; col < buttonCount(); col++) {
                    sum += matrix[row][col] * result[col];
                }
                result[row] = (joltages[row] - sum) / matrix[row][row];
            }
            return result;
        }

        int solve() {
            //reorder the matrix and find pivot elements to simplify
            for(int row = 0; row < joltages.length; row++) {
                int pivot = findPivot(row);
                if(pivot != row) { //only when
                    switchLines(row,pivot);
                }
                usePivot(row);
            }
            //return 0;
            return Arrays.stream(backFill()).mapToInt(i -> (int)i).sum();
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
        return input
                .map(GaussMachine::parse)
                .peek(System.out::println)
                .mapToLong(GaussMachine::solve)
                .sum();

    }
}
