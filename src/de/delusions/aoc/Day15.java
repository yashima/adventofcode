package de.delusions.aoc;

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

    Map<Integer, List<LensInstruction>> bucketMap = new HashMap<>();

    public Day15( Integer... expected ) {
        super( 15, "Lens Library", expected );
    }

    @Override
    public Integer part0( Stream<String> input ) {
        return Arrays.stream( input.collect( Collectors.joining() ).split( "," ) ).mapToInt( Day15::hashString ).sum();
    }

    static int hashString( String input ) {
        return input.chars().reduce( 0, ( a, b ) -> ( ( a + b ) * 17 ) % 256 );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Arrays.stream( input.collect( Collectors.joining() ).split( "," ) ).map( LensInstruction::create ).forEach( this::executeInstruction );
        return bucketMap.values().stream().mapToInt( bucket -> bucket.stream().mapToInt( lens -> power( bucket, lens ) ).sum() ).sum();
    }

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

    private static int power( List<LensInstruction> bucket, LensInstruction lens ) {
        int bucketNumber = lens.hash() + 1;
        int bucketSpot = bucket.indexOf( lens ) + 1;
        return bucketNumber * bucketSpot * lens.focalLength();
    }

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
