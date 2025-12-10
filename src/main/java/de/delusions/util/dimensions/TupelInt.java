package de.delusions.util.dimensions;

import java.util.Arrays;

public class TupelInt extends Tupel<Integer> {

    public TupelInt(Integer[] data) {
        super(data);
    }

    public static TupelInt from(String dataString){
        Integer[] data = Arrays
                .stream(dataString.split(","))
                .mapToLong(s -> Integer.parseInt(s))
                .boxed()
                .toArray(Integer[]::new);
        return new TupelInt(data);
    }
}
