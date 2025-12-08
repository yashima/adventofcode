package de.delusions.util.dimensions;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

public abstract class Tupel<T> {

    @Getter
    int dimensions;

    @Setter
    @Getter
    T[] data;

    public Tupel(T... data) {
        this.dimensions = data.length;
        this.data = data;
    }

    public Tupel(int dimensions) {
        this.dimensions = dimensions;
        this.data = (T[]) new Object[dimensions];
    }

    public T get(int position){
        return data[position];
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tupel<?> tupel)) return false;
        return dimensions == tupel.dimensions && Objects.deepEquals(data, tupel.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimensions, Arrays.hashCode(data));
    }

    @Override
    public String toString() {
        return "Tupel{" +
                "data=" + Arrays.toString(data) +
                '}';
    }

}
