package de.delusions.util;

import java.util.Arrays;
import java.util.List;

public enum Direction {

    north("^"), northeast("┐"), east(">"), southeast("┘"), south("v"), southwest("└"), west("<"), northwest("┌");

    private final String symbol;

    Direction(String symbol) {
        this.symbol = symbol;
    }

    public static List<Direction> getBasic() {
        return List.of(north, east, south, west);
    }

    public static Direction[] cardinals() {
        return new Direction[] {north, east, south, west};
    }

    public String getSymbol() {
        return symbol;
    }
    public char getCharacter() { return symbol.charAt(0); }

    public static List<Character> getCharacters() {
        return Arrays.stream(values()).map(Direction::getCharacter).toList();
    }

    public Direction turnRight() {
        return turnRight(90);
    }

    public Direction turnRight(int degree) {
        return values()[(ordinal() + (degree / 45)) % values().length];
    }

    public Direction turnLeft() {
        return turnLeft(90);
    }

    public Direction turnLeft(int degree) {
        return values()[(ordinal() + ((360 - degree) / 45)) % values().length];
    }

    public Direction opposite() {
        return turnRight(180);
    }

    public boolean isOpposite(Direction d) {
        return this.opposite() == d;
    }

    public boolean isOrthogonal(Direction d) {
        return this.turnLeft() == d || this.turnRight() == d;
    }
}
