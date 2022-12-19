package de.delusions.aoc.advent2022;

import de.delusions.aoc.Day;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
public class Day13
    extends Day<Integer> {

    Day13() {
        super( 13, "Distress Signal" );
    }

    @Override
    public Integer part1( Stream<String> input ) {
        List<String> lines = input.filter( Predicate.not( String::isBlank ) ).toList();
        List<Packet> packets = new ArrayList<>();
        for ( int idx = 0; idx < lines.size(); idx = idx + 2 ) {
            packets.add( new Packet( lines.get( idx ), lines.get( idx + 1 ) ) );
        }
        return packets.stream().filter( Packet::isOrdered ).map( Packet::getPosition ).reduce( 0, Integer::sum );
    }

    @Override
    public Integer part2( Stream<String> input ) {
        PacketContent dividerA = new PacketContent( "[[2]]" );
        PacketContent dividerB = new PacketContent( "[[6]]" );
        List<PacketContent> packets = new ArrayList<>( input.filter( Predicate.not( String::isBlank ) ).map( PacketContent::new ).toList() );
        packets.add( dividerB );
        packets.add( dividerA );
        List<PacketContent> packetContents = packets.stream().sorted().peek( System.out::println ).toList();
        return ( packetContents.indexOf( dividerA ) + 1 ) * ( packetContents.indexOf( dividerB ) + 1 );
    }


    static AtomicInteger PACKET_ID = new AtomicInteger( 1 );

    static class Packet {
        PacketContent left;

        PacketContent right;

        int position = PACKET_ID.getAndIncrement();

        Packet( String left, String right ) {
            this.left = new PacketContent( left );
            this.right = new PacketContent( right );
        }

        boolean isOrdered() {
            return left.compareTo( right ) <= 0;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return position + ":" + left.toString() + " | " + right.toString();
        }
    }

    static class PacketContent
        implements Comparable<PacketContent> {
        Integer value = null;

        List<PacketContent> contents = new ArrayList<>();

        PacketContent( String packetString ) {
            Matcher packetMatcher = Pattern.compile( "\\[(.*)\\]" ).matcher( packetString );
            Matcher numberMatcher = Pattern.compile( "(\\d+)" ).matcher( packetString );
            if ( packetString.equals( "[]" ) ) {
                //do nothing
            }
            else if ( packetMatcher.matches() ) {
                String[] children = packetMatcher.group( 1 ).split( "," );
                int depth = 0;
                StringBuilder childPacketString = new StringBuilder();
                for ( String child : children ) {
                    depth = depth + child.chars().map( c -> c == '[' ? 1 : ( c == ']' ? -1 : 0 ) ).reduce( 0, Integer::sum );
                    childPacketString.append( ( childPacketString.length() == 0 ) ? "" : "," ).append( child );
                    if ( depth == 0 ) {
                        contents.add( new PacketContent( childPacketString.toString() ) );
                        childPacketString = new StringBuilder();
                    }
                }
            }
            else if ( numberMatcher.find() ) {
                value = Integer.parseInt( numberMatcher.group( 1 ) );
            }
        }

        @Override
        public int compareTo( PacketContent o ) {
            int result = 0;
            if ( o == null ) {
                throw new IllegalStateException( "Oops" );
            }
            else if ( value != null && o.value != null ) {
                result = value.compareTo( o.value );
            }
            else if ( value == null && o.value == null ) {
                result = this.compareContents( o.contents );
            }
            else if ( value == null ) {
                result = this.compareContents( List.of( o ) );
            }
            else {
                result = -o.compareContents( List.of( this ) );
            }
            return result;
        }

        private int compareContents( List<PacketContent> otherContents ) {
            LinkedList<PacketContent> otherStack = new LinkedList<>( otherContents );
            LinkedList<PacketContent> stack = new LinkedList<>( this.contents );
            while ( !otherStack.isEmpty() && !stack.isEmpty() ) {
                PacketContent content = stack.pop();
                PacketContent otherContent = otherStack.pop();
                int result = content.compareTo( otherContent );
                if ( result != 0 ) {
                    return result;
                }
            }
            return Integer.compare( stack.size(), otherStack.size() );
        }

        @Override
        public String toString() {
            return value == null ? contents.toString() : value.toString();
        }
    }

}
