package de.delusions.util;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class CoordinatesTest {

    @Test
    public void testLookingTowards() {
        Coordinates center = new Coordinates(1,1);
        List<Direction> fails = new ArrayList<>();
        for(Direction d : Direction.values()){
            center.setFacing(d);
            if(!center.lookingTowards(center.moveTo(d,3,1)).equals(d)){
                fails.add(d);
            }
        }
        assertThat(fails).isEmpty();
    }

}