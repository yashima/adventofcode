package de.delusions.aoc;

import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day12Test {


    @Test
    public void testRecordSingleMatch() {
        Day12.SpringRow row = Day12.SpringRow.create( "???.### 1,1,3" );
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

    @Test
    public void testFindImpossiblePrefix() {
        assertThat( Day12.isImpossible( List.of( "#", "#?" ), List.of( 2, 5 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "##", "??" ), List.of( 1, 2 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "##", "#", "?", "#" ), List.of( 2, 2, 2, 4 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "#", "##" ), List.of( 1, 2, 3 ) ) ).isFalse();

    }

}
