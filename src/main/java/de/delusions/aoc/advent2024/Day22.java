package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Day22 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day22.class);

    public Day22() {
        super("", 37327623L, 0L, 0L, 0L);
    }

    //
//    Each step of the above process involves mixing and pruning:
//
//    To mix a value into the secret number, calculate the bitwise XOR of the given value and the secret number. Then, the secret number becomes the result of that operation. (If the secret number is 42 and you were to mix 15 into the secret number, the secret number would become 37.)
//    To prune the secret number, calculate the value of the secret number modulo 16777216. Then, the secret number becomes the result of that operation. (If the secret number is 100000000 and you were to prune the secret number, the secret number would become 16113920.)

    class Generator {
        int start;
        BigInteger current;
        int step;

        static  BigInteger NO_64 = BigInteger.valueOf(64);
        static  BigInteger NO_32 = BigInteger.valueOf(32);
        static  BigInteger NO_2048 = BigInteger.valueOf(2048);
        static  BigInteger NO_16777216 = BigInteger.valueOf(16777216);

        Generator(int start) {
            this.start = start;
            this.current = BigInteger.valueOf(start);
            this.step = 0;
        }

        long getCurrent(){
            return current.longValue();
        }

        void step() {
            step++;
            current = prune(mix(current.multiply(NO_64)));
            current = prune(mix(current.divide(NO_32)));
            current = prune(mix(current.multiply(NO_2048)));
        }

        long getSequenceNumber(int index){
            current = BigInteger.valueOf(start);
            step = 0;
            while(step < index){
                step();
            }
            return getCurrent();
        }

        BigInteger mix(BigInteger value) {
            return value.xor(current);
        }

        BigInteger prune(BigInteger value) {
            return value.mod(NO_16777216);
        }
    }

    @Override
    public Long part0(Stream<String> input) {
        return input.mapToInt(Integer::parseInt).mapToObj(Generator::new).mapToLong( g -> g.getSequenceNumber(2000)).sum();
    }
    @Override
    public Long part1(Stream<String> input) {
        return 0L;
    }
}
