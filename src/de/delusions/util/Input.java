package de.delusions.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class Input {

    public static Path DOWNLOAD = Paths.get( System.getProperty( "user.home" ) + "\\Downloads\\input" );

    private final int day;

    private final boolean test;

    private final int part;

    Input( int day, boolean test, int part ) {
        this.day = day;
        this.test = test;
        this.part = part;
    }

    Stream<String> getStream()
        throws IOException {
        Path path = Paths.get(System.getProperty( "user.dir" ) +  String.format("\\inputs\\%s\\day-%02d%s.txt", test ? "test" : "prod", day, test? "-"+part : "" ) );
        System.out.println(path.toString());
        if ( test ) {
            if ( !fileExists( path ) ) {
                Files.createFile( path );
            }
            if ( Files.size( path ) == 0 ) {
                throw new RuntimeException( "Please copy test input into " + path.toString() );
            }
        }
        else {
            if ( !fileExists( path ) && !fileExists( DOWNLOAD ) ) {
                throw new RuntimeException( "Please download today's input into " +path.toString() );
            }
            if ( fileExists( DOWNLOAD ) ) {
                Files.move( DOWNLOAD, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        //by now file should exist:
        return Files.lines( path );
    }

    private static boolean fileExists( Path filePath ) {
        return Files.exists( filePath ) && Files.isRegularFile( filePath );
    }
}
