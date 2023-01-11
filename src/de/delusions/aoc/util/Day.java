package de.delusions.aoc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class Day<T> {

    public static String PATH = System.getProperty( "user.dir" ) + "\\inputs\\day%s-input.txt";

    public static String TEST_PATH = System.getProperty( "user.dir" ) + "\\inputs\\day%s-test.txt";

    int day;

    String tag;

    boolean testMode = false;

    public abstract T part1( Stream<String> input );

    public abstract T part2( Stream<String> input );


    public Day( int day, String tag ) {
        this.day = day;
        this.tag = tag;
    }

    public void run( boolean test, boolean time ) {
        this.testMode = test;
        System.out.println( System.getProperty( "user.dir" ) );
        long timer = System.currentTimeMillis();
        System.out.println( "Day " + day + ", part1: " + tag + "=" + part1( getInput( test ) ) );
        System.out.println( "Day " + day + ", part2: " + tag + "=" + part2( getInput( test ) ) );
        if ( time ) {
            System.out.println( "Took " + ( System.currentTimeMillis() - timer ) + "ms" );
        }
    }

    public boolean isTestMode() {
        return testMode;
    }

    public Stream<String> getInput( boolean test ) {
        try {

            return Files.lines( Paths.get( String.format( test ? TEST_PATH : PATH, day ) ) );

        }
        catch ( IOException e ) {
            System.out.println( "File not found: " + e.getMessage() );
            return new ArrayList<String>().stream();
        }
    }

}
