package de.delusions.aoc.advent2024;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.delusions.tools.ConfigProperties;
import de.delusions.util.Day;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.delusions.tools.InputDownloader.DayExamples;
import static de.delusions.tools.InputDownloader.Example;

public abstract class DayTest {

    DayExamples dayExamples;

    @BeforeClass
    void setUp() throws IOException {
        ConfigProperties.Config config = ConfigProperties.loadProperties();
        String content = Files.readString(Paths.get(config.inputStoragePath(), String.format("examples_day_%d.json", getDayNumber())));
        dayExamples = new ObjectMapper().readValue(content, DayExamples.class);
    }

    void assertPart(int part) {
        Example testCase = getTestCase(part);
        Day<?> day = getDay();
        Object result = day.part0(testCase.input().lines());
        assert result.equals(testCase.solutions().getFirst());
    }

    int getDayNumber() {
        if (dayExamples == null) {
            Matcher matcher = Pattern.compile("(\\d++)").matcher(getClass().getCanonicalName());
            matcher.find();
            return Integer.parseInt(matcher.group(1));
        }
        return dayExamples.day();
    }

    Example getTestCase(int part) {
        boolean foundfirst = false;
        for(Example testCase : dayExamples.tests()) {
            if(testCase.solutions().size()==1){
                if(part==1) {
                    return testCase;
                } else if(part==2 && foundfirst) {
                    return testCase;
                }
                foundfirst = true;
            }
        }
        return null;
    }

    Day<?> getDay() {
        return Day.loadDayClass(getDayNumber());
    }


    @Test
    public abstract void testPart1();

    @Test
    public abstract void testPart2();
}
