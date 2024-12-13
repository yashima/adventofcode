package de.delusions.aoc.advent2023;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 extends Day<Integer> {
    static final Pattern p = Pattern.compile( "([a-z]+)(.)([0-9])?" );

    final Map<Integer, List<LensInstruction>> bucketMap = new HashMap<>();

    public Day15( Integer... expected ) {
        super( 15, "Lens Library", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        //just a test of the hash function
        return Arrays.stream( input.collect( Collectors.joining() ).split( "," ) ).mapToInt( Day15::hashString ).sum();
    }

    /**
     * Formula for calculating the lense hash for part 1
     *
     * @param input the string to hash
     * @return a number representing the string to be put into buckets
     */
    static int hashString( String input ) {
        return input.chars().reduce( 0, ( a, b ) -> ( ( a + b ) * 17 ) % 256 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        bucketMap.clear(); //stupidest bug ever to forget to reset the class variable between runs

        //processing the input, creating the instructions and executing them to build the bucketMap:
        Arrays.stream( input.collect( Collectors.joining() ).split( "," ) ).map( LensInstruction::create ).forEach( this::executeInstruction );

        //calculating the power of the bucketMap, bucket by bucket, lense by lense
        return bucketMap.values()
                        .stream()
                        .mapToInt( bucket -> bucket.stream().mapToInt( lens -> power( bucket.indexOf( lens ), lens ) ).sum() )
                        .sum();
    }

    /**
     * Take an instruction and execute the command which either removes a lense from a bucket, adds a lense to a bucket or replaces one
     *
     * @param instruction the instruction to execute
     */
    void executeInstruction( LensInstruction instruction ) {
        List<LensInstruction> bucket = bucketMap.getOrDefault( instruction.hash(), new ArrayList<>() );
        int index = bucket.indexOf( instruction );
        if ( instruction.op() == '-' ) { //remove a lense with given label from this bucket
            if ( index >= 0 ) {bucket.remove( index );}
        }
        else {
            if ( bucket.isEmpty() ) { //new bucket, add lense
                bucketMap.put( instruction.hash(), bucket );
            }
            if ( index < 0 ) { //bucket doesn't contain lense w/label, add lense at the end
                bucket.add( instruction );
            }
            else { //replacing lense w/label
                bucket.remove( index );
                bucket.add( index, instruction );
            }
        }
    }

    /**
     * Calculates the power of a given lens in a bucket
     *
     * @param position position in the bucket (positions start counting at 0, because java lists)
     * @param lens     the lense provides its focal length and the hash
     * @return the formula is the product of the hash+1, the focallength and the position in the bucket
     */
    private static int power( int position, LensInstruction lens ) {
        return ( lens.hash() + 1 ) * ( position + 1 ) * lens.focalLength();
    }

    /**
     * Each instruction is immutable and contains several fields
     * @param label the label which has to be unique in each bucket
     * @param hash the hash calculated from the label which determines the bucket
     * @param op the operation '=' for adding or replacing a lense or '-' for removing one
     * @param focalLength the focallength is needed to calculate the power
     */
    record LensInstruction(String label, int hash, char op, int focalLength) {
        static LensInstruction create( String instruction ) {
            Matcher m = p.matcher( instruction );
            if ( m.find() ) {
                String f = m.group( 3 );
                String label = m.group( 1 );
                return new LensInstruction( label, hashString( label ), m.group( 2 ).charAt( 0 ), f == null ? -1 : Integer.parseInt( f ) );
            }
            throw new IllegalStateException( "Bug Alarm: '" + instruction + "' could not be parsed" );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {return true;}
            if ( !( o instanceof LensInstruction that ) ) {return false;}
            return Objects.equals( label, that.label );
        }

        @Override
        public int hashCode() {
            return Objects.hash( label );
        }
    }
}
