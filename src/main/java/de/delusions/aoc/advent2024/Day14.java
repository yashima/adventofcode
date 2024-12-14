package de.delusions.aoc.advent2024;

import de.delusions.tools.ConfigProperties;
import de.delusions.util.Coordinates;
import de.delusions.util.Day;
import de.delusions.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day14 extends Day<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Day14.class);

    public Day14() {
        super("Restroom Redoubt", 12, 0, 218619120, 7055);
    }

    public static Pattern REGEX = Pattern.compile("p=(\\d+).(\\d+).*v=(-?\\d+),(-?\\d+)");

    record Robot(Coordinates start, int xVel, int yVel) {
        static Robot parse(String line) {
            var matcher = REGEX.matcher(line);
            if (matcher.matches()) {
                return new Robot(new Coordinates(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))),
                        Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
            }
            return null;
        }

        Coordinates move(int afterSeconds, Matrix theMap) {
            return new Coordinates(
                    wrap(start.x, xVel, afterSeconds, theMap.getXLength()),
                    wrap(start.y, yVel, afterSeconds, theMap.getYLength()));

        }

    }

    Matrix getARoom() {
        int xDim = isTestMode() ? 11 : 101;
        int yDim = isTestMode() ? 7 : 103;
        Matrix aRoom = new Matrix(xDim, yDim, 0, 0);
        aRoom.setAllValues(0);
        aRoom.setPrintMap(Map.of(0, ".", 1, "1", 2, "2", 3, "3", 4, "4", 5, "5"));
        return aRoom;
    }

    static int wrap(int start, int vel, int seconds, int dim) {
        int pos = (start + seconds * vel) % dim;
        return pos < 0 ? dim + pos : pos;
    }

    @Override
    public Integer part0(Stream<String> input) {
        List<Robot> robots = input.map(Robot::parse).filter(Objects::nonNull).collect(Collectors.toList());
        Matrix aRoom = getARoom();
        robots.forEach(r -> {
            Coordinates current = r.move(100, aRoom);
            int value = aRoom.getValue(current);
            aRoom.setValue(current, value + 1);
        });

        int w = aRoom.getXLength() / 2;
        int h = aRoom.getYLength() / 2;
        return List.of(
                        aRoom.getSubMatrix(0, 0, w, h),
                        aRoom.getSubMatrix(0, h + 1, w, h),
                        aRoom.getSubMatrix(w + 1, 0, w, h),
                        aRoom.getSubMatrix(w + 1, h + 1, w, h))
                .stream()
                .mapToInt(q -> q.coordinatesStream().mapToInt(c -> q.getValue(c)).sum())
                .reduce(1, (a, b) -> a * b);
    }

    @Override
    public Integer part1(Stream<String> input) {
        if (isTestMode()) {
            return 0;
        }
        List<Robot> robots_txt = input.map(Robot::parse).filter(Objects::nonNull).collect(Collectors.toList());
        AtomicInteger frame = new AtomicInteger(0);

        while (frame.get() < 10000) {
            Matrix aRoom = getARoom();
            frame.incrementAndGet();//one really needs to be careful where the increment is placed.
            robots_txt.forEach(r -> {
                Coordinates current = r.move(frame.get(), aRoom);
                int value = aRoom.getValue(current);
                aRoom.setValue(current, value + 1);
            });
            saveImage(aRoom.transposeRight(), frame.get());
        }
        LOG.debug("Go look at the images in the images folder and pick the frame with the xmas tree");
        return 7055;
    }


    void saveImage(Matrix aRoom,int frame)  {
        BufferedImage image = toBufferedImage(aRoom);
        Path filePath = Paths.get(String.format("images/frame%05d.png",frame));
        try {
            if(!filePath.toFile().exists()) {
                ImageIO.write(image, "png", new File(filePath.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    BufferedImage toBufferedImage(Matrix aRoom) {
        BufferedImage image = new BufferedImage(aRoom.getYLength(), aRoom.getXLength(), BufferedImage.TYPE_INT_RGB);
        aRoom.coordinatesStream().forEach(c -> {
            int value = aRoom.getValue(c);
            image.setRGB(c.y, c.x, value == 0 ? Color.white.getRGB() : Color.black.getRGB());
        });
        return image;
    }

    double standardDeviation(int[] numbers){
        BigDecimal avg = BigDecimal.valueOf(Arrays.stream(numbers).sum()).divide(BigDecimal.valueOf(numbers.length),1,RoundingMode.DOWN);
        return Math.sqrt(Arrays.stream(numbers)
                .mapToObj(n -> BigDecimal.valueOf(n).subtract(avg).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(numbers.length),1, RoundingMode.DOWN).doubleValue());
    }

}
