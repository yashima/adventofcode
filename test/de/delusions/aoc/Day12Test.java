package de.delusions.aoc;

import main.java.de.delusions.aoc.advent2023.Day12;
import main.java.de.delusions.aoc.advent2023.Day12.SpringRow;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day12Test {


    @Test
    public void testRecordSingleMatch() {
        SpringRow row = SpringRow.create( "???.### 1,1,3" );
        assertThat( row.groups() ).containsExactly( "???", "###" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = SpringRow.create( "?#?#?#?#?#?#?#? 1,3,1,6" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );

        row = SpringRow.create( "????.#...#... 4,1,1" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 1 );
    }


    @Test
    public void multipleMatches() {

        assertThat( SpringRow.allUnknown( "???" ) ).isTrue();
        assertThat( SpringRow.allUnknown( "?#?" ) ).isFalse();
        assertThat( SpringRow.allBroken( "###" ) ).isTrue();
        assertThat( SpringRow.allBroken( "?#?" ) ).isFalse();

        SpringRow row = SpringRow.create( ".??..??...?##. 1,1,3" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

        row = SpringRow.create( "????.######..#####. 1,6,5" );
        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 4 );

    }

    @Test
    public void multipleMatchesSpecial() {

        SpringRow row = SpringRow.create( "?###???????? 3,2,1" );

        assertThat( Day12.getConfigurations( row ) ).isEqualTo( 10 );

    }

    @Test
    public void testFindImpossiblePrefix() {
        assertThat( Day12.isImpossible( List.of( "#", "#?" ), List.of( 2, 5 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "##", "??" ), List.of( 1, 2 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "##", "#", "?", "#" ), List.of( 2, 2, 2, 4 ) ) ).isTrue();
        assertThat( Day12.isImpossible( List.of( "#", "##" ), List.of( 1, 2, 3 ) ) ).isFalse();
    }

    @Test
    public void testRemovePrefix() {
        SpringRow springRow2 = new SpringRow( List.of( "#" ), List.of( 1 ) );
        while ( !springRow2.isDone() && springRow2.removeUnambiguousHead() ) {
            //do nothing?
        }
        assertThat( springRow2.isDone() ).isTrue();
        SpringRow springRow1 = new SpringRow( List.of( "#", "##" ), List.of( 1 ) );
        while ( !springRow1.isDone() && springRow1.removeUnambiguousHead() ) {
            //do nothing?
        }
        assertThat( springRow1 ).isEqualTo( new SpringRow( List.of( "##" ), List.of() ) );
        SpringRow springRow = new SpringRow( List.of( "?", "##", "?" ), List.of( 1, 2, 3 ) );
        while ( !springRow.isDone() && springRow.removeUnambiguousHead() ) {
            //do nothing?
        }
        assertThat( springRow ).isEqualTo( new SpringRow( List.of( "?" ), List.of( 3 ) ) );
    }

    @Test
    public void testSplitGroup() {
        assertThat( SpringRow.splitOff( "##?", 1 ) ).isNull();
        assertThat( SpringRow.splitOff( "#??", 4 ) ).isNull();
        assertThat( SpringRow.splitOff( "???", 1 ) ).isNull();

        assertThat( SpringRow.splitOff( "#??", 1 ) ).isEqualTo( "?" );
        assertThat( SpringRow.splitOff( "#??", 2 ) ).isEqualTo( "" );
        assertThat( SpringRow.splitOff( "#??", 3 ) ).isEqualTo( "" );


    }

}
