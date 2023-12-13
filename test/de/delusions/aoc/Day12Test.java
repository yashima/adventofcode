package de.delusions.aoc;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Day12Test {


    @Test
    public void testRecordSingleMatch() {
        Day12.SpringRow row = Day12.SpringRow.create( "???.### 1,1,3" );
        assertThat( row.matches( "#.#.###" ) ).isTrue();
        assertThat( row.groups() ).containsExactly( "???", "###" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = Day12.SpringRow.create( "?#?#?#?#?#?#?#? 1,3,1,6" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = Day12.SpringRow.create( "????.#...#... 4,1,1" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );
    }


    @Test
    public void multipleMatches() {

        Day12.SpringRow row = Day12.SpringRow.create( ".??..??...?##. 1,1,3" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

        row = Day12.SpringRow.create( "????.######..#####. 1,6,5" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

    }

    @Test
    public void multipleMatchesSpecial() {



        Day12.SpringRow row = Day12.SpringRow.create( "?###???????? 3,2,1" );

        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 10 );

    }

}
