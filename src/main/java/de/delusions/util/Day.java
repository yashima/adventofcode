package de.delusions.util;

import de.delusions.tools.ConfigProperties;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public abstract class Day<T> {

    T[] expected;

    int day;

    String tag;

    boolean testMode = false;

    @SafeVarargs
    public Day(int day, String tag, T... expected) {
        this.day = day;
        this.tag = tag;
        this.expected = expected;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public abstract T part0(Stream<String> input);

    public abstract T part1(Stream<String> input);

    public void run(boolean test, int part) {
        this.testMode = test; //keep this
        long timer = System.currentTimeMillis();
        T result = part == 0 ? part0(getInput(test, part)) : part1(getInput(test, part));
        timer = System.currentTimeMillis() - timer;
        boolean verify = verify(result, part, test);
        String log = String.format("Day %01d '%s' Part %d: result=%s success=%s time=%dms", day, tag, part, result, verify, timer);
        if (verify) {
            System.out.println(log);
        } else {
            System.err.println(log);
        }
    }

    public boolean verify(T result, int part, boolean test) {
        int index = part + (test ? 0 : 2);
        if (result == null || expected.length < index - 1) {
            return false;
        }
        return result.equals(expected[index]);
    }

    public Stream<String> getInput(boolean test, int part) {
        try {
            ConfigProperties.loadProperties();
            if (test) {
                return ConfigProperties.getExampleStream(day, part);
            } else {
                return ConfigProperties.getInputStream(day);
            }

        } catch (IOException e) {
            System.err.println("Input could not be retrieved: " + e.getMessage());
            return null;
        }
    }

    public static Day<?> loadDayClass(int dayNumber, Object... args) {
        try {
            String className = "de.delusions.aoc.days.Day" + dayNumber;
            Class<?> dayClass = Class.forName(className);
            Constructor<?>[] constructors = dayClass.getConstructors();
            for(Constructor<?> constructor : constructors) {
                if(constructor.getParameterCount() == args.length) {
                    return (Day<?>) constructor.newInstance(args);
                }
            }
            throw new IllegalArgumentException("No constructor found for day: " + dayNumber);
        } catch (ClassNotFoundException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw new IllegalArgumentException("Class not found for day: " + dayNumber, e);
        }
    }

}
