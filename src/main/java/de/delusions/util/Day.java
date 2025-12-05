package de.delusions.util;

import de.delusions.tools.ConfigProperties;
import de.delusions.tools.DynamicCompiler;
import de.delusions.tools.InputDownloader;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class Day<T> {

    final T[] expected;

    final int day;

    final String tag;

    boolean testMode = false;

    @SafeVarargs
    public Day(String tag, T... expected) {
        this.day = Integer.parseInt(getClass().getSimpleName().substring(3));
        this.tag = tag;
        this.expected = expected;
    }

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
            Day.log.info(log);
        } else {
            Day.log.error(log);
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
            log.error("Input could not be retrieved: {}", e.getMessage());
            return null;
        }
    }

    public static void generateDay(String templateName,int day,boolean test)  {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String filename = String.format("src/main/%s/de/delusions/aoc/advent%04d/Day%02d%s.java", test?"test":"java", year, day,test?"Test":"");
        Paths.get(filename).getParent().toFile().mkdirs();
        if(Paths.get(filename).toFile().exists()) {
            //do not recreate file
           return;
        }
        try (InputStream input = InputDownloader.class.getClassLoader().getResourceAsStream(String.format("templates/%s.template",templateName))) {
            String template = new String(input.readAllBytes());
            String content = template.replace("{$day}", String.format("%02d",day)).replace("{$year}", String.valueOf(year));
            Paths.get(filename).toFile().createNewFile();
            Files.writeString(Paths.get(filename), content);

        } catch (IOException e) {
            log.error("Could not generate Day template: {}", e.getMessage());
        }
    }

    public static Day<?> loadDayClass(int dayNumber, Object... args) {
        try {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String className = String.format("de.delusions.aoc.advent%04d.Day%02d" ,year, dayNumber);
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
