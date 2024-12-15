package de.delusions.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coordinates {
    public static boolean USE_FACING = false;
    public final int x;

    public final int y;

    final Coordinates previous;

    int value = 0;

    Direction facing;

    public Coordinates(String coordString, String divider, int value) {
        this(coordString.split(divider), value);
    }

    public Coordinates(String[] coords, int value) {
        this(Integer.parseInt(coords[0].trim()), Integer.parseInt(coords[1].trim()), value);
    }

    public Coordinates(int x, int y) {
        this(x, y, 0, null);
    }

    public Coordinates(int x, int y, Direction facing) {
        this(x, y, 0, null);
        this.facing = facing;
    }


    public Coordinates(int x, int y, int value) {
        this(x, y, value, null);
    }

    public Coordinates(int x, int y, int value, Coordinates previous) {
        this.previous = previous;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Coordinates moveTo(Direction move, int value) {
        return moveTo(move, 1, value);
    }

    public Coordinates moveTo(Direction move) {
        return moveTo(move, 1, 1);
    }

    public Coordinates moveToNext() {
        Coordinates coordinates = moveTo(facing, 1, 1);
        coordinates.setFacing(facing);
        return coordinates;
    }

    public Coordinates moveDay22(Direction move) {
        int row = x;
        int col = y;
        return switch (move) {
            case west -> new Coordinates(row, col - 1, 3, this);
            case east -> new Coordinates(row, col + 1, 3, this);
            case south -> new Coordinates(row + 1, col, 3, this);
            case north -> new Coordinates(row - 1, col, 3, this);
            default -> throw new IllegalStateException("Unexpected value: " + move);
        };
    }

    public Coordinates moveTo(Direction move, int distance, int value) {
        Coordinates result = switch (move) {
            case north -> new Coordinates(this.x - distance, this.y, value, this);
            case south -> new Coordinates(this.x + distance, this.y, value, this);
            case east -> new Coordinates(this.x, this.y + distance, value, this);
            case west -> new Coordinates(this.x, this.y - distance, value, this);
            case southwest -> new Coordinates(this.x - distance, this.y + distance, value, this);
            case southeast -> new Coordinates(this.x + distance, this.y + distance, value, this);
            case northwest -> new Coordinates(this.x - distance, this.y - distance, value, this);
            case northeast -> new Coordinates(this.x + distance, this.y - distance, value, this);
        };
        result.facing = move;
        return result;
    }

    public List<Coordinates> getAdjacent() {
        List<Coordinates> result = new ArrayList<>();
        for (Direction d : Direction.values()) {
            result.add(this.moveTo(d, 1));
        }
        return result;
    }

    public Direction getFacing() {
        return facing;
    }

    public Coordinates setFacing(Direction facing) {
        this.facing = facing;
        return this;
    }

    public Direction lookingTowards(Coordinates that) {
        Direction result = null;
        if (x == that.x && y != that.y) { //north south
            result = y > that.y ? Direction.west : Direction.east;
        } else if (y == that.y && x != that.x) { //east west
            result = x > that.x ? Direction.north : Direction.south;
        } else if (Math.abs(this.x - that.x) == Math.abs(this.y - that.y)){
            if(this.x>that.x){
                result = this.y>that.y ? Direction.northwest : Direction.southwest;
            } else {
                result = this.y>that.y ? Direction.northeast : Direction.southeast;
            }
        }
        return result;
    }

    public int manhattanDistance(Coordinates other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public Coordinates vector(Coordinates that,int factor,int value){
        int deltaX = that.x - this.x;
        int deltaY = that.y - this.y;
        return new Coordinates(this.x + factor*deltaX, this.y + factor*deltaY,value);
    }

    @Override
    public int hashCode() {
        if (USE_FACING)
            return Objects.hash(getX(), getY(), getFacing());
        else
            return Objects.hash(getX(), getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coordinates that)) {
            return false;
        }
        return getX() == that.getX() && getY() == that.getY() && (!USE_FACING || Objects.equals(getFacing(), that.getFacing()));
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x=" + x + ", y=" + y + (USE_FACING ? " " + facing.getCharacter() : "") + '}';
    }
}
