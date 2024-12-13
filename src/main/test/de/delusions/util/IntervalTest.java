package de.delusions.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntervalTest {

    @Test
    public void testOverlap() {
        Interval a = new Interval(3, 8);
        Interval b = new Interval(5, 10);
        Interval c = new Interval(2, 12);
        Interval d = new Interval(10, 12);
        assertThat(a.overlap(b)).isTrue();
        assertThat(b.overlap(a)).isTrue();
        assertThat(a.overlap(c)).isTrue();
        assertThat(c.overlap(a)).isTrue();
        assertThat(a.overlap(d)).isFalse();

    }

    @Test
    public void testContains() {
        Interval a = new Interval(1, 10);
        assertThat(a.contains(5)).isTrue();
        assertThat(a.contains(15)).isFalse();
        assertThat(a.contains(1)).isTrue();
        assertThat(a.contains(10)).isTrue();
        assertThat(a.equals(new Interval(1, 10))).isTrue();
        assertThat(a.contains(new Interval(1, 10))).isTrue();
        assertThat(a.contains(new Interval(2, 8))).isTrue();
        assertThat(a.contains(new Interval(2, 12))).isFalse();
        assertThat(a.contains(new Interval(12, 13))).isFalse();
    }

    @Test
    public void testLength() {
        Interval a = new Interval(1, 10);
        assertThat(a.length()).isEqualTo(10L);
    }

    @Test
    public void testUnion() {
        Interval a = new Interval(1, 10);
        Interval b = new Interval(5, 12);
        assertThat(a.union(b)).isEqualTo(new Interval(1, 12));
        assertThat(b.union(a)).isEqualTo(new Interval(1, 12));

        Interval c = new Interval(6, 10);
        Interval d = new Interval(5, 12);
        assertThat(c.union(d)).isEqualTo(new Interval(5, 12));
        assertThat(d.union(c)).isEqualTo(new Interval(5, 12));
    }

    @Test
    public void testIntersect() {
        Interval a = new Interval(1, 10);
        Interval b = new Interval(5, 12);
        Interval c = new Interval(5, 8);
        assertThat(a.intersect(b)).isEqualTo(new Interval(5, 10));
        assertThat(b.intersect(a)).isEqualTo(new Interval(5, 10));
        assertThat(a.intersect(c)).isEqualTo(c);
        assertThat(c.intersect(a)).isEqualTo(c);
    }

    @Test
    public void testSubtract() {
        Interval a = new Interval(1, 10);
        Interval b = new Interval(5, 12);
        assertThat(a.subtract(b)).isEqualTo(new Interval(1, 4));
        assertThat(b.subtract(a)).isEqualTo(new Interval(11, 12));
    }

}
