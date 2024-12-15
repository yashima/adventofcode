package de.delusions.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MathUtil {

    public static List<Integer> calculatePrimeFactors(Integer number) {
        return calculateBiggerPrimeFactors(number.longValue()).stream().map(Long::intValue).toList();
    }

    public static List<Long> calculateBiggerPrimeFactors(Long number) {
        List<Long> factors = new ArrayList<>();
        if (number == 1) {
            return factors;
        }
        long factor = 2;
        while (factor * factor <= number) {
            while (number % factor == 0) {
                factors.add(factor);
                number = number / factor;
            }
            factor++;
        }
        if (number != 1) {
            factors.add(number);
        }
        return factors;
    }

    public static BigInteger calculateSmallestCommonMultiple(List<List<Integer>> numbers) {
        List<Integer> commonFactors = new ArrayList<>();
        for (int i = 0; i < numbers.size(); i++) {
            List<Integer> number = numbers.get(i);
            commonFactors.addAll(number);
            for (int j = i + 1; j < numbers.size(); j++) {
                List<Integer> other = numbers.get(j);
                other.removeAll(number);
            }
        }
        System.out.println(commonFactors);
        BigInteger result = BigInteger.ONE;
        for (Integer factor : commonFactors) {
            result = result.multiply(BigInteger.valueOf(factor));
        }
        return result;
    }

    /**
     * Calculates the standard deviation of the provided values.
     * See: <a href="https://rosettacode.org/wiki/Cumulative_standard_deviation#Java">Standard Deviation</a>
     *
     * @param values the array of double values
     * @return the standard deviation of the values
     */
    public static double sd(double[] values) {
        int n = 0;
        double sum = 0;
        double squares = 0;
        for (double value : values) {
            n++;
            sum += value;
            squares += value * value;
        }
        return Math.sqrt(squares / n - sum * sum / n / n);
    }

    /**
     * Calculates the median of the provided values.
     * See: <a href="https://rosettacode.org/wiki/Averages/Median#Java">Median Calculation</a>
     *
     * @param values the array of double values
     * @return the median of the values
     */
    public static double median(double[] values) {
        List<Double> list = Arrays.stream(values).boxed().toList();
        list.sort(Double::compareTo);
        int mid = list.size() / 2;
        return switch (list.size() % 2) {
            case 0 -> {
                double valueA = list.get(mid);
                double valueB = list.get(mid + 1);
                yield (valueA + valueB) / 2;
            }
            case 1 -> list.get(mid);
            default -> 0;
        };
    }

}
