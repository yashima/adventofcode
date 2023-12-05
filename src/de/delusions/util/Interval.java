package de.delusions.util;

public class Interval implements Comparable<Interval> {
    long lower;

    long upper;

    public Interval( String lower, String upper ) {
        this( Long.parseLong( lower ), Long.parseLong( upper ) );
    }

    public Interval( long lower, long upper ) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains( long value ) {
        return this.lower <= value && this.upper >= value;
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

    public long length() {
        return Math.abs( upper - lower );
    }

    public long getUpper() {
        return upper;
    }

    public long getLower() {
        return lower;
    }

    @Override
    public int compareTo( Interval other ) {
        int lowerCompare = Long.compare( lower, other.lower );
        return lowerCompare == 0 ? Long.compare( upper, other.upper ) : lowerCompare;
    }

    @Override
    public String toString() {
        return "Interval{" + "lower=" + lower + ", upper=" + upper + '}';
    }

}
