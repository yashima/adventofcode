package de.delusions.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class Day<T> {

    static int TEST_1 = 0;
    static int TEST_2 = 1;
    static int PROD_1 = 2;
    static int PROD_2 = 3;

    T[] expected;

    int day;

    String tag;

    boolean testMode = false;

    public abstract T part0( Stream<String> input );

    public abstract T part1( Stream<String> input );

    public Day( int day, String tag,T... expected ) {
        this.day = day;
        this.tag = tag;
        this.expected = expected;
    }

    public void run( boolean test, int part ) {
        this.testMode = test; //keep this
        long timer = System.currentTimeMillis();
        T result = part==0 ? part0( getInput( test,part ) ) : part1( getInput( test,part ) );
        timer = System.currentTimeMillis()-timer;
        System.out.printf( "Day %01d '%s' Part %d: result=%s success=%s time=%dms%n", day , tag, part , result , verify( result, part, test ), timer);
    }

    public boolean verify(T result, int part, boolean test){
        int index = part + ( test ? 0 : 2 );
        if(result==null || expected.length<index-1){
            return false;
        }
        return result.equals( expected[index]);
    }

    public Stream<String> getInput( boolean test , int part) {
        try {

            return new Input( day, test, part ).getStream();

        }
        catch ( IOException e ) {
            System.err.println( "Input could not be retrieved: " + e.getMessage() );
            return new ArrayList<String>().stream();
        }
    }

}
