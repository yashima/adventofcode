package de.delusions.tools;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.delusions.tools.ConfigProperties.*;

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

    private static final HttpClient client = HttpClient.newBuilder().build();
    public record Example(String input, List<String> solutions) {}

    public record DayExamples(String tagline, int day, String url, List<Example> tests) {}

    private final int day;
    private final int year;
    private final int part;

    InputDownloader(int year, int day, int part) {
        this.year = year;
        this.day = day;
        this.part = part;
    }

    private String makeHttpRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("cookie", "session=" + config.sessionCookie())
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
        Path filePath = getInputPath(day, part);
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

    private static final String TITLE_REGEX = "^---\\s*Day\\s+(\\d+):\\s*(.*?)\\s*---$";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX);
    public void downloadExamples() throws IOException, InterruptedException {
        Path filePath = getExamplePath(day);
        if (Files.exists(filePath)) {
            LOG.info("File {} already exists. Skipping download.", filePath.getFileName());
            return;
        }
        String url = String.format("https://adventofcode.com/%s/day/%s", year, day);
        String body = makeHttpRequest(url);
        if (body != null) {

            Document document = Jsoup.parse(body);
            JSONArray testCases = new JSONArray();
            String tagline = document.selectFirst("h2").text();
            Matcher matcher = TITLE_PATTERN.matcher(tagline);
            JSONObject day = new JSONObject();
            day.put("url",url);
            if (matcher.matches()) {
                day.put("day", Integer.parseInt(matcher.group(1)));
                day.put("tagline", matcher.group(2));
                day.put("tests",testCases);

            }
            Elements codeBlocks = document.select("pre code, code em");

            JSONObject testCase=null;
            for (Element codeBlock : codeBlocks) {

                if(testCase!=null && codeBlock.tagName().equals("em")){
                    String value = codeBlock.wholeText();
                    testCase.getJSONArray("solutions").put(value);
                } else if(codeBlock.tagName().equals("code")) {
                    //do previous one
                    if(testCase!=null) {
                        testCases.put(testCase);
                    }
                    testCase = new JSONObject();
                    testCase.put("solutions", new JSONArray());
                    testCase.put("input", codeBlock.wholeText());
                }
            }
            testCases.put(testCase);
            saveToFile(filePath, day.toString(2));
        }
    }

    private void saveToFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    public static void main(String[] args) {
        try {
            ConfigProperties.loadProperties();
            LocalDate currentDate = LocalDate.now().plusDays(1);
            int year = ConfigProperties.getYear();
            for (int day = 1; day <= 25; day++) {
                LocalDate targetDate = LocalDate.of(year, 12, day);
                if (!targetDate.isBefore(currentDate)) {
                    LOG.info("The specified date is not in the past. Skipping download for day {}", day);
                    break;
                }
                InputDownloader downloader = new InputDownloader(year, day, 1);
                downloader.downloadInput();
                downloader.downloadExamples();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Exception: {} ", e.getMessage());
        }
    }


}
