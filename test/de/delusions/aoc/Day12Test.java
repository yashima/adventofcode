package de.delusions.aoc;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Day12Test {

    @Test
    public void testDigitization() {
        assertThat( Day12.digits( 1234, 5 ) ).containsExactly( 0, 1, 2, 3, 4 );
        assertThat( Day12.digits( 1234, 4 ) ).containsExactly( 1, 2, 3, 4 );
        assertThat( Day12.digits( 1230, 4 ) ).containsExactly( 1, 2, 3, 0 );
    }


    @Test
    public void testRecordSingleMatch() {
        Day12.SpringRow row = Day12.SpringRow.create( "???.### 1,1,3" );
        assertThat( row.broken() ).isEqualTo( 5 );
        assertThat( row.notBroken() ).isEqualTo( 2 );
        assertThat( row.brokenGroups() ).containsExactly( 1, 1, 3 );
        assertThat( row.convertGroupsToString() ).isNull();
        assertThat( row.groups() ).containsExactly( "???", "###" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = Day12.SpringRow.create( "?#?#?#?#?#?#?#? 1,3,1,6" );
        assertThat( row.broken() ).isEqualTo( 11 );
        assertThat( row.notBroken() ).isEqualTo( 4 );
        assertThat( row.brokenGroups() ).containsExactly( 1, 3, 1, 6 );
        assertThat( row.convertGroupsToString() ).isNull();
        assertThat( row.groups() ).containsExactly( "?#?#?#?#?#?#?#?" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = Day12.SpringRow.create( "????.#...#... 4,1,1" );
        assertThat( row.broken() ).isEqualTo( 6 );
        assertThat( row.notBroken() ).isEqualTo( 7 );
        assertThat( row.brokenGroups() ).containsExactly( 4, 1, 1 );
        assertThat( row.convertGroupsToString() ).isNull();
        assertThat( row.groups() ).containsExactly( "????", "#", "#" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );
    }

    @Test
    public void multipleMatches() {

        Day12.SpringRow row = Day12.SpringRow.create( ".??..??...?##. 1,1,3" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

        row = Day12.SpringRow.create( "????.######..#####. 1,6,5" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

        row = Day12.SpringRow.create( "?###???????? 3,2,1" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 10 );
    }

}
