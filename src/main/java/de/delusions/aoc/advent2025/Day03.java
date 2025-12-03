package de.delusions.aoc.advent2025;

import de.delusions.util.Day;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Day 3: Harder than Day 2, simpler than Day 1 somehow. Firsts for 2025:
 * - off-by-one error,
 * - long overflow no idea why but BigInteger works
 * - recursion
 */
@Slf4j
public class Day03 extends Day<Long> {
    public Day03() {
        super("Lobby", 357L,3121910778619L,17427L,173161749617495L);
    }

    record BatteryBank(int[] batteries){
        static BatteryBank parse(String line){
            return new BatteryBank(line.chars().map(c -> c - '0').toArray());
        }

        BatteryBank head(int lengthToKeep){
            return new BatteryBank(Arrays.copyOfRange(batteries,0,lengthToKeep));
        }

        BatteryBank tail(int last){
            return new BatteryBank(Arrays.copyOfRange(batteries,last+1,batteries.length));
        }

        long calculateVoltage(List<Integer> flips){
            return flips.stream().map(f -> batteries[f]).reduce(0,(sum,f) -> sum*10+f);
        }

        BigInteger calculateVoltageBigInt(List<Integer> flips){
            return flips.stream().map(f -> BigInteger.valueOf(batteries[f])).reduce(BigInteger.ZERO,(sum,f) -> sum.multiply(BigInteger.TEN).add(f));
        }

        long largest(int flips){
            return calculateVoltageBigInt(findFlips(flips)).longValue();
        }

        List<Integer> findFlips(int flips){
            List<Integer> result = new ArrayList<>();
            while(result.size()<flips){
                int lengthToKeep = batteries.length-(flips-2);
                result.addAll(head(lengthToKeep).findFlips());
                if(flips>2){
                    int lastFlipIndex = result.getLast();
                    List<Integer> rawResult = tail(lastFlipIndex).findFlips(flips-2);
                    result.addAll(rawResult.stream().map(i -> i+lastFlipIndex+1).toList());
                }
            }
            return result;
        }

        List<Integer> findFlips() {
            List<Integer> flips = List.of(0,1);
            for(int x1=0;x1<batteries.length-1;x1++){
                for(int x2=x1+1;x2<batteries.length;x2++){
                    long voltage = calculateVoltage(List.of(x1,x2));
                    if(voltage> calculateVoltage(flips)){
                        flips = List.of(x1,x2);
                    }
                }
            }
            return flips;
        }
    }

    @Override
    public Long part0(Stream<String> input) {
        return input.map(BatteryBank::parse).mapToLong( b -> b.largest(2)).sum();
    }

    @Override
    public Long part1(Stream<String> input) {
        return input.map(BatteryBank::parse).mapToLong( b -> b.largest(12)).sum();
    }
}
