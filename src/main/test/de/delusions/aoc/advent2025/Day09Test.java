package de.delusions.aoc.advent2025;

import de.delusions.aoc.advent2025.Day09.Pair;
import de.delusions.util.dimensions.TupelLong;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Slf4j
public class Day09Test {

    @ParameterizedTest
    @CsvSource({
            "01,02"
            })
    void testExampleInput(String input,int expected){
        //Assertions.assertThat(Day09.XXX.parse(input).YYY()).isEqualTo(expected);

    }

    static Pair pair = new Pair( TupelLong.from(1L,1L),TupelLong.from(4L,4L));
    static Day09 day = new Day09();

    @Test
    public void testInvertPair(){
        Pair inverted = pair.invertCorners();
        Assertions.assertThat(inverted.a().x()).isEqualTo(1);
        Assertions.assertThat(inverted.a().y()).isEqualTo(4);
        Assertions.assertThat(inverted.b().y()).isEqualTo(1);
        Assertions.assertThat(inverted.b().x()).isEqualTo(4);
    }

    @Test
    public void testRectangleEdges(){
        Pair inverted = pair.invertCorners();
        List<Pair> rectangleEdges = pair.getRectangleEdges();
        Assertions.assertThat(rectangleEdges).hasSize(4);
        Assertions.assertThat(rectangleEdges.get(0).orientation()).isEqualTo(Day09.Orientation.VERTICAL);
        Assertions.assertThat(rectangleEdges.get(0).a()).isEqualTo(pair.a());
        Assertions.assertThat(rectangleEdges.get(0).b()).isEqualTo(inverted.a());
        Assertions.assertThat(rectangleEdges.get(1).orientation()).isEqualTo(Day09.Orientation.HORIZONTAL);
        Assertions.assertThat(rectangleEdges.get(1).a()).isEqualTo(pair.a());
        Assertions.assertThat(rectangleEdges.get(1).b()).isEqualTo(inverted.b());
        Assertions.assertThat(rectangleEdges.get(2).orientation()).isEqualTo(Day09.Orientation.VERTICAL);
        Assertions.assertThat(rectangleEdges.get(2).a()).isEqualTo(pair.b());
        Assertions.assertThat(rectangleEdges.get(2).b()).isEqualTo(inverted.b());
        Assertions.assertThat(rectangleEdges.get(3).orientation()).isEqualTo(Day09.Orientation.HORIZONTAL);
        Assertions.assertThat(rectangleEdges.get(3).a()).isEqualTo(pair.b());
        Assertions.assertThat(rectangleEdges.get(3).b()).isEqualTo(inverted.a());
    }

    @Test
    public void testRectangleIntersection() {
        Pair inverted = pair.invertCorners();
        List<Pair> rectangleEdges = pair.getRectangleEdges();
        Assertions.assertThat(rectangleEdges.get(0).intersects(rectangleEdges.get(1), false)).isEqualTo(TupelLong.from(1L, 1L));
        Assertions.assertThat(rectangleEdges.get(0).intersects(rectangleEdges.get(3), false)).isEqualTo(TupelLong.from(1L, 4L));
        Assertions.assertThat(rectangleEdges.get(2).intersects(rectangleEdges.get(1), false)).isEqualTo(TupelLong.from(4L, 1L));
        Assertions.assertThat(rectangleEdges.get(2).intersects(rectangleEdges.get(3), false)).isEqualTo(TupelLong.from(4L, 4L));
        Assertions.assertThat(rectangleEdges.get(0).intersects(rectangleEdges.get(2), false)).isNull();
        Assertions.assertThat(rectangleEdges.get(1).intersects(rectangleEdges.get(3), false)).isNull();

        Pair edge1 = Pair.createPair(1L, 1L, 1L, 5L);
        Pair edge2 = Pair.createPair(0L, 3L, 3L, 3L);
        Pair edge3 = Pair.createPair(0L, 6L, 3L, 6L);
        Assertions.assertThat(edge1.intersects(edge2, false)).isEqualTo(TupelLong.from(1L, 3L));
        Assertions.assertThat(edge1.intersects(edge3, false)).isNull();
    }

    @Test
    public void testEdgeIntersectionWithNonMatchingX() {
        Pair edge4 = Pair.createPair(9L,5L,2L,5L); //y=5 gleich heisst: vertical von 2 bis 9
        Pair edge5 = Pair.createPair(7L,5L,7L,3L); //x=7 gleich heisst: horizontal von 3 bis 5
        //y=5 liegt nicht ECHT im intervall von Edge5
        //x=7 liegt ECHT im intervall von Edge4
        Assertions.assertThat(edge4.intersects(edge5,true)).isNull();
    }

    @Test
    public void testVertexInside(){
        TupelLong pInside = TupelLong.from(2L,3L);
        TupelLong pOutside = TupelLong.from(2L,5L);
        Assertions.assertThat(pair.isInsideRectangle(pInside)).isTrue();
        Assertions.assertThat(pair.isInsideRectangle(pOutside)).isFalse();
    }

    @Test
    public void testRaycast(){
        List<Pair> edges = pair.getRectangleEdges();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(2L,3L))).isTrue();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(1L,3L))).isTrue();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(1L,1L))).isTrue();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(1L,20L))).isFalse();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(0L,0L))).isFalse();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(1L,5L))).isFalse();
    }

    @Test
    public void testIsOnEdge(){
        List<String> inputs = Arrays.stream("7,1\n11,1\n11,7\n9,7\n9,5\n2,5\n2,3\n7,3\n".split("\n")).toList();
        day.setCoordinates(inputs.stream().map(s -> TupelLong.from(s)).toList());

        Assertions.assertThat(Pair.createPair(2L,3L,2L,5L).isOnEdge(TupelLong.from(2L,7L))).isFalse();
        Assertions.assertThat(Pair.createPair(2L,3L,2L,5L).isOnEdge(TupelLong.from(12L,12L))).isFalse();
        Assertions.assertThat(Pair.createPair(2L,3L,2L,5L).isOnEdge(TupelLong.from(2L,6L))).isFalse();
        Assertions.assertThat(Pair.createPair(2L,3L,2L,5L).isOnEdge(TupelLong.from(2L,4L))).isTrue();
    }

    @Test
    public void testInput(){
        List<String> inputs = Arrays.stream("7,1\n11,1\n11,7\n9,7\n9,5\n2,5\n2,3\n7,3\n".split("\n")).toList();
        day.setCoordinates(inputs.stream().map(s -> TupelLong.from(s)).toList());
        TreeMap<Long,Pair> candidates = new TreeMap<>();
        List<Pair> edges = day.createEdges(candidates);
        Assertions.assertThat(day.castRay(edges,TupelLong.from(7L,1L))).isTrue();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(2L,7L))).isFalse();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(12L,12L))).isFalse();
        Assertions.assertThat(day.castRay(edges,TupelLong.from(0L,0L))).isFalse();
        Pair solution = candidates.get(24L);
        Assertions.assertThat(solution).isNotNull();
        //Assertions.assertThat(day.castRay(edges,TupelLong.from(5L,12L))).isFalse();
        Assertions.assertThat(day.castRay(edges,solution.invertCorners().a())).isTrue();
        Assertions.assertThat(day.castRay(edges,solution.invertCorners().b())).isTrue();

        Pair notSolution = candidates.get(40L);
        Assertions.assertThat(notSolution).isNotNull();
        Assertions.assertThat(day.castRay(edges,notSolution.invertCorners().a())).isTrue();
        Assertions.assertThat(day.castRay(edges,notSolution.invertCorners().b())).isFalse();
    }

}