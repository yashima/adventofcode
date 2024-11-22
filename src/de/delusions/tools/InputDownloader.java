package de.delusions.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(InputDownloader.class);

    private static String sessionCookie;
    private static String inputStoragePath;
    private static final HttpClient client = HttpClient.newBuilder().build();

    public static void loadProperties() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            sessionCookie = properties.getProperty("session.cookie");
            inputStoragePath = properties.getProperty("input.storage.path");
        }
    }

    public static void downloadInput(int year, int day) throws IOException, InterruptedException {
        LocalDate currentDate = LocalDate.now();
        LocalDate targetDate = LocalDate.of(year, 12, day);

        if (!targetDate.isBefore(currentDate)) {
            LOG.info("The specified date is not in the past. Skipping download for day " + day);
            return;
        }

        Path filePath = Paths.get(inputStoragePath, "input_day_" + day + ".txt");
        if (Files.exists(filePath)) {
            LOG.info("File for day " + day + " already exists. Skipping download.");
            return;
        }

        String url = "https://adventofcode.com/" + year + "/day/" + day + "/input";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("cookie", "session=" + sessionCookie)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            saveToFile(response.body(), day);
            LOG.info("Day " + day + " input downloaded successfully.");
        } else {
            LOG.error("Failed to download input for day " + day + ". Status code: " + response.statusCode());
        }
    }

    private static void saveToFile(String content, int day) throws IOException {
        Path path = Paths.get(inputStoragePath, "input_day_" + day + ".txt");
        Files.createDirectories(path.getParent()); // Create directories if they do not exist
        Files.writeString(path, content);
    }

    public static void main(String[] args) {
        try {
            // Load properties from config file
            loadProperties();

            // Download input for each day
            int year = 2023;
            for (int day = 1; day <= 24; day++) {
                downloadInput(year, day);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
