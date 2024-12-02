package de.delusions.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class ConfigProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigProperties.class);
    static final String SESSION_COOKIE = "session.cookie";
    static final String INPUT_STORAGE = "input.storage.path";
    static final String EXAMPLE_FORMAT = "file.format.example";
    static final String INPUT_FORMAT = "file.format.input";
    static final String EXAMPLE_DEFAULT_FORMAT = "example_%s.json";
    static final String INPUT_DEFAULT_FORMAT = "input_%s.txt";
    static final String YEAR = "year";

    public record Config(String sessionCookie, String inputStoragePath, String exampleFormat, String inputFormat, int year) {
    }

    public static Config config;

    public static Config loadProperties() throws IOException {
        if (config == null) {

            try (InputStream input = InputDownloader.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    LOG.error("Sorry, unable to find config.properties");
                    return new Config(null, "src/main/resources/inputs", EXAMPLE_DEFAULT_FORMAT, INPUT_DEFAULT_FORMAT, 0);
                }
                Properties properties = new Properties();
                properties.load(input);
                config = new Config(
                        properties.getProperty(SESSION_COOKIE, null),
                        properties.getProperty(INPUT_STORAGE, "src/main/resources/inputs"),
                        properties.getProperty(EXAMPLE_FORMAT, EXAMPLE_DEFAULT_FORMAT),
                        properties.getProperty(INPUT_FORMAT, INPUT_DEFAULT_FORMAT),
                        Integer.parseInt(properties.getProperty(YEAR, "2024")));
            }
        }
        return config;
    }

    static Path getExamplePath(int day) {
        return Paths.get(config.inputStoragePath(), String.format(config.exampleFormat(), day));
    }

    static Path getInputPath(int day) {
        return Paths.get(config.inputStoragePath(), String.format(config.inputFormat(), day ));
    }

    static int getYear() {
        return config.year();
    }

    public static Stream<String> getInputStream(int day)
            throws IOException {
        Path path = getInputPath(day);
        if (!Files.exists(path)) {
            InputDownloader downloader = new InputDownloader(config.year, day);
            try {
                downloader.downloadInput();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Files.lines(path);
    }

    public static  Stream<String> getExampleStream(int day, int part) throws IOException {
        Path path = getExamplePath(day);
        if (!Files.exists(path)) {
            InputDownloader downloader = new InputDownloader(config.year, day );
            try {
                downloader.downloadExamples();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        String content = Files.readString(path);
        InputDownloader.DayExamples dayExamples = new ObjectMapper().readValue(content, InputDownloader.DayExamples.class);
        if(part==1 && dayExamples.tests().size()==1)
            return dayExamples.tests().get(0).input().lines();
        return dayExamples.tests().get(part).input().lines();

    }
}
