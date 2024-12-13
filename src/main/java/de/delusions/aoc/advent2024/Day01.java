package de.delusions.aoc.advent2024;

import de.delusions.util.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day01 extends Day<Integer> {

    public Day01() {
        super(1, "Historian Hysteria",11,31 ,1341714,27384707);
    }

    record HistoriansHaveBadHandWriting(List<Integer> a, List<Integer> b){
        void addLine(String line){
            String[] parts = line.split("\\s+");
            a.add(Integer.parseInt(parts[0].trim()));
            b.add(Integer.parseInt(parts[1].trim()));
        }

        int size(){
            return a.size();
        }

        int countEntries(int i){
            return (int)b.stream().filter(j -> j == i).count();
        }
    }

    @Override
    public Integer part0(Stream<String> input) {
        HistoriansHaveBadHandWriting h = new HistoriansHaveBadHandWriting(new ArrayList<>(), new ArrayList<>());
        input.forEach(h::addLine);
        h.a.sort(Integer::compareTo);
        h.b.sort(Integer::compareTo);
        int sum = 0;
        for(int i = 0; i < h.size(); i++) {
            sum += Math.abs(h.a.get(i) - h.b.get(i));
        }
        return sum;
    }

    @Override
    public Integer part1(Stream<String> input) {
        HistoriansHaveBadHandWriting h = new HistoriansHaveBadHandWriting(new ArrayList<>(), new ArrayList<>());
        input.forEach(h::addLine);
        Map<Integer, Integer> map = h.a.stream().collect(Collectors.toMap(i -> i, i ->  h.countEntries(i), (i,j)->i + h.countEntries(j) ));
        return map.keySet().stream().map(i -> map.get(i)*i).mapToInt(Integer::intValue).sum();

    }
}
