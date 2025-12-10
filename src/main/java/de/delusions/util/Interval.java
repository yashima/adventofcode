package de.delusions.util;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Class representing an interval with a lower and upper boundary, both inclusive.
 */
public class Interval implements Comparable<Interval> {
    long lower;

    long upper;

    public static Interval from(String input){
        return new Interval(input.split("-")[0], input.split("-")[1]);
    }

    public Interval(String lower, String upper) {
        this(Long.parseLong(lower), Long.parseLong(upper));
    }

    public Interval(long lower, long upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains(long value) {
        return this.lower <= value && this.upper >= value;
    }

    public boolean contains(Interval bar) {
        return this.lower <= bar.lower && this.upper >= bar.upper;
    }

    public boolean containsExclusive(long value) {
       return this.lower < value && this.upper > value;
    }

    public boolean overlap(Interval bar) {
        return this.lower <= bar.lower && bar.lower <= this.upper || this.lower > bar.lower && this.lower <= bar.upper;
    }

    public IntStream stream() {
        return IntStream.range((int) this.lower, (int) this.upper + (upper == lower ? +1 : 0));
    }

    public Interval intersect(Interval bar) {
        if (!overlap(bar)) {
            throw new IllegalStateException("Can only make intersection of overlapping Intervals");
        }
        return new Interval(Math.max(this.lower, bar.lower), Math.min(this.upper, bar.upper));
    }

    public Interval translate(int value) {
        return new Interval(lower + value, upper + value);
    }

    public Interval subtract(Interval bar) {
        return lower < bar.lower ? new Interval(lower, Math.min(bar.lower, upper) - 1) : new Interval(Math.max(lower, bar.upper) + 1, upper);
    }

    public Interval union(Interval bar) {
        if (!overlap(bar)) {
            throw new IllegalStateException("Can only make union of overlapping Interval");
        }
        return new Interval(Math.min(this.lower, bar.lower), Math.max(bar.upper, this.upper));
    }

    public long length() {
        return Math.abs(upper - lower) + 1;
    }

    public long getUpper() {
        return upper;
    }

    public void setUpper(long upper) {
        this.upper = upper;
    }

    public long getLower() {
        return lower;
    }

    public void setLower(long lower) {
        this.lower = lower;
    }

    @Override
    public int compareTo(Interval other) {
        int lowerCompare = Long.compare(lower, other.lower);
        return lowerCompare == 0 ? Long.compare(upper, other.upper) : lowerCompare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLower(), getUpper());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interval interval)) {
            return false;
        }
        return getLower() == interval.getLower() && getUpper() == interval.getUpper();
    }

    @Override
    public String toString() {
        return "Interval{" + "lower=" + lower + ", upper=" + upper + '}';
    }

    public Interval copy() {
        return new Interval(this.lower, this.upper);
    }

}
