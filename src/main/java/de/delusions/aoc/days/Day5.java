package de.delusions.aoc.days;

import de.delusions.util.Day;

import java.util.*;
import java.util.stream.Stream;

public class Day5 extends Day<Integer> {


    public Day5(){
        super( "Print Queue",143,123,5964,0);
    }

    record Order(int first, int last){
        static Order fromLine(String line){
            String[] parts = line.trim().split("\\|");
            if(parts.length == 2){
                return new Order(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
            return null;
        }
    }
    record Pages(List<Integer> numbers){
        static Pages fromLine(String line){
            if(line.isBlank() || !line.contains(",")) return null;
            return new Pages(new ArrayList<>(Arrays.stream(line.split(",")).map(n -> Integer.parseInt(n.strip())).toList()));
        }
        int middle(){
            return numbers.get(numbers.size()/2);
        }

        boolean isOrdered(Map<Integer, List<Integer>> order, int first){
            if(!order.containsKey(first)) return true;
            return order.get(first).stream().allMatch(last -> !numbers.contains(last) || numbers.indexOf(first) < numbers.indexOf(last));
        }

        boolean isOrdered(Map<Integer, List<Integer>> order){
            return numbers.stream().allMatch(i -> isOrdered(order,i));
        }
    }

    record PrintQueue(List<Pages> pages, Map<Integer, List<Integer>> order){
        Pages order(Pages pages){
            pages.numbers.sort((a, b) -> {
                List<Integer> followers = order.get(a);
                if(followers!=null && followers.contains(b)){
                    return 1;
                }
                followers = order.get(b);
                if(followers!=null && followers.contains(a)){
                    return -1;
                }
                return 0;
            });
            return pages;
        }
    }

    private PrintQueue parsePrintQueue(Stream<String> input) {
        PrintQueue queue = new PrintQueue(new ArrayList<>(), new HashMap<>());
        input.forEach(line -> {
            Order orderLine = Order.fromLine(line);
            if(orderLine != null){
                queue.order.putIfAbsent(orderLine.first,new ArrayList<>());
                queue.order.get(orderLine.first).add(orderLine.last);
            }
            Pages pages = Pages.fromLine(line);
            if(pages != null){
                queue.pages.add(pages);
            }
        });
        return queue;
    }

    @Override
    public Integer part0(Stream<String> input) {
        PrintQueue queue = parsePrintQueue(input);
        return queue.pages.stream().filter(p -> p.isOrdered(queue.order)).mapToInt(p -> queue.order(p).middle()).sum();

    }

    @Override
    public Integer part1(Stream<String> input) {
        PrintQueue queue = parsePrintQueue(input);
        return queue.pages.stream().filter(p -> !p.isOrdered(queue.order)).mapToInt(p -> queue.order(p).middle()).sum();
    }
}
