package de.delusions.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Matrix {
    //y = cols , -1 = more west, +1 more east
    //x = rows , -1 = more north, +1 more south
    final int[][] matrix; //int rows | cols

    final int xOffset;

    final int yOffset;
    Map<Integer, String> printMap = new HashMap<>();

    public Matrix(int[][] initialized) {
        this(initialized, 0, 0);
    }
    //Map.of( 0, ".", 1, "#", 2, "o", 3, "S", 4, "B" );

    public Matrix(int[][] initialized, int xOffset, int yOffset) {
        this.matrix = initialized;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Matrix(int xDim, int yDim, int xOffset, int yOffset) {
        this(new int[xDim][yDim], xOffset, yOffset);
    }

    public static Matrix createFromString(String input, String divider) {
        return new Matrix(Arrays.stream(input.split(divider)).map(line -> line.chars().toArray()).toArray(int[][]::new));
    }

    public static Matrix createFromStream(Stream<String> input) {
        return new Matrix(input.map(line -> line.chars().toArray()).toArray(int[][]::new));
    }

    public static String toCharString(int[] array) {
        if (array == null) {
            return null;
        }
        return Arrays.stream(array).mapToObj(c -> (char) c + "").collect(Collectors.joining());
    }

    public String rowToString(int idx) {
        if (idx < 0 || idx >= matrix.length) {
            return null;
        }
        return toCharString(getRow(idx)).trim();
    }

    public int size() {
        return getXLength() * getYLength();
    }

    public void setPrintMap(Map<Integer, String> printMap) {
        this.printMap = printMap;
    }

    public int cleanup() {
        AtomicInteger max = new AtomicInteger(0);
        if (Arrays.stream(matrix).map(row -> row.length).peek(l -> max.set(Math.max(max.get(), l))).distinct().count() > 1) {
            for (int x = 0; x < getXLength(); x++) {
                int[] row = getRow(x);
                if (row.length < max.get()) {
                    matrix[x] = new int[max.get()];
                    System.arraycopy(row, 0, matrix[x], 0, row.length);
                }
            }
        }
        return max.get();
    }

    public IndexedRow getIndexedRow(int index) {
        return new IndexedRow(index, getRow(index));
    }

    public int rowToBinary(int idx, Map<Character, Integer> mapping) {
        String rowToString = rowToString(idx);
        if (rowToString == null) {
            return -1;
        }
        return Integer.parseUnsignedInt(rowToString.chars().mapToObj(c -> mapping.get((char) c) + "").collect(Collectors.joining()), 2);
    }

    public IntStream rowAsStream(int x) {
        return Arrays.stream(getRow(x));
    }

    public Stream<Coordinates> coordinatesStream() {
        return IntStream.range(0, getXLength()).boxed().flatMap(x -> IntStream.range(0, getYLength()).boxed().map(y -> createCoords(x, y)));
    }

    public void setValue(int x, int y, int value) {
        this.matrix[x - xOffset][y - yOffset] = value;
    }

    public void setValue(Coordinates coordinates) {
        setValue(coordinates, coordinates.getValue());
    }

    public void incrementValue(Coordinates coordinates) {
        setValue(coordinates, getValue(coordinates) + coordinates.getValue());
    }

    public void setValue(Coordinates coordinates, int value) {
        int x = coordinates.x - xOffset;
        int y = coordinates.y - yOffset;
        matrix[x][y] = value;
    }

    public int getValue(Coordinates coordinates) {
        int y = coordinates.y - yOffset;
        int x = coordinates.x - xOffset;
        return matrix[x][y];
    }

    public int getValue(Coordinates coordinates, int defaultValue) {
        if (!isInTheMatrix(coordinates)) return defaultValue;
        int y = coordinates.y - yOffset;
        int x = coordinates.x - xOffset;
        return matrix[x][y];
    }

    /* sometimes we have an endless matrix and just want coordinates values */
    public int getRelativeValue(Coordinates c) {
        return matrix[convert(c.x, getXLength())][convert(c.y, getYLength())];
    }

    private int convert(int val, int len) {
        return val > 0 ? val % len : ((val % len) + len) % len;
    }

    public void setAllValuesRow(int y, int value) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][y] = value;
        }
    }

    public void setAllValues(int value) {
        for (int[] ints : matrix) {
            Arrays.fill(ints, value);
        }
    }

    public boolean isEmpty(Coordinates coordinates) {
        return getValue(coordinates) == 0;
    }

    public boolean isInTheMatrix(Coordinates... coordinates) {
        try {
            for (Coordinates c : coordinates) {
                getValue(c);
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public Coordinates createCoords(int x, int y) {
        return new Coordinates(x + xOffset, y + yOffset);
    }

    public int getXLength() {
        return matrix.length;
    }

    public int getYLength() {
        return matrix[0].length;
    }

    public List<Coordinates> findValues(List<Integer> values, boolean firstOnly) {
        List<Coordinates> positions = new ArrayList<>();
        for (int x = 0; x < getXLength(); x++) {
            for (int y = 0; y < getYLength(); y++) {
                Coordinates coordinates = createCoords(x, y);
                if (values.contains(getValue(coordinates))) {
                    positions.add(coordinates);
                    if (firstOnly) {
                        break;
                    }
                }
            }
        }
        return positions;
    }

    /**
     * A list of coordinates that do not match the given value.
     * These coordinates include the value found instead.
     *
     * @param notValue the value we don't want
     * @return a list of coordinates with values that do not match the notValue
     */
    public List<Coordinates> findNotValues(char notValue) {
        List<Coordinates> positions = new ArrayList<>();
        for (int x = 0; x < getXLength(); x++) {
            for (int y = 0; y < getYLength(); y++) {
                Coordinates coordinates = createCoords(x, y);
                int currentValue = getValue(coordinates);
                if (notValue != currentValue) {
                    coordinates.setValue(currentValue);
                    positions.add(coordinates);
                }
            }
        }
        return positions;
    }

    public List<Coordinates> findValues(int value, boolean firstOnly) {
        return findValues(List.of(value), firstOnly);
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public Stream<IndexedRow> indexedRows() {
        AtomicInteger index = new AtomicInteger(0);
        return Arrays.stream(matrix).map(r -> new IndexedRow(index.getAndIncrement(), r));
    }

    public void setRow(int x, int[] row) {
        if (getYLength() >= 0) {
            System.arraycopy(row, 0, matrix[x], 0, getYLength());
        }
    }

    public int[] getRow(int x) {
        if (x < 0 || x > matrix.length - 1) {
            return null;
        }
        return matrix[x];
    }

    public void setRowReverse(int x, int[] row) {
        for (int y = 0; y < getYLength(); y++) {
            matrix[x][getYLength() - y - 1] = row[y];
        }
    }

    public void setColumn(int y, int[] column, boolean fromTop) {
        for (int x = 0; x < getXLength(); x++) {
            matrix[fromTop ? x : getXLength() - x - 1][y] = column[x];
        }
    }

    public String colToString(int idx) {
        return toCharString(getColumn(idx, true));
    }

    public int[] getColumn(int y, boolean fromTop) {
        int[] column = new int[matrix.length];
        for (int x = 0; x < getXLength(); x++) {
            column[fromTop ? x : getXLength() - x - 1] = matrix[x][y];
        }
        return column;
    }

    public IntStream colAsStream(int y) {
        return Arrays.stream(getColumn(y, true));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < getXLength(); x++) {
            for (int y = 0; y < getYLength(); y++) {
                if (printMap.isEmpty()) {
                    builder.append(Character.valueOf((char) getValue(createCoords(x, y))));
                } else {
                    builder.append(printMap.get(getValue(createCoords(x, y))));
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public Matrix transposeLeft() {
        return new Matrix(this.columnsLeft().toList().toArray(new int[0][0]));
    }

    public Matrix transpose() {
        return new Matrix(this.columns().toList().toArray(new int[0][0]));
    }

    Stream<int[]> columnsLeft() {
        List<int[]> columns = new ArrayList<>();
        for (int y = getYLength() - 1; y >= 0; y--) {
            columns.add(getColumn(y, true));
        }
        return columns.stream();
    }

    public Matrix transposeRight() {
        return new Matrix(this.columnsRight().toList().toArray(new int[0][0]));
    }

    public Stream<int[]> rows() {
        return Arrays.stream(matrix);
    }

    Stream<int[]> columnsRight() {
        List<int[]> columns = new ArrayList<>();
        for (int y = 0; y < getYLength(); y++) {

            int[] column = getColumn(y, false);

            columns.add(column);
        }
        return columns.stream();
    }

    public Stream<int[]> columns() {
        List<int[]> columns = new ArrayList<>();
        for (int y = 0; y < getYLength(); y++) {
            columns.add(getColumn(y, true));
        }
        return columns.stream();
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(xOffset, yOffset);
        result = 31 * result + Arrays.deepHashCode(getMatrix());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Matrix matrix1)) {
            return false;
        }
        return xOffset == matrix1.xOffset && yOffset == matrix1.yOffset && Arrays.deepEquals(getMatrix(), matrix1.getMatrix());
    }

    public int[] getRowReverse(int index) {
        return Arrays.stream(getRow(index)).boxed().toList().reversed().stream().mapToInt(i -> i).toArray();
    }

    public void setValueSafely(Coordinates theElf, char o, List<Character> filter) {
        if (isInTheMatrix(theElf)) {
            if (!filter.contains((char) getValue(theElf))) {
                setValue(theElf, o);
            }
        }
        //only set value if in matrix, else ignore
    }

    public Matrix getSubMatrix(int x, int y, int width, int height) {
        int[][] subMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                subMatrix[i][j] = getValue(createCoords(x + i, y + j));
            }
        }
        return new Matrix(subMatrix);
    }

    public record IndexedRow(int index, int[] row) {
    }
}
