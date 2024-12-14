package de.delusions.aoc.advent2024;

import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day13 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day13.class);

    public Day13() {
        super("", 480L, 0L, 26299L, 0L);
    }

    final Pattern INPUT = Pattern.compile("Button A: X.(\\d+), Y.(\\d+)Button B: X.(\\d+), Y.(\\d+)Prize: X.(\\d+), Y.(\\d+)");

    record Vector(BigDecimal a1, BigDecimal a2) {

        static Vector create(int x, int y) {
            return new Vector(BigDecimal.valueOf(x), BigDecimal.valueOf(y));
        }

    }

    record Claw(Vector buttonA, Vector buttonB, Vector price) {

        Solution solveVectors() {
            return solveVectors(buttonA, buttonB, price);
        }

        //b = (z2 x1 -z1 x2) / (x1 y2 - x2y1)
        //a = (z1 - b y1) / x1
        static Solution solveVectors(Vector buttonA, Vector buttonB, Vector price) {
            BigDecimal divisor = buttonA.a1.multiply(buttonB.a2).subtract(buttonA.a2.multiply(buttonB.a1));
            BigDecimal divMe = price.a2.multiply(buttonA.a1).subtract(price.a1.multiply( buttonA.a2));

            BigDecimal factorB = divMe.divide(divisor, 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal factorA = price.a1.subtract(factorB.multiply(buttonB.a1));
            factorA = factorA.divide(buttonA.a1, 1, BigDecimal.ROUND_HALF_UP);
            return new Solution(factorA,factorB);
        }
    }

    record Solution(BigDecimal a, BigDecimal b) {
        long tokens() {
            if (isValid()) {
                return a.multiply(BigDecimal.valueOf(3)).add(b).longValue();
            }
            return 0L;
        }

        boolean isValid(){
            return isValid(a) && isValid(b);
        }

        private boolean isValid(BigDecimal number){
            return number!=null &&
                    number.compareTo(BigDecimal.ZERO)>0 &&
                    number.remainder(BigDecimal.ONE).doubleValue()==0;
        }

    }


    @Override
    public Long part0(Stream<String> input) {
        List<Claw> machines = INPUT.matcher(input.collect(Collectors.joining())).results().map(m ->
                new Claw(Vector.create(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                        Vector.create(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))),
                        Vector.create(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))))).toList();


        return machines.stream().map(Claw::solveVectors).filter(Solution::isValid).peek(System.out::println).mapToLong(Solution::tokens).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return 0L;
    }


}
