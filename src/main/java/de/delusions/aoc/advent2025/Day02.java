package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Day 2: Much easier to program than day 1. Especially since I was able to generalize part 2 from part 1 mostly.
 * I needed unit tests again to make sure my solution covers both part 1 and part 2.
 *
 * Part 1: Find numbers inside of ranges that consist of 2 repetitions like 11 or 200200
 * Part 2: Find numbers that consist of n repetitions. Like 111 or 61616161 or 446446
 */
@Slf4j
public class Day02 extends Day<Long> {

    public Day02() {
        super("Gift Shop", 1227775554L,4174379265L,31000881061L,46769308485L);
    }

    record Range(Long min, Long max){
        static Range parse(String range){
            String[] parts = range.split("-");
            return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }

        Long sumOfInvalidProducts(){ return stream().filter(Day02::isInValidProductId).sum(); }

        Long sumOfFunnyProducts(){ return stream().filter(Day02::isFunnyProductId).sum(); }

        /** Returns a stream of all numbers in the range */
        LongStream stream(){ return LongStream.rangeClosed(min, max); }
    }

    /** For Part1 SequenceLength repeat 2 */
    static boolean isInValidProductId(Long productId){
        String product = String.valueOf(productId);
        return product.length()%2==0 && isInValidProduct(product,product.length()/2);
    }

    /** For Part2 SequenceLength needs to be checked for all possible lengths for every number */
    static boolean isFunnyProductId(Long productId){
        String product = String.valueOf(productId);
        return IntStream.range(1,product.length()/2+1).filter(sL -> isInValidProduct(product,sL)).findFirst().isPresent();
    }

    /** Checks if the product is a sequence of digits that is repeated sequenceLength times */
    static boolean isInValidProduct(String product,int sequenceLength){
        int length = product.length();
        if(length % sequenceLength != 0) return false;
        int repeat = length / sequenceLength;
        boolean result = product.substring(0, sequenceLength).repeat(repeat).equals(product);
        //if (result) log.debug("Checking {}x{} for sequenceLength {}: {}", product, sequenceLength, sequenceLength, product.substring(0, sequenceLength).repeat(repeat));
        return result;
    }

    /** Converts the stream into a single line split by comma and back to a stream  */
    Stream<String> convert(Stream<String> input) {
        String line = input.findFirst().orElseThrow();
        return Arrays.stream(line.split(","));
    }

    @Override
    public Long part0(Stream<String> input) {
        return convert(input).map(Range::parse).mapToLong(Range::sumOfInvalidProducts).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return convert(input).map(Range::parse).mapToLong(Range::sumOfFunnyProducts).sum();
    }
}
