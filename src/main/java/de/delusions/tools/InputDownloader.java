package de.delusions.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * The InputDownloader class is responsible for downloading inputs and examples
 * for the Advent of Code challenges for a specific year and day.
 * <p>
 * It uses HttpClient to make HTTP requests and retrieve data from the adventofcode.com website.
 * It downloads data to a specified storage path, and uses a session cookie for authenticated requests.
 * The downloaded data is then saved to files on the local filesystem.
 */
public class InputDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(InputDownloader.class);
    ;

    private static String sessionCookie;
    private static String inputStoragePath;
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Properties properties = new Properties();

    private final int day;
    private final int year;

    InputDownloader(int year, int day) {
        this.year = year;
        this.day = day;
    }

    private String makeHttpRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("cookie", "session=" + sessionCookie)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            LOG.info("Day {} downloaded successfully.", day);
            return response.body();
        } else {
            LOG.error("Failed to download input for day {}. Status code: {}", day, response.statusCode());
            return null;
        }
    }

    public void downloadInput() throws IOException, InterruptedException {
        Path filePath = getPath("input");
        if (Files.exists(filePath)) {
            LOG.info("File {} already exists. Skipping download.", filePath.getFileName());
            return;
        }
        String url = String.format("https://adventofcode.com/%s/day/%s/input", year, day);
        String body = makeHttpRequest(url);
        if (body != null) {
            saveToFile(filePath, body);
        }
    }

    private static final String TITLE_REGEX = "^---\\s*Day\\s+\\d+:\\s*(.*?)\\s*---$";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX);
    private void downloadExamples() throws IOException, InterruptedException {
        Path filePath = getPath("examples");
        if (Files.exists(filePath)) {
            LOG.info("File {} already exists. Skipping download.", filePath.getFileName());
            return;
        }
        String url = String.format("https://adventofcode.com/%s/day/%s", year, day);
        String body = makeHttpRequest(url);
        if (body != null) {

            Document document = Jsoup.parse(body);
            String tagline = document.selectFirst("h2").text();
            Matcher matcher = TITLE_PATTERN.matcher(tagline);
            if (matcher.matches()) {
                tagline = matcher.group(1);
            }
            Elements codeBlocks = document.select("pre code");
            StringBuilder sb = new StringBuilder(tagline);
            for (Element codeBlock : codeBlocks) {
                sb.append("\n-----------------------\n");
                sb.append(codeBlock.text());
            }
            saveToFile(filePath, sb.toString());
        }
    }

    private void saveToFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent()); // Create directories if they do not exist
        Files.writeString(path, content);
    }

    private Path getPath(String type) {
        return Paths.get(inputStoragePath, String.format("%s_day_%s.txt", type, day));
    }

    public static void main(String[] args) {
        try {
            loadProperties();
            LocalDate currentDate = LocalDate.now();

            int year = 2023;
            for (int day = 1; day <= 24; day++) {
                LocalDate targetDate = LocalDate.of(year, 12, day);
                if (!targetDate.isBefore(currentDate)) {
                    LOG.info("The specified date is not in the past. Skipping download for day {}", day);
                    break;
                }
                InputDownloader downloader = new InputDownloader(year, day);
                downloader.downloadInput();
                downloader.downloadExamples();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Exception: {} ", e.getMessage());
        }
    }

    public static void loadProperties() throws IOException {
        try (InputStream input = InputDownloader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                LOG.error("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
            sessionCookie = properties.getProperty("session.cookie");
            inputStoragePath = properties.getProperty("input.storage.path");
        }
    }
}
