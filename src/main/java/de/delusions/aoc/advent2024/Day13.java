package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day13 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day13.class);

    public Day13() {
        super("Claw Contraption", 480L, 875318608908L, 26299L, 107824497933339L);
    }

    final Pattern INPUT = Pattern.compile("Button A: X.(\\d+), Y.(\\d+)Button B: X.(\\d+), Y.(\\d+)Prize: X.(\\d+), Y.(\\d+)");

    static final BigDecimal OFF_BY_ONE = BigDecimal.valueOf(10000000000000L);

    record Vector(BigDecimal x, BigDecimal y) {

        static Vector create(int x, int y) {
            return new Vector(BigDecimal.valueOf(x), BigDecimal.valueOf(y));
        }

        static Vector createOff(int x, int y) {
            return new Vector(BigDecimal.valueOf(x).add(OFF_BY_ONE), BigDecimal.valueOf(y).add(OFF_BY_ONE));
        }

    }

    record Claw(Vector buttonA, Vector buttonB, Vector price) {

        Solution solveVectors() {
            return solveVectors(buttonA, buttonB, price);
        }

        //b = (z2 x1 -z1 x2) / (x1 y2 - x2y1)
        //a = (z1 - b y1) / x1
        static Solution solveVectors(Vector buttonA, Vector buttonB, Vector price) {
            BigDecimal divisor = buttonA.x.multiply(buttonB.y).subtract(buttonA.y.multiply(buttonB.x));
            BigDecimal divMe = price.y.multiply(buttonA.x).subtract(price.x.multiply( buttonA.y));

            BigDecimal factorB = divMe.divide(divisor, 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal factorA = price.x.subtract(factorB.multiply(buttonB.x));
            factorA = factorA.divide(buttonA.x, 1, BigDecimal.ROUND_HALF_UP);
            return new Solution(factorA,factorB);
        }
    }

    record Solution(BigDecimal a, BigDecimal b) {
        long tokens() {
            return a.multiply(BigDecimal.valueOf(3)).add(b).longValue();
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


        return machines.stream().map(Claw::solveVectors).filter(Solution::isValid).mapToLong(Solution::tokens).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        List<Claw> machines = INPUT.matcher(input.collect(Collectors.joining())).results().map(m ->
                new Claw(Vector.create(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                        Vector.create(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))),
                        Vector.createOff(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))))).toList();

        return machines.stream().map(Claw::solveVectors).filter(Solution::isValid).mapToLong(Solution::tokens).sum();
    }


}
