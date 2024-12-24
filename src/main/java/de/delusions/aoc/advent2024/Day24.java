package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day24 extends Day<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(Day24.class);

    public Day24() {
        super("", 2024L, 0L, 0L, 0L);
    }

    static Pattern REGEX = Pattern.compile("((...): (0|1))|((...) (AND|XOR|OR) (...) -> (...))");

    enum OP {
        AND, OR, XOR
    }

    record Expression(String operand1, OP operator, String operand2, String result) {
        boolean eval(Map<String, Boolean> inputs, Map<String, Expression> expressions) {
            boolean operand1Value = inputs.containsKey(operand1) ? inputs.get(operand1) : expressions.get(operand1).eval(inputs, expressions);
            boolean operand2Value = inputs.containsKey(operand2) ? inputs.get(operand2) : expressions.get(operand2).eval(inputs, expressions);
            switch (operator) {
                case AND:
                    return operand1Value && operand2Value;
                case OR:
                    return operand1Value || operand2Value;
                case XOR:
                    return operand1Value ^ operand2Value;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        }

    }


    @Override
    public Long part0(Stream<String> input) {
        Map<String, Boolean> inputs = new HashMap<>();
        Map<String, Expression> expressions = input.map(REGEX::matcher)
                .filter(m -> m.matches())
                .map(m -> {
                    if (m.group(1) != null) {
                        inputs.put(m.group(2), m.group(3).equals("1"));
                        return null;
                    }
                    return new Expression(m.group(5), OP.valueOf(m.group(6)), m.group(7), m.group(8));
                })
                .filter(Objects::nonNull)
                .collect(HashMap::new, (m, e) -> m.put(e.result, e), HashMap::putAll);

        String binaryString = expressions.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted(Map.Entry.comparingByKey())
                .peek(e -> LOG.info("{}", e))
                .map(e -> ""+(e.getValue().eval(inputs, expressions) ? 1 : 0)).collect(Collectors.joining());
        return Long.parseLong(new StringBuilder(binaryString).reverse().toString(), 2);
    }

    //0011111101000
    //0001011111100
    @Override
    public Long part1(Stream<String> input) {
        return 0L;
    }
}
