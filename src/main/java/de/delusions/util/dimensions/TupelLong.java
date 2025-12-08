package de.delusions.util.dimensions;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TupelLong extends Tupel<Long>{

    static MathContext mc = new MathContext(10);

    static AtomicInteger ID = new AtomicInteger(0);
    public static void resetID(){
        ID.set(0);
    }

    public static TupelLong from(String dataString){
        Long[] data = Arrays
                .stream(dataString.split(","))
                .mapToLong(s -> Long.parseLong(s))
                .boxed()
                .toArray(Long[]::new);
        return new TupelLong(ID.getAndIncrement(),data);
    }

    public static TupelLong from(Long... data){
        return new TupelLong(ID.getAndIncrement(),data);
    }

    @Getter
    int id;

    public TupelLong(int id,Long... data) {
        super(data);
        this.id = id;
    }

    public Long distanceManhattan(TupelLong other) {
        if (this.dimensions != other.dimensions) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        long sum = 0L ;
        for (int i = 0; i < this.dimensions; i++) {
            sum += Math.abs(this.data[i] - other.data[i]);
        }
        return sum;
    }

    public BigInteger distanceManhattanBig(TupelLong other) {
        if (this.dimensions != other.dimensions) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < this.dimensions; i++) {
            sum = sum.add(BigInteger.valueOf(Math.abs(this.data[i] - other.data[i])));
        }
        return sum;
    }

    public BigDecimal distance(TupelLong other) {
        if (this.dimensions != other.dimensions) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < this.dimensions; i++) {
            BigDecimal diff = BigDecimal.valueOf(this.data[i]-other.data[i]);
            sum = sum.add(diff.multiply(diff)); // (a_i - b_i)^2
        }
        // sqrt as BigDecimal with given precision
        return sum.sqrt(mc);
    }


    @Override
    public String toString() {
        return "TupelLong{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
