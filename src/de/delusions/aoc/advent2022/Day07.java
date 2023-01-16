package de.delusions.aoc.advent2022;

import de.delusions.aoc.util.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day07 extends Day<Integer> {

    public Day07() {
        super( 7, "No Space Left On Device" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        Directory root = new Directory( "/", null );
        List<Directory> directories = parseStructure( input, root );
        return directories.stream().map( Directory::size ).filter( s -> s < 100000 ).reduce( 0, Integer::sum );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        Directory root = new Directory( "/", null );
        List<Directory> directories = parseStructure( input, root );
        int needed = 30000000;
        int space = 70000000 - root.size();
        return directories.stream().map( Directory::size ).filter( size -> space + size > needed ).min( Integer::compareTo ).get();
    }

    List<Directory> parseStructure( Stream<String> input, Directory root ) {
        final List<Directory> directories = new ArrayList<>();
        final AtomicReference<Directory> current = new AtomicReference<>( root );
        input.skip( 2 ).forEach( line -> {
            if ( line.startsWith( "$" ) ) {
                if ( line.equals( "$ cd .." ) ) {
                    current.set( current.get().parent );
                }
                else if ( line.startsWith( "$ cd" ) ) {
                    current.set( current.get().directories.get( line.substring( 5 ) ) );
                }
            }
            else if ( line.startsWith( "dir" ) ) {
                directories.add( current.get().addDirectory( line ) );
            }
            else {
                current.get().addFile( line );
            }
        } );
        return directories;
    }

    static class Directory { //so far only used by Day7
        Map<String, Directory> directories = new HashMap<>();

        Map<String, Integer> files = new HashMap<>();

        Directory parent;

        String name;

        Directory( String name, Directory parent ) {
            this.name = name;
            this.parent = parent;
        }

        void addFile( String line ) {
            String[] pair = line.split( " " );
            files.put( pair[1], Integer.parseInt( pair[0] ) );
        }

        Directory addDirectory( String line ) {
            Directory directory = new Directory( line.split( " " )[1], this );
            directories.put( directory.name, directory );
            return directory;
        }

        int size() {
            return files.values().stream().reduce( 0, Integer::sum ) + directories.values().stream().map( Directory::size ).reduce( 0, Integer::sum );
        }
    }

}
