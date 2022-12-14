import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class AdventOfCode2022
{

    //-------------- DAY 1: elves expedition -------------------

    static Integer day1Part1( Stream<String> input){
        return dayOne(input, 10000);
    }

    static Integer day1Part2( Stream<String> input){
        return dayOne(input, 3);
    }
    static int dayOne( Stream<String> input, int numberOfElves )
    {
        AtomicInteger current = new AtomicInteger( 0 );
        return input.map( line -> line.isEmpty() ? "skip" + current.incrementAndGet() : current.get() + "-" + line )//
            .filter( l -> l.contains( "-" ) ) //
            .collect(
                Collectors.toMap( l -> l.split( "-" )[0], l -> Integer.parseInt( l.split( "-" )[1] ), Integer::sum ) )//
            .values().stream().sorted( Comparator.reverseOrder() ) //
            .limit( numberOfElves ) //
            .reduce( 0, Integer::sum );
    }

    //-------------- DAY 2: playing games -------------------

    static int day2Part1( Stream<String> input )
    {
        return input.map( line -> {
            int opponent = ( line.charAt( 0 ) + 1 ) % 3 + 1;
            int me = ( line.charAt( 2 ) - 1 ) % 3 + 1;
            return me + ( me - opponent == 0 ? 3 : ( List.of( 1, -2 ).contains( me - opponent ) ? 6 : 0 ) );
        } ).reduce( 0, Integer::sum );
    }

    static int day2Part2( Stream<String> input )
    {
        return input.map(
            line -> List.of( "fnord", "B X", "C X", "A X", "A Y", "B Y", "C Y", "C Z", "A Z", "B Z" ).indexOf(
                line ) ).reduce( 0, Integer::sum );
    }

    //-------------- DAY 3: something in the backpacks -------------------
    static int day3Part1( Stream<String> input )
    {
        return input.map( backpack -> backpack.substring( backpack.length() / 2 ).chars().distinct().filter(
                    type -> backpack.substring( 0, backpack.length() / 2 ).chars().filter(
                        c -> c == type ).findFirst().isPresent() ) //
                .map( AdventOfCode2022::priority ) //ascii offset
                .sum() ) //
            .reduce( 0, Integer::sum );
    }

    static int day3Part2( Stream<String> input )
    {
        Stack<List<String>> elfGroups = new Stack<>();
        elfGroups.push( new ArrayList<>() );
        input.forEach( elf -> {
            if ( elfGroups.isEmpty() || elfGroups.peek().size() == 3 )
            {
                elfGroups.push( new ArrayList<>() );
            }
            elfGroups.peek().add( elf );
        } );
        return elfGroups.stream().map( group -> group.get( 0 ).chars().filter(
            type -> isInPack( group.get( 1 ), type ) && isInPack( group.get( 2 ), type ) ).map(
            AdventOfCode2022::priority ).findFirst().orElseGet( null ) ).reduce( 0, Integer::sum );

    }

    static int priority( int type )
    {
        return type < 97 ? type - 64 + 26 : type - 96;
    }

    static boolean isInPack( String backpack, int type )
    {
        return backpack.chars().filter( type1 -> type1 == type ).findFirst().isPresent();
    }

    //-------------- DAY 4: pairing food -------------------

    static Pattern day4Pattern = Pattern.compile( "([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)", Pattern.MULTILINE );

    record Pair(Coordinates foo, Coordinates bar)
    {
    }

    static long day4Part1( Stream<String> input )
    {
        return input.map( AdventOfCode2022::parseInputDay4 ).filter( Objects::nonNull ).filter(
            pair -> contains( pair.foo, pair.bar ) || contains( pair.bar, pair.foo ) ).count();
    }

    static long day4Part2( Stream<String> input )
    {
        return input.map( AdventOfCode2022::parseInputDay4 ).filter( Objects::nonNull ).filter(
            pair -> overlap( pair.foo, pair.bar ) || contains( pair.foo, pair.bar ) ).count();
    }

    static Pair parseInputDay4( String input )
    {
        final Matcher matcher = day4Pattern.matcher( input );
        return matcher.matches() ? new Pair( new Coordinates( matcher.group( 1 ), matcher.group( 2 ) ),
                                             new Coordinates( matcher.group( 3 ), matcher.group( 4 ) ) ) : null;
    }

    static boolean contains( Coordinates foo, Coordinates bar )
    {
        return foo.x <= bar.x && foo.y >= bar.y;
    }

    static boolean overlap( Coordinates foo, Coordinates bar )
    {
        return foo.x <= bar.x && bar.x <= foo.y;
    }


    //-------------- DAY 5: stacking boxes -------------------
    final static Pattern day5MovePattern =
        Pattern.compile( "move ([0-9]+) from ([0-9]+) to ([0-9]+)", Pattern.MULTILINE );

    final static Pattern day5StackPattern =
        Pattern.compile( ".(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.).", Pattern.MULTILINE );

    record Move(int number, int from, int to)
    {
    }

    static String day5Part1( Stream<String> input )
    {
        Map<Integer, LinkedList<String>> boxes = new HashMap<>();
        input.forEach( line -> {
            final Matcher stackMatcher = day5StackPattern.matcher( line );
            if ( stackMatcher.matches() )
            {
                for ( int group = 1; group <= 9; group++ )
                {
                    String match = stackMatcher.group( group ).trim();
                    if ( !match.isEmpty() && match.matches( "[A-Z]" ) )
                    {
                        boxes.putIfAbsent( group, new LinkedList<>() );
                        boxes.get( group ).add( match );
                    }
                }
            }
            final Matcher moveMatcher = day5MovePattern.matcher( line );
            if ( moveMatcher.matches() )
            {
                Move move =
                    new Move( Integer.parseInt( moveMatcher.group( 1 ) ), Integer.parseInt( moveMatcher.group( 2 ) ),
                              Integer.parseInt( moveMatcher.group( 3 ) ) );
                LinkedList<String> lifter = new LinkedList<>();
                for ( int n = 0; n < move.number; n++ )
                {
                    lifter.push( boxes.get( move.from ).pop() );
                }
                lifter.forEach( box -> boxes.get( move.to ).push( box ) );
            }
        } );
        return boxes.keySet().stream().sorted().map( key -> boxes.get( key ).pop() ).reduce( "", ( a, b ) -> a + b );
    }

    //-------------- DAY 6: -------------------

    static int day6Part1( Stream<String> input )
    {
        String line = input.reduce( "", ( a, b ) -> a + b );
        int magicNumber = 14;
        for ( int idx = 0; idx < line.length() - magicNumber; idx++ )
        {
            if ( line.substring( idx, idx + magicNumber ).chars().distinct().count() == magicNumber )
            {
                return idx + magicNumber;
            }
        }
        return 0;
    }

    //-------------- DAY 7: Directories -------------------
    static int day7Part1( Stream<String> input )
    {
        Directory root = new Directory( "/", null );
        final List<Directory> directories = new ArrayList<>();
        final AtomicReference<Directory> current = new AtomicReference<>( root );
        input.skip( 2 ).forEach( line -> {
            if ( line.startsWith( "$" ) )
            {
                if ( line.equals( "$ cd .." ) )
                {
                    current.set( current.get().parent );
                }
                else if ( line.startsWith( "$ cd" ) )
                {
                    current.set( current.get().directories.get( line.substring( 5 ) ) );
                }
            }
            else if ( line.startsWith( "dir" ) )
            {
                directories.add( current.get().addDirectory( line ) );
            }
            else
            {
                current.get().addFile( line );
            }
        } );
        int needed = 30000000;
        int space = 70000000 - root.size();
        return directories.stream().map( Directory::size ).filter( s -> s < 100000 ).reduce( 0, Integer::sum );
        //return directories.stream().map( Directory::size ).filter( size -> space + size > needed ).min( Integer::compareTo ).get();
    }

    static class Directory
    {
        Map<String, Directory> directories = new HashMap<>();

        Map<String, Integer> files = new HashMap<>();

        Directory parent;

        String name;

        Directory( String name, Directory parent )
        {
            this.name = name;
            this.parent = parent;
        }

        void addFile( String line )
        {
            String[] pair = line.split( " " );
            files.put( pair[1], Integer.parseInt( pair[0] ) );
        }

        Directory addDirectory( String line )
        {
            Directory directory = new Directory( line.split( " " )[1], this );
            directories.put( directory.name, directory );
            return directory;
        }

        int size()
        {
            return files.values().stream().reduce( 0, Integer::sum ) +
                directories.values().stream().map( Directory::size ).reduce( 0, Integer::sum );
        }
    }

    //-------------- DAY 8: Scenic Treepatch -------------------
    static int day8Part1( Stream<String> input )
    {
        Set<Coordinates> visibleTrees = new HashSet<>();

        //int[][] treePatch = { { 0,0,0,0}, { 0,0,0,0}, { 0,0,0,0}, { 0,0,0,0} };

        int[][] treePatch = input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new );

        for ( int row = 0; row < treePatch.length; row++ )
        {
            int westSize = -1, eastSize = -1, northSize = -1, southSize = -1;
            int rowLength = treePatch[row].length;
            for ( int col = 0; col < rowLength; col++ )
            {
                westSize = getMaxSize( visibleTrees, treePatch, row, col, westSize );
                eastSize = getMaxSize( visibleTrees, treePatch, row, rowLength - col - 1, eastSize );
                northSize = getMaxSize( visibleTrees, treePatch, col, row, northSize );
                southSize = getMaxSize( visibleTrees, treePatch, rowLength - col - 1, row, southSize );
            }
        }
        return visibleTrees.size();
    }

    static int getMaxSize( Set<Coordinates> visibleTrees, int[][] treePatch, int row, int col,
                                   int maxSizeForRow )
    {
        Coordinates tree = new Coordinates( row, col, treePatch[row][col] );
        if ( tree.value > maxSizeForRow )
        {
            maxSizeForRow = tree.value;
            visibleTrees.add( tree );
        }
        return maxSizeForRow;
    }

    static int day8Part2( Stream<String> input )
    {
        List<Integer> scenery = new ArrayList<>();
        int[][] treePatch = input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new );

        for ( int row = 1; row < treePatch.length - 1; row++ )
        {
            int rowLength = treePatch[row].length;
            for ( int col = 1; col < rowLength - 1; col++ )
            {
                scenery.add( scenicScore( treePatch, row, col ) );
            }
        }
        return scenery.stream().max( Integer::compareTo ).get();
    }

    static Integer scenicScore( int[][] treePatch, int row, int col )
    {
        int treeSize = treePatch[row][col];
        boolean west = true, east = true, south = true, north = true;
        int score = 1;
        int radius = 1;
        while ( west || east || south || north && score > 0 )
        {

            if ( west && lastTree( treePatch, treeSize, row, col - radius, false ) )
            {
                west = false;
                score = score * radius;
            }
            if ( east && lastTree( treePatch, treeSize, row, col + radius, false ) )
            {
                east = false;
                score = score * radius;
            }
            if ( south && lastTree( treePatch, treeSize, row + radius, col, true ) )
            {
                south = false;
                score = score * radius;
            }
            if ( north && lastTree( treePatch, treeSize, row - radius, col, true ) )
            {
                north = false;
                score = score * radius;
            }
            radius++;
        }
        return score;
    }

    static boolean lastTree( int[][] treePatch, int treeSize, int row, int col, boolean checkRow )
    {
        return ( checkRow ? ( row == 0 || row == treePatch.length - 1 )
            : ( col == 0 || col == treePatch[row].length - 1 ) ) || treePatch[row][col] >= treeSize;
    }

    //-------------- DAY 9: moving ropes -------------------
    static int day9Part1( Stream<String> input )
    {
        Set<Coordinates> positionsVisited = new HashSet<>();
        RowEnd head = new RowEnd( "head" );
        List<RowEnd> knots = IntStream.range( 1, 10 ).mapToObj( idx -> new RowEnd( "t" + idx ) ).toList();
        input.forEach( line -> {
            head.receiveCommand( line );
            while ( head.processCommand() )
            {
                RowEnd current = head;
                for ( int idx = 0; idx < knots.size(); idx++ )
                {
                    RowEnd knot = knots.get( idx );
                    Coordinates position = knot.follow( current );
                    current = knot;
                    if ( idx + 1 == knots.size() )
                    {
                        positionsVisited.add( position );
                    }
                }
            }
        } );
        return positionsVisited.size();
    }


    static class RowEnd
    {
        String name;

        int row = 0;

        int col = 0;

        int steps = 0;

        String direction;

        RowEnd( String name )
        {
            this.name = name;
        }

        void receiveCommand( String line )
        {
            String[] pair = line.split( " " );
            direction = pair[0];
            steps = Integer.parseInt( pair[1] );
        }

        boolean processCommand()
        {
            if ( steps == 0 )
            {
                return false;
            }
            switch ( direction )
            {
                case "U":
                    this.row++;
                    break;
                case "D":
                    this.row--;
                    break;
                case "L":
                    this.col--;
                    break;
                case "R":
                    this.col++;
                    break;
            }
            steps--;
            return true;
        }

        Coordinates follow( RowEnd head )
        {
            if ( Math.abs( this.row - head.row ) >= 2 || Math.abs( this.col - head.col ) >= 2 )
            {
                col = col + Integer.compare( head.col, this.col );
                row = row + Integer.compare( head.row, this.row );
            }
            return new Coordinates( row, col );
        }
    }

    //-------------- DAY 10: CRT Display -------------------

    static AtomicInteger clock = new AtomicInteger( 0 );

    static AtomicInteger register = new AtomicInteger( 1 );

    static List<String> crt = new ArrayList<>();

    static int WIDTH = 40;

    static int day10Part1( Stream<String> lines )
    {
        AtomicInteger frequency = new AtomicInteger( 0 );
        lines.forEach( line -> {
            frequency.addAndGet( computeCycle( 0 ) );
            if ( line.startsWith( "addx" ) )
            {
                frequency.addAndGet( computeCycle( Integer.parseInt( line.substring( 5 ) ) ) );
            }
        } );
        crt.forEach( System.out::print );
        return frequency.get();
    }

    static int computeCycle( int delta )
    {
        int cycle = clock.incrementAndGet();
        int X = register.addAndGet( delta );
        crt.add( ( Math.abs( X - ( cycle % WIDTH ) ) <= 1 ? "#" : "." ) + ( ( cycle ) % WIDTH == 0 ? "\n" : "" ) );
        return List.of( 20, 60, 100, 140, 180, 220 ).contains( cycle ) ? cycle * X : 0;
    }

    //-------------- DAY 11: Monkey Business -------------------

    static long day11Part1( Stream<String> input )
    {
        List<String> lines = input.map( String::trim ).filter( line -> !line.isEmpty() ).toList();
        List<Monkey> monkeys = new ArrayList<>();
        int monkeyDef = 6;
        for ( int idx = 0; idx < 8; idx++ )
        {
            int start = idx * monkeyDef;
            Monkey monkey = Monkey.parseFromStrings( lines.subList( start, start + monkeyDef ), monkeys );
            monkeys.add( monkey );
            System.out.println( "Init: " + monkey );
        }
        for ( int round = 0; round < 10000; round++ )
        {
            monkeys.forEach( Monkey::inspect );
        }
        return monkeys.stream().peek( System.out::println ).map( Monkey::getBusiness ).sorted(
            Comparator.reverseOrder() ).limit( 2 ).reduce( 1L, ( a, b ) -> a * b );
    }

    static class Monkey
    {
        BigInteger MAGIC = BigInteger.valueOf( 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 );

        BigInteger lowerAnxiety( BigInteger currentAnxiety )
        {
            return currentAnxiety.mod( MAGIC );
        }

        Stack<BigInteger> items = new Stack<>();

        Function<BigInteger, BigInteger> monkeyFingers;

        BigInteger monkeyTest;

        int trueMonkey;

        int falseMonkey;

        long business = 0;

        List<Monkey> monkeys;

        public Long getBusiness()
        {
            return business;
        }

        void inspect()
        {
            while ( !items.isEmpty() )
            {
                BigInteger newItemAnxietyLevel = lowerAnxiety( monkeyFingers.apply( items.pop() ) );
                if ( newItemAnxietyLevel.mod( monkeyTest ).equals( BigInteger.ZERO ) )
                {
                    monkeys.get( trueMonkey ).items.push( newItemAnxietyLevel );
                }
                else
                {
                    monkeys.get( falseMonkey ).items.push( newItemAnxietyLevel );
                }
                business++;
            }
        }

        public String toString()
        {
            return String.format( "Monkey[items=%s,div=%s, true=%s,false=%s, busy=%s]", items, monkeyTest, trueMonkey,
                                  falseMonkey, business );
        }

        static Monkey parseFromStrings( List<String> monkeyString, List<Monkey> monkeys )
        {
            Monkey monkey = new Monkey();
            Matcher itemMatcher = Pattern.compile( "(\\d+)" ).matcher( monkeyString.get( 1 ) );
            while ( itemMatcher.find() )
            {
                monkey.items.push( BigInteger.valueOf( Long.parseLong( itemMatcher.group( 1 ) ) ) );
            }
            Matcher operationMatcher =
                Pattern.compile( "Operation: new = old ([\\*\\+]) (\\d+|old)" ).matcher( monkeyString.get( 2 ) );
            if ( operationMatcher.matches() )
            {
                boolean sum = operationMatcher.group( 1 ).equals( "+" );
                String operand = operationMatcher.group( 2 );
                if ( !operand.equals( "old" ) )
                {
                    BigInteger op = BigInteger.valueOf( Long.parseLong( operand ) );
                    monkey.monkeyFingers = item -> sum ? item.add( op ) : item.multiply( op );
                }
                else
                {
                    monkey.monkeyFingers = item -> item.multiply( item );
                }
            }
            Matcher testMatcher = Pattern.compile( "Test: divisible by (\\d+)" ).matcher( monkeyString.get( 3 ) );
            if ( testMatcher.matches() )
            {
                monkey.monkeyTest = BigInteger.valueOf( Long.parseLong( testMatcher.group( 1 ) ) );
            }
            Pattern pattern = Pattern.compile( "If (true|false): throw to monkey (\\d+)" );
            Matcher trueMonkeyMatcher = pattern.matcher( monkeyString.get( 4 ) );
            Matcher falseMonkeyMatcher = pattern.matcher( monkeyString.get( 5 ) );
            if ( trueMonkeyMatcher.matches() )
            {
                monkey.trueMonkey = Integer.parseInt( trueMonkeyMatcher.group( 2 ) );
            }
            if ( falseMonkeyMatcher.matches() )
            {
                monkey.falseMonkey = Integer.parseInt( falseMonkeyMatcher.group( 2 ) );
            }
            monkey.monkeys = monkeys;
            return monkey;
        }
    }

    //-------------- DAY 12 paths into the hills -------------------
    static int[][] HILL = {{}};

    static List<Coordinates> GOALS = new ArrayList<>();

    static int day12Part1( Stream<String> input )
    {
        HILL = input.map( line -> line.chars().map( c -> c - 96 ).toArray() ).toArray( int[][]::new );
        GOALS.addAll( findAllPositions( 'a' ) );
        Coordinates startPos = findPosition( 'E' );

        Map<Coordinates, Path> openList = new HashMap<>();
        Map<Coordinates, Path> closedList = new HashMap<>();

        Path start = new Path( startPos.x, startPos.y, null, "o" );
        openList.put( start.pos, start );

        List<Path> candidates = new ArrayList<>();
        while ( !openList.isEmpty() )
        {

            Path path = openList.values().stream().min( Comparator.comparing( Path::getFnord ) ).get();
            openList.remove( path.pos );

            List<Path> children = Stream.of( 'U', 'D', 'L', 'R' )//
                .map( path::move )//
                .filter( Objects::nonNull )//
                .filter( Path::isLegal ) //
                .toList();

            children.forEach( child -> {
                if ( hasReachedGoal( child, GOALS ) )
                {
                    candidates.add( child );
                    return;
                }
                if ( ( !openList.containsKey( child.pos ) || openList.get( child.pos ).fnord > child.fnord ) &&
                    ( !closedList.containsKey( child.pos ) || closedList.get( child.pos ).fnord > child.fnord ) )
                {
                    openList.put( child.pos, child );
                }
            } );
            closedList.put( path.pos, path );
        }

        return candidates.stream().map( path -> path.steps ).min( Integer::compareTo ).get();
    }

    static boolean hasReachedGoal( Path path, List<Coordinates> goals )
    {
        return goals.contains( path.pos );
    }

    static class Path
    {
        Path parent;

        Coordinates pos;

        String move;

        int height;

        int cost;

        int fnord;

        int steps;


        Path( int x, int y, Path parent, String move )
        {
            this.pos = new Coordinates( x, y );
            this.move = move;
            this.parent = parent;
            this.height = parent == null ? 0 : HILL[x][y];
            this.cost = 1 + ( parent == null ? 0 : parent.cost );
            this.fnord = cost + lineOfSight();
            this.steps = parent == null ? 0 : parent.steps + 1;
        }

        int lineOfSight()
        {
            return GOALS.stream().map( g -> Math.abs( pos.x - g.x ) + Math.abs( pos.y - g.y ) ).min(
                Integer::compareTo ).get();
        }

        boolean isLegal()
        {
            return parent.height - height <= 1;
        }

        public String toString()
        {
            return parent + " -> " + cost + "[" + pos.x + ":" + pos.y + "]";
        }

        public int getFnord()
        {
            return fnord;
        }

        Path move( char move )
        {
            try
            {
                return switch ( move )
                    {
                        case 'L' -> new Path( this.pos.x, this.pos.y - 1, this, "<" );
                        case 'R' -> new Path( this.pos.x, this.pos.y + 1, this, ">" );
                        case 'U' -> new Path( this.pos.x - 1, this.pos.y, this, "^" );
                        case 'D' -> new Path( this.pos.x + 1, this.pos.y, this, "v" );
                        default -> null;
                    };
            }
            catch ( ArrayIndexOutOfBoundsException e )
            {
                return null;
            }
        }

        String prettyPrint()
        {
            Map<Coordinates, Path> positions = new HashMap<>();
            Path current = this;
            while ( current != null )
            {
                positions.put( current.pos, current );
                current = current.parent;
            }
            StringBuilder builder = new StringBuilder();
            for ( int x = 0; x < HILL.length; x++ )
            {
                for ( int y = 0; y < HILL[0].length; y++ )
                {
                    Coordinates currentPos = new Coordinates( x, y );
                    if ( GOALS.contains( currentPos ) )
                    {
                        builder.append( "X" );
                    }
                    else if ( positions.containsKey( currentPos ) )
                    {
                        builder.append( positions.get( currentPos ).move );
                    }
                    else
                    {
                        builder.append( "_" );
                    }
                }
                builder.append( "\n" );
            }
            builder.append( "Steps=" );
            builder.append( this.steps );
            return builder.toString();
        }
    }

    static Coordinates findPosition( char search )
    {
        for ( int x = 0; x < HILL.length; x++ )
        {
            for ( int y = 0; y < HILL[0].length; y++ )
            {
                if ( HILL[x][y] == (int) search - 96 )
                {
                    return new Coordinates( x, y );

                }
            }
        }
        return null;
    }

    static List<Coordinates> findAllPositions( char search )
    {
        List<Coordinates> positions = new ArrayList<>();
        for ( int x = 0; x < HILL.length; x++ )
        {
            for ( int y = 0; y < HILL[0].length; y++ )
            {
                if ( HILL[x][y] == (int) search - 96 )
                {
                    positions.add( new Coordinates( x, y ) );

                }
            }
        }
        return positions;
    }

    //-------------- DAY 13 SOS Packets -------------------
    static int day13Part1( Stream<String> input )
    {
        List<String> lines = input.filter( Predicate.not( String::isBlank ) ).toList();
        List<Packet> packets = new ArrayList<>();
        for ( int idx = 0; idx < lines.size(); idx = idx + 2 )
        {
            packets.add( new Packet( lines.get( idx ), lines.get( idx + 1 ) ) );
        }
        return packets.stream().filter( Packet::isOrdered ).map( Packet::getPosition ).reduce( 0, Integer::sum );
    }

    static int day13Part2( Stream<String> input )
    {
        PacketContent dividerA = new PacketContent( "[[2]]" );
        PacketContent dividerB = new PacketContent( "[[6]]" );
        List<PacketContent> packets =
            new ArrayList<>( input.filter( Predicate.not( String::isBlank ) ).map( PacketContent::new ).toList() );
        packets.add( dividerB );
        packets.add( dividerA );
        List<PacketContent> packetContents = packets.stream().sorted().peek( System.out::println ).toList();
        return ( packetContents.indexOf( dividerA ) + 1 ) * ( packetContents.indexOf( dividerB ) + 1 );
    }

    static AtomicInteger PACKET_ID = new AtomicInteger( 1 );

    static class Packet
    {
        PacketContent left;

        PacketContent right;

        int position = PACKET_ID.getAndIncrement();

        Packet( String left, String right )
        {
            this.left = new PacketContent( left );
            this.right = new PacketContent( right );
        }

        boolean isOrdered()
        {
            return left.compareTo( right ) <= 0;
        }

        public int getPosition()
        {
            return position;
        }

        @Override
        public String toString()
        {
            return position + ":" + left.toString() + " | " + right.toString();
        }
    }

    static class PacketContent
        implements Comparable<PacketContent>
    {
        Integer value = null;

        List<PacketContent> contents = new ArrayList<>();

        PacketContent( String packetString )
        {
            Matcher packetMatcher = Pattern.compile( "\\[(.*)\\]" ).matcher( packetString );
            Matcher numberMatcher = Pattern.compile( "(\\d+)" ).matcher( packetString );
            if ( packetString.equals( "[]" ) )
            {
                //do nothing
            }
            else if ( packetMatcher.matches() )
            {
                String[] children = packetMatcher.group( 1 ).split( "," );
                int depth = 0;
                StringBuilder childPacketString = new StringBuilder();
                for ( String child : children )
                {
                    depth = depth +
                        child.chars().map( c -> c == '[' ? 1 : ( c == ']' ? -1 : 0 ) ).reduce( 0, Integer::sum );
                    childPacketString.append( ( childPacketString.length() == 0 ) ? "" : "," ).append( child );
                    if ( depth == 0 )
                    {
                        contents.add( new PacketContent( childPacketString.toString() ) );
                        childPacketString = new StringBuilder();
                    }
                }
            }
            else if ( numberMatcher.find() )
            {
                value = Integer.parseInt( numberMatcher.group( 1 ) );
            }
        }

        @Override
        public int compareTo( PacketContent o )
        {
            int result = 0;
            if ( o == null )
            {
                throw new IllegalStateException( "Oops" );
            }
            else if ( value != null && o.value != null )
            {
                result = value.compareTo( o.value );
            }
            else if ( value == null && o.value == null )
            {
                result = this.compareContents( o.contents );
            }
            else if ( value == null )
            {
                result = this.compareContents( List.of( o ) );
            }
            else
            {
                result = -o.compareContents( List.of( this ) );
            }
            return result;
        }

        private int compareContents( List<PacketContent> otherContents )
        {
            LinkedList<PacketContent> otherStack = new LinkedList<>( otherContents );
            LinkedList<PacketContent> stack = new LinkedList<>( this.contents );
            while ( !otherStack.isEmpty() && !stack.isEmpty() )
            {
                PacketContent content = stack.pop();
                PacketContent otherContent = otherStack.pop();
                int result = content.compareTo( otherContent );
                if ( result != 0 )
                {
                    return result;
                }
            }
            return Integer.compare( stack.size(), otherStack.size() );
        }

        @Override
        public String toString()
        {
            return value == null ? contents.toString() : value.toString();
        }
    }

    //-------------- DAY 14 Falling Sand -------------------
    static Integer day14( Stream<String> input )
    {
        List<String> testInput = List.of( "498,4 -> 498,6 -> 496,6", "503,4 -> 502,4 -> 502,9 -> 494,9" );
        List<List<Coordinates>> walls = testInput.stream().map( line -> Arrays.stream( line.split( "->" ) ).map(
            coord -> new Coordinates( coord, "," ) ).toList() ).toList();
        int xMin =
            walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).min( Integer::compareTo ).get();
        int xMax =
            walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).max( Integer::compareTo ).get();
        int yMin =
            walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).min( Integer::compareTo ).get();
        int yMax =
            walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).max( Integer::compareTo ).get();
        int[][] cave = new int[xMax - xMin][yMax - yMin];
        //xMin and yMin double as offsets
        Coordinates previous = null;
        for ( List<Coordinates> wall : walls )
        {
            for ( Coordinates coordinate : wall )
            {

                previous = coordinate;
            }
            previous = null;
        }
        return 0;
    }

    enum Direction
    {
        same,
        north,
        south,
        east,
        west
    }

    static class Coordinates
    {
        Coordinates previous;

        int x;

        int y;

        int value = 0;

        int visitCount = 0;

        Coordinates( String coordString, String divider )
        {
            this( coordString.split( divider ) );
        }

        Coordinates( String[] coords )
        {
            this( Integer.parseInt( coords[0] ), Integer.parseInt( coords[0] ) );
        }

        Coordinates( int x, int y )
        {
            this( x, y, 0, null );
        }

        Coordinates( int x, int y, int value )
        {
            this( x, y, value, null );
        }

        Coordinates( int x, int y, int value, Coordinates previous )
        {
            this.previous = previous;
            this.x = x;
            this.y = y;
            this.value = value;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        Coordinates move( Direction move )
        {
            try
            {
                return switch ( move )
                    {
                        case west -> new Coordinates( this.x, this.y - 1, 0, this );
                        case east -> new Coordinates( this.x, this.y + 1, 0, this );
                        case north -> new Coordinates( this.x - 1, this.y, 0, this );
                        case south -> new Coordinates( this.x + 1, this.y, 0, this );
                        default -> null;
                    };
            }
            catch ( ArrayIndexOutOfBoundsException e )
            {
                return null;
            }
        }

        Direction getDirection( Coordinates other )
        {
            Direction result;
            if ( x == other.x && y == other.y )
            {
                result = Direction.same;
            }
            else if ( x == other.x && y < other.y )
            {
                result = Direction.west;
            }
            else if ( x == other.x )
            {
                result = Direction.east;
            }
            else if ( y == other.y && x < other.x )
            {
                result = Direction.north;
            }
            else if ( y == other.y )
            {
                result = Direction.south;
            }
            else
            {
                result = null;
            }
            return result;
        }
    }

    static class Day {
        static String PATH = "C:\\Users\\"+System.getProperty( "user.name" )+"\\Downloads\\day%s-input.txt";
        static String TEST_PATH = "C:\\Users\\"+System.getProperty( "user.name" )+"\\Downloads\\day%s-input.txt";
        int day;
        String tag;

        Function<Stream<String>,Object> part1;
        Function<Stream<String>,Object> part2;

        Day(int day,String tag,Function<Stream<String>,Object> part1, Function<Stream<String>,Object> part2 ){
            this.day = day;
            this.tag = tag;
            this.part1 = part1;
            this.part2 = part2;
        }

        void run(boolean test) {
            try
            {
                if(part1!=null){
                    Stream<String> input = Files.lines(Paths.get(String.format(test? TEST_PATH:PATH,day)));
                    System.out.println("Day "+day+", part1: "+tag+"="+part1.apply( input ));
                }
                if(part2!=null){
                    Stream<String> input = Files.lines(Paths.get(String.format(test? TEST_PATH:PATH,day)));
                    System.out.println("Day "+day+", part2: "+tag+"="+part2.apply( input ));
                }
            }
            catch ( IOException e )
            {
                System.out.println("File not found: "+e.getMessage());
            }
        }

    }

    public static void main( String[] args )
        throws Exception
    {

        List<Day> daysOfAdvent = new ArrayList<>();
        daysOfAdvent.add(new Day( 14,"Sand", AdventOfCode2022::day14, null ));


        if ( false )
        {
            daysOfAdvent.add(new Day( 1,"Calories", AdventOfCode2022::day1Part1, AdventOfCode2022::day1Part2 ));
            daysOfAdvent.add(new Day( 2,"Game", AdventOfCode2022::day2Part1, AdventOfCode2022::day2Part2 ));
            daysOfAdvent.add(new Day( 3,"Mistakes", AdventOfCode2022::day3Part1, AdventOfCode2022::day3Part2 ));
            daysOfAdvent.add(new Day( 4,"Overlaps", AdventOfCode2022::day4Part1, AdventOfCode2022::day4Part2 ));
            daysOfAdvent.add(new Day( 5,"Stacks", AdventOfCode2022::day5Part1, null ));
            daysOfAdvent.add(new Day( 6,"Tuning", AdventOfCode2022::day6Part1, null ));
            daysOfAdvent.add(new Day( 7,"Directory", AdventOfCode2022::day7Part1, null ));
            daysOfAdvent.add(new Day( 8,"Scenery", AdventOfCode2022::day8Part1, AdventOfCode2022::day8Part2 ));
            daysOfAdvent.add(new Day( 9,"Positions", AdventOfCode2022::day9Part1, null ));
            daysOfAdvent.add(new Day( 10,"Frequency", AdventOfCode2022::day10Part1, null ));
            daysOfAdvent.add(new Day( 11,"Monkey", AdventOfCode2022::day11Part1, null ));
            daysOfAdvent.add(new Day( 12,"Steps", AdventOfCode2022::day12Part1, null ));
            daysOfAdvent.add(new Day( 13,"Order", AdventOfCode2022::day13Part1, AdventOfCode2022::day13Part2 ));
        }

        for(Day day : daysOfAdvent){
            day.run( false );
        }

    }

}