package de.delusions.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MathUtil {

    public static List<Integer> calculatePrimeFactors( int number ) {
        List<Integer> factors = new ArrayList<>();
        if ( number == 1 ) {
            return factors;
        }
        int factor = 2;
        while ( factor * factor <= number ) {
            while ( number % factor == 0 ) {
                factors.add( factor );
                number = number / factor;
            }
            factor++;
        }
        if ( number != 1 ) {
            factors.add( number );
        }
        return factors;
    }

    public static List<Long> calculateBiggerPrimeFactors( long number ) {
        List<Long> factors = new ArrayList<>();
        if ( number == 1 ) {
            return factors;
        }
        long factor = 2;
        while ( factor * factor <= number ) {
            while ( number % factor == 0 ) {
                factors.add( factor );
                number = number / factor;
            }
            factor++;
        }
        if ( number != 1 ) {
            factors.add( number );
        }
        return factors;
    }

    public static BigInteger calculateSmallestCommonMultiple( List<List<Integer>> numbers ) {
        List<Integer> commonFactors = new ArrayList<>();
        for ( int i = 0; i < numbers.size(); i++ ) {
            List<Integer> number = numbers.get( i );
            commonFactors.addAll( number );
            for ( int j = i + 1; j < numbers.size(); j++ ) {
                List<Integer> other = numbers.get( j );
                other.removeAll( number );
            }
        }
        System.out.println( commonFactors );
        BigInteger result = BigInteger.ONE;
        for ( Integer factor : commonFactors ) {
            result = result.multiply( BigInteger.valueOf( factor ) );
        }
        return result;
    }

}
