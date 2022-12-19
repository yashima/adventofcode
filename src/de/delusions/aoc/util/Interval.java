package de.delusions.aoc.util;

public class Interval
    implements Comparable<Interval> {
    int lower;

    int upper;

    public Interval( String lower, String upper ) {
        this( Integer.parseInt( lower ), Integer.parseInt( upper ) );
    }

    public Interval( int lower, int upper ) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains( Interval bar ) {
        return this.lower <= bar.lower && this.upper >= bar.upper;
    }

    public boolean overlap( Interval bar ) {
        return this.lower <= bar.lower && bar.lower <= this.upper;
    }

    public Interval union( Interval bar ) {
        return new Interval( Math.min( this.lower, bar.lower ), Math.max( bar.upper, this.upper ) );
    }

    public int length() {
        return Math.abs( upper - lower );
    }

    public int getUpper() {
        return upper;
    }

    @Override
    public int compareTo( Interval other ) {
        int lowerCompare = Integer.compare( lower, other.lower );
        return lowerCompare == 0 ? Integer.compare( upper, other.upper ) : lowerCompare;
    }

    @Override
    public String toString() {
        return "Interval{" + "lower=" + lower + ", upper=" + upper + '}';
    }

}
