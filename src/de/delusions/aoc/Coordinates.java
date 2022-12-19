package de.delusions.aoc;

import java.util.Objects;

public class Coordinates {
    Coordinates previous;

    //TODO make accessors
    public int x;

    //TODO make accessors
    public int y;

    int value = 0;

    public Coordinates( String coordString, String divider, int value ) {
        this( coordString.split( divider ), value );
    }

    public Coordinates( String[] coords, int value ) {
        this( Integer.parseInt( coords[0].trim() ), Integer.parseInt( coords[1].trim() ), value );
    }

    public Coordinates( int x, int y ) {
        this( x, y, 0, null );
    }

    public Coordinates( int x, int y, int value ) {
        this( x, y, value, null );
    }

    public Coordinates( int x, int y, int value, Coordinates previous ) {
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

    public void setValue( int value ) {
        this.value = value;
    }

    public Coordinates moveTo( Direction move, int value ) {
        return moveTo( move, 1, value );
    }

    public Coordinates moveTo( Direction move, int distance, int value ) {
        return switch ( move ) {
            case west -> new Coordinates( this.x - distance, this.y, value, this );
            case east -> new Coordinates( this.x + distance, this.y, value, this );
            case south -> new Coordinates( this.x, this.y + distance, value, this );
            case north -> new Coordinates( this.x, this.y - distance, value, this );
            case southwest -> new Coordinates( this.x - distance, this.y + distance, value, this );
            case southeast -> new Coordinates( this.x + distance, this.y + distance, value, this );
            case northwest -> new Coordinates( this.x - distance, this.y - distance, value, this );
            case northeast -> new Coordinates( this.x + distance, this.y - distance, value, this );
        };
    }


    public Direction lookingTowards( Coordinates other ) {
        Direction result = null;
        if ( x == other.x && y != other.y ) { //north south
            result = y > other.y ? Direction.north : Direction.south;
        }
        else if ( y == other.y && x != other.x ) { //east west
            result = x > other.x ? Direction.west : Direction.east;
        }
        return result;
    }

    public int manhattanDistance( Coordinates other ) {
        return Math.abs( x - other.x ) + Math.abs( y - other.y );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Coordinates ) ) {
            return false;
        }
        Coordinates that = (Coordinates) o;
        return getX() == that.getX() && getY() == that.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash( getX(), getY() );
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x=" + x + ", y=" + y + '}';
    }
}
