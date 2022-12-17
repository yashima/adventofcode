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

class AdventOfCode2022 {

    abstract static class Day<T> {

        static String PATH = "C:\\Users\\" + System.getProperty( "user.name" ) + "\\Downloads\\day%s-input.txt";

        static String TEST_PATH = "C:\\Users\\" + System.getProperty( "user.name" ) + "\\Downloads\\day%s-test.txt";

        int day;

        String tag;

        abstract T part1( Stream<String> input );

        abstract T part2( Stream<String> input );


        Day( int day, String tag ) {
            this.day = day;
            this.tag = tag;
        }

        void run( boolean test ) {
            System.out.println( "Day " + day + ", part1: " + tag + "=" + part1( getInput( test ) ) );
            System.out.println( "Day " + day + ", part2: " + tag + "=" + part2( getInput( test ) ) );
        }

        Stream<String> getInput( boolean test ) {
            try {

                return Files.lines( Paths.get( String.format( test ? TEST_PATH : PATH, day ) ) );

            }
            catch ( IOException e ) {
                System.out.println( "File not found: " + e.getMessage() );
                return new ArrayList<String>().stream();
            }
        }

    }

    static class Coordinates {
        Coordinates previous;

        int x;

        int y;

        int value = 0;

        Coordinates( String coordString, String divider, int value ) {
            this( coordString.split( divider ), value );
        }

        Coordinates( String[] coords, int value ) {
            this( Integer.parseInt( coords[0].trim() ), Integer.parseInt( coords[1].trim() ), value );
        }

        Coordinates( int x, int y ) {
            this( x, y, 0, null );
        }

        Coordinates( int x, int y, int value ) {
            this( x, y, value, null );
        }

        Coordinates( int x, int y, int value, Coordinates previous ) {
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

        Coordinates moveTo( Direction move, int value ) {
            return moveTo( move, 1, value );
        }

        Coordinates moveTo( Direction move, int distance, int value ) {
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


        Direction lookingTowards( Coordinates other ) {
            Direction result = null;
            if ( x == other.x && y != other.y ) { //north south
                result = y > other.y ? Direction.north : Direction.south;
            }
            else if ( y == other.y && x != other.x ) { //east west
                result = x > other.x ? Direction.west : Direction.east;
            }
            return result;
        }

        int manhattanDistance( Coordinates other ) {
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

    static class Interval
        implements Comparable<Interval> {
        int lower;

        int upper;

        Interval( String lower, String upper ) {
            this( Integer.parseInt( lower ), Integer.parseInt( upper ) );
        }

        Interval( int lower, int upper ) {
            this.lower = lower;
            this.upper = upper;
        }

        boolean contains( Interval bar ) {
            return this.lower <= bar.lower && this.upper >= bar.upper;
        }

        boolean overlap( Interval bar ) {
            return this.lower <= bar.lower && bar.lower <= this.upper;
        }

        Interval union( Interval bar ) {
            return new Interval( Math.min( this.lower, bar.lower ), Math.max( bar.upper, this.upper ) );
        }

        int length() {
            return Math.abs( upper - lower );
        }

        @Override
        public int compareTo( Interval other ) {
            int lowerCompare = Integer.compare( lower, other.lower );
            return lowerCompare == 0 ? Integer.compare( upper, other.upper ) : lowerCompare;
        }

        @Override
        public String toString() {
            return "Interval{" + "lower=" + lower + ", upper=" + upper + '}';
        }

    }

    static class Matrix {
        //y = cols , -1 = more west, +1 more east
        //x = rows , -1 = more north, +1 more south
        int[][] matrix; //int rows | cols

        int xOffset;

        int yOffset;

        Map<Integer, String> printMap = Map.of( 0, ".", 1, "#", 2, "o", 3, "S", 4, "B" );

        Matrix( int[][] initialized ) {
            this( initialized, 0, 0 );
        }

        Matrix( int[][] initialized, int xOffset, int yOffset ) {
            this.matrix = initialized;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        Matrix( int xDim, int yDim, int xOffset, int yOffset ) {
            this( new int[xDim][yDim], xOffset, yOffset );
        }

        void setValue( Coordinates coordinates ) {
            int x = coordinates.x - xOffset;
            int y = coordinates.y - yOffset;
            matrix[x][y] = coordinates.value;
        }

        int getValue( Coordinates coordinates ) {
            int y = coordinates.y - yOffset;
            int x = coordinates.x - xOffset;
            return matrix[x][y];
        }

        void setAllValuesRow( int y, int value ) {
            for ( int i = 0; i < matrix.length; i++ ) {
                matrix[i][y] = value;
            }
        }

        boolean isEmpty( Coordinates coordinates ) {
            return getValue( coordinates ) == 0;
        }

        boolean isInTheMatrix( Coordinates coordinates ) {
            try {
                getValue( coordinates );
                return true;
            }
            catch ( ArrayIndexOutOfBoundsException e ) {
                return false;
            }
        }

        Coordinates createCoords( int x, int y ) {
            return new Coordinates( x + xOffset, y + yOffset );
        }

        int getXLength() {
            return matrix.length;
        }

        int getYLength() {
            return matrix[0].length;
        }

        List<Coordinates> findValues( int value, boolean firstOnly ) {
            List<Coordinates> positions = new ArrayList<>();
            for ( int x = 0; x < getXLength(); x++ ) {
                for ( int y = 0; y < getYLength(); y++ ) {
                    Coordinates coordinates = createCoords( x, y );
                    coordinates.value = value;
                    if ( getValue( coordinates ) == value ) {
                        positions.add( coordinates );
                        if ( firstOnly ) {
                            break;
                        }
                    }
                }
            }
            return positions;
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for ( int y = 0; y < getYLength(); y++ ) {
                for ( int x = 0; x < getXLength(); x++ ) {
                    builder.append( printMap.get( getValue( createCoords( x, y ) ) ) );
                }
                builder.append( "\n" );
            }
            return builder.toString();
        }
    }

    enum Direction {
        north,
        south,
        east,
        west,
        southeast,
        southwest,
        northeast,
        northwest
    }

    interface PathNode<T> {
        int getValue();

        int getId();
        List<T> getChildren();
    }

    static class Path<T extends PathNode<T>> {
        Path<T> parent;
        T node;
        int totalCost;
        int heuristic;
        int totalHeuristic;

        Path(Path<T> parent, T node, int cost, int heuristic){
            this.parent = parent;
            this.node = node;
            this.totalCost = (parent==null ? 0 : parent.totalCost ) + cost;
            this.heuristic = heuristic;
            this.totalHeuristic = (parent==null ? 0 : parent.totalHeuristic) + this.heuristic;
        }

        boolean contains(PathNode<T> node){
            return this.node.equals( node ) || ( parent != null && parent.contains( node ) );
        }
        int getF(){ //f = g + h, hier ist die heuristic aber invertiert
            return totalCost - totalHeuristic;
        }

        int length(){ //minus parent
            return parent==null? 1 : parent.length()+1;
        }

        public int getTotalHeuristic() {
            return totalHeuristic;
        }

        Set<Integer> getNodeIdsWithoutStart(){
            Set<Integer> result = parent == null ? new HashSet<>() : parent.getNodeIdsWithoutStart();
            if(parent!=null) {
                result.add( node.getId() );
            }
            return result;
        }

        @Override
        public String toString() {
            return (parent==null ? "" : parent +", ") +totalCost+": "+ node+ " h="+heuristic;
        }

        List<Path<T>> search(Function<Path<T>,List<Path<T>>> childPaths, Function<Path<T>,Boolean> finished){
            Map<T, Path<T>> openList = new HashMap<>();
            Map<T, Path<T>> closedList = new HashMap<>();

            openList.put( this.node, this );

            List<Path<T>> candidates = new ArrayList<>();
            while ( !openList.isEmpty() ) {

                Path<T> path = openList.values().stream().min( Comparator.comparing( Path::getF ) ).get();
                openList.remove( path.node );

                List<Path<T>> children = childPaths.apply( path );

                children.forEach( child -> {
                    if ( finished.apply( child ) ) {
                        candidates.add( child );
                        return;
                    }
                    if ( ( !openList.containsKey( child.node ) || openList.get( child.node ).getF() > child.getF() ) &&
                        ( !closedList.containsKey( child.node ) || closedList.get( child.node ).getF() > child.getF() ) ) {
                        openList.put( child.node, child );
                    }
                } );
                closedList.put( path.node, path );
            }
            return candidates;
        }

    }





    //---- end helper classes ----

    static class Day1
        extends Day<Integer> {

        Day1() {
            super( 1, "Calorie Counting" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            return solve( input, 10000 );
        }

        @Override
        Integer part2( Stream<String> input ) {
            return solve( input, 3 );
        }

        int solve( Stream<String> input, int numberOfElves ) {
            AtomicInteger current = new AtomicInteger( 0 );
            return input.map( line -> line.isEmpty() ? "skip" + current.incrementAndGet() : current.get() + "-" + line )//
                .filter( l -> l.contains( "-" ) ) //
                .collect( Collectors.toMap( l -> l.split( "-" )[0], l -> Integer.parseInt( l.split( "-" )[1] ), Integer::sum ) )//
                .values().stream().sorted( Comparator.reverseOrder() ) //
                .limit( numberOfElves ) //
                .reduce( 0, Integer::sum );
        }
    }


    static class Day2
        extends Day<Integer> {
        Day2() {
            super( 2, "Rock Paper Scissors" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            return input.map( line -> {
                int opponent = ( line.charAt( 0 ) + 1 ) % 3 + 1;
                int me = ( line.charAt( 2 ) - 1 ) % 3 + 1;
                return me + ( me - opponent == 0 ? 3 : ( List.of( 1, -2 ).contains( me - opponent ) ? 6 : 0 ) );
            } ).reduce( 0, Integer::sum );
        }

        @Override
        Integer part2( Stream<String> input ) {
            return input.map( line -> List.of( "fnord", "B X", "C X", "A X", "A Y", "B Y", "C Y", "C Z", "A Z", "B Z" ) //
                .indexOf( line ) ).reduce( 0, Integer::sum );
        }
    }

    static class Day3 extends Day<Integer> {
        Day3() {
            super( 3, "Rucksack Reorganization" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            return input.map( backpack -> backpack.substring( backpack.length() / 2 ).chars().distinct()//
                    .filter( type -> backpack.substring( 0, backpack.length() / 2 ).chars() //
                    .filter( c -> c == type ).findFirst().isPresent() ) //
                    .map( this::priority ) //ascii offset
                    .sum() ) //
                    .reduce( 0, Integer::sum );
        }

        @Override
        Integer part2( Stream<String> input ) {
            Stack<List<String>> elfGroups = new Stack<>();
            elfGroups.push( new ArrayList<>() );
            input.forEach( elf -> {
                if ( elfGroups.isEmpty() || elfGroups.peek().size() == 3 ) {
                    elfGroups.push( new ArrayList<>() );
                }
                elfGroups.peek().add( elf );
            } );
            return elfGroups.stream().map(
                group -> group.get( 0 ).chars().filter( type -> isInPack( group.get( 1 ), type ) && isInPack( group.get( 2 ), type ) ).map(
                    this::priority ).findFirst().orElseGet( null ) ).reduce( 0, Integer::sum );


        }

        int priority( int type ) {
            return type < 97 ? type - 64 + 26 : type - 96;
        }
        boolean isInPack( String backpack, int type ) {
            return backpack.chars().filter( type1 -> type1 == type ).findFirst().isPresent();
        }
    }

    static class Day4 extends Day<Long>{

        static Pattern day4Pattern = Pattern.compile( "([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)" );
        Day4(){
            super(4,"Camp Cleanup");
        }

        @Override
        Long part1( Stream<String> input ) {
            return input.map( this::parse ).filter( Objects::nonNull ).filter(
                pair -> pair.foo.overlap( pair.bar ) || pair.foo.contains( pair.bar ) ).count();
        }

        @Override
        Long part2( Stream<String> input ) {
            return input.map( this::parse ).filter( Objects::nonNull ).filter(
                pair -> pair.foo.contains( pair.bar ) || pair.bar.contains( pair.foo ) ).count();
        }

        record Pair(Interval foo, Interval bar) {}
        Pair parse( String input ) {
            final Matcher matcher = day4Pattern.matcher( input );
            return matcher.matches() ? new Pair( new Interval( matcher.group( 1 ), matcher.group( 2 ) ),
                                                 new Interval( matcher.group( 3 ), matcher.group( 4 ) ) ) : null;
        }

    }

    static class Day5 extends Day<String> {
        final static Pattern day5MovePattern = Pattern.compile( "move ([0-9]+) from ([0-9]+) to ([0-9]+)" );

        final static Pattern day5StackPattern = Pattern.compile( ".(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)." );

        record Move(int number, int from, int to) {
        }
        Day5(){ super(5,"Supply Stacks"); }


        @Override
        String part1( Stream<String> input ) {
            Map<Integer, LinkedList<String>> boxes = new HashMap<>();
            input.forEach( line -> {
                final Matcher stackMatcher = day5StackPattern.matcher( line );
                if ( stackMatcher.matches() ) {
                    for ( int group = 1; group <= 9; group++ ) {
                        String match = stackMatcher.group( group ).trim();
                        if ( !match.isEmpty() && match.matches( "[A-Z]" ) ) {
                            boxes.putIfAbsent( group, new LinkedList<>() );
                            boxes.get( group ).add( match );
                        }
                    }
                }
                final Matcher moveMatcher = day5MovePattern.matcher( line );
                if ( moveMatcher.matches() ) {
                    Move move = new Move( Integer.parseInt( moveMatcher.group( 1 ) ), Integer.parseInt( moveMatcher.group( 2 ) ),
                                          Integer.parseInt( moveMatcher.group( 3 ) ) );
                    LinkedList<String> lifter = new LinkedList<>();
                    for ( int n = 0; n < move.number; n++ ) {
                        lifter.push( boxes.get( move.from ).pop() );
                    }
                    lifter.forEach( box -> boxes.get( move.to ).push( box ) );
                }
            } );
            return boxes.keySet().stream().sorted().map( key -> boxes.get( key ).pop() ).reduce( "", ( a, b ) -> a + b );
        }

        @Override
        String part2( Stream<String> input ) {
            //TODO (solved it but neglegcted to write code for both separately
            return null;
        }
    }





    static class Day6 extends Day<Integer> {

        Day6() {
            super( 6, "uning Trouble" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            String line = input.reduce( "", ( a, b ) -> a + b );
            int magicNumber = 14;
            for ( int idx = 0; idx < line.length() - magicNumber; idx++ ) {
                if ( line.substring( idx, idx + magicNumber ).chars().distinct().count() == magicNumber ) {
                    return idx + magicNumber;
                }
            }
            return 0;
        }

        @Override
        Integer part2( Stream<String> input ) {
            //TODO part 2 is probably up there
            return null;
        }
    }


    //-------------- DAY 7: Directories -------------------
    static class Day7 extends Day<Integer> {

        Day7() {
            super( 7, "No Space Left On Device" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            Directory root = new Directory( "/", null );
            List<Directory> directories = parseStructure( input, root );
            return directories.stream().map( Directory::size ).filter( s -> s < 100000 ).reduce( 0, Integer::sum );
        }

        @Override
        Integer part2( Stream<String> input ) {
            Directory root = new Directory( "/", null );
            List<Directory> directories = parseStructure( input, root );
            int needed = 30000000;
            int space = 70000000 - root.size();
            return directories.stream().map( Directory::size ).filter( size -> space + size > needed ).min( Integer::compareTo ).get();
        }

        List<Directory> parseStructure( Stream<String> input, Directory root ) {
            final List<Directory> directories = new ArrayList<>();
            final AtomicReference<Directory> current = new AtomicReference<>( root );
            input.skip( 2 ).forEach( line -> {
                if ( line.startsWith( "$" ) ) {
                    if ( line.equals( "$ cd .." ) ) {
                        current.set( current.get().parent );
                    }
                    else if ( line.startsWith( "$ cd" ) ) {
                        current.set( current.get().directories.get( line.substring( 5 ) ) );
                    }
                }
                else if ( line.startsWith( "dir" ) ) {
                    directories.add( current.get().addDirectory( line ) );
                }
                else {
                    current.get().addFile( line );
                }
            } );
            return directories;
        }

        static class Directory { //so far only used by Day7
            Map<String, Directory> directories = new HashMap<>();

            Map<String, Integer> files = new HashMap<>();

            Directory parent;

            String name;

            Directory( String name, Directory parent ) {
                this.name = name;
                this.parent = parent;
            }

            void addFile( String line ) {
                String[] pair = line.split( " " );
                files.put( pair[1], Integer.parseInt( pair[0] ) );
            }

            Directory addDirectory( String line ) {
                Directory directory = new Directory( line.split( " " )[1], this );
                directories.put( directory.name, directory );
                return directory;
            }

            int size() {
                return files.values().stream().reduce( 0, Integer::sum ) + directories.values().stream().map( Directory::size ).reduce( 0, Integer::sum );
            }
        }

    }


    //-------------- DAY 8: Scenic Treepatch -------------------

    static class Day8 extends Day<Integer> {

        Day8() {
            super( 8, "Treetop Tree House" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            Set<Coordinates> visibleTrees = new HashSet<>();

            Matrix treePatch = new Matrix( input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new ) );

            for ( int row = 0; row < treePatch.matrix.length; row++ ) {
                int westSize = -1, eastSize = -1, northSize = -1, southSize = -1;
                int rowLength = treePatch.matrix[row].length;
                for ( int col = 0; col < rowLength; col++ ) {
                    westSize = getMaxSize( visibleTrees, treePatch, row, col, westSize );
                    eastSize = getMaxSize( visibleTrees, treePatch, row, rowLength - col - 1, eastSize );
                    northSize = getMaxSize( visibleTrees, treePatch, col, row, northSize );
                    southSize = getMaxSize( visibleTrees, treePatch, rowLength - col - 1, row, southSize );
                }
            }
            return visibleTrees.size();
        }

        @Override
        Integer part2( Stream<String> input ) {
            List<Integer> scenery = new ArrayList<>();
            Matrix treePatch = new Matrix( input.map( line -> line.chars().map( c -> c - 48 ).toArray() ).toArray( int[][]::new ) );

            for ( int row = 1; row < treePatch.matrix.length - 1; row++ ) {
                int rowLength = treePatch.matrix[row].length;
                for ( int col = 1; col < rowLength - 1; col++ ) {
                    scenery.add( scenicScore( treePatch, row, col ) );
                }
            }
            return scenery.stream().max( Integer::compareTo ).get();
        }

        private int getMaxSize( Set<Coordinates> visibleTrees, Matrix treePatch, int row, int col, int maxSizeForRow ) {
            Coordinates tree = new Coordinates( row, col );
            tree.value = treePatch.getValue( tree );
            if ( tree.value > maxSizeForRow ) {
                maxSizeForRow = tree.value;
                visibleTrees.add( tree );
            }
            return maxSizeForRow;
        }

        private Integer scenicScore( Matrix treePatch, int row, int col ) {
            int treeSize = treePatch.getValue( new Coordinates( row, col ) );
            boolean west = true, east = true, south = true, north = true;
            int score = 1;
            int radius = 1;
            while ( west || east || south || north && score > 0 ) {

                if ( west && lastTree( treePatch, treeSize, row, col - radius, false ) ) {
                    west = false;
                    score = score * radius;
                }
                if ( east && lastTree( treePatch, treeSize, row, col + radius, false ) ) {
                    east = false;
                    score = score * radius;
                }
                if ( south && lastTree( treePatch, treeSize, row + radius, col, true ) ) {
                    south = false;
                    score = score * radius;
                }
                if ( north && lastTree( treePatch, treeSize, row - radius, col, true ) ) {
                    north = false;
                    score = score * radius;
                }
                radius++;
            }
            return score;
        }

        private boolean lastTree( Matrix treePatch, int treeSize, int row, int col, boolean checkRow ) {
            return ( checkRow ? ( row == 0 || row == treePatch.matrix.length - 1 ) : ( col == 0 || col == treePatch.matrix[row].length - 1 ) ) ||
                treePatch.matrix[row][col] >= treeSize;
        }
    }


    static class Day9 extends Day<Integer> {

        Day9() {
            super( 9, "Rope Bridge" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            Set<Coordinates> positionsVisited = new HashSet<>();
            RowEnd head = new RowEnd( "head" );
            List<RowEnd> knots = IntStream.range( 1, 10 ).mapToObj( idx -> new RowEnd( "t" + idx ) ).toList();
            input.forEach( line -> {
                head.receiveCommand( line );
                while ( head.processCommand() ) {
                    RowEnd current = head;
                    for ( int idx = 0; idx < knots.size(); idx++ ) {
                        RowEnd knot = knots.get( idx );
                        Coordinates position = knot.follow( current );
                        current = knot;
                        if ( idx + 1 == knots.size() ) {
                            positionsVisited.add( position );
                        }
                    }
                }
            } );
            return positionsVisited.size();
        }

        @Override
        Integer part2( Stream<String> input ) {
            //TODO solved both but...
            return null;
        }


        static class RowEnd { //only used by Day9
            String name;

            int row = 0;

            int col = 0;

            int steps = 0;

            String direction;

            RowEnd( String name ) {
                this.name = name;
            }

            void receiveCommand( String line ) {
                String[] pair = line.split( " " );
                direction = pair[0];
                steps = Integer.parseInt( pair[1] );
            }

            boolean processCommand() {
                if ( steps == 0 ) {
                    return false;
                }
                switch ( direction ) {
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

            Coordinates follow( RowEnd head ) {
                if ( Math.abs( this.row - head.row ) >= 2 || Math.abs( this.col - head.col ) >= 2 ) {
                    col = col + Integer.compare( head.col, this.col );
                    row = row + Integer.compare( head.row, this.row );
                }
                return new Coordinates( row, col );
            }
        }
    }

    //-------------- DAY 10: CRT Display -------------------
    static class Day10 extends Day<Integer> {
        static int WIDTH = 40;
        AtomicInteger clock = new AtomicInteger( 0 );
        AtomicInteger register = new AtomicInteger( 1 );
        List<String> crt = new ArrayList<>();

        Day10() {
            super( 10, "Cathode-Ray Tube" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            AtomicInteger frequency = new AtomicInteger( 0 );
            input.forEach( line -> {
                frequency.addAndGet( computeCycle( 0 ) );
                if ( line.startsWith( "addx" ) ) {
                    frequency.addAndGet( computeCycle( Integer.parseInt( line.substring( 5 ) ) ) );
                }
            } );
            crt.forEach( System.out::print );
            return frequency.get();
        }

        @Override
        Integer part2( Stream<String> input ) {
            //TODO solved but...
            return null;
        }

        int computeCycle( int delta ) {
            int cycle = clock.incrementAndGet();
            int X = register.addAndGet( delta );
            crt.add( ( Math.abs( X - ( cycle % WIDTH ) ) <= 1 ? "#" : "." ) + ( ( cycle ) % WIDTH == 0 ? "\n" : "" ) );
            return List.of( 20, 60, 100, 140, 180, 220 ).contains( cycle ) ? cycle * X : 0;
        }
    }

    //-------------- DAY 11: Monkey Business -------------------
    static class Day11 extends Day<Long> {

        Day11() {
            super( 11, "Monkey in the Middle" );
        }

        @Override
        Long part1( Stream<String> input ) {
            List<String> lines = input.map( String::trim ).filter( line -> !line.isEmpty() ).toList();
            List<Monkey> monkeys = new ArrayList<>();
            int monkeyDef = 6;
            for ( int idx = 0; idx < 8; idx++ ) {
                int start = idx * monkeyDef;
                Monkey monkey = Monkey.parseFromStrings( lines.subList( start, start + monkeyDef ), monkeys );
                monkeys.add( monkey );
                System.out.println( "Init: " + monkey );
            }
            for ( int round = 0; round < 10000; round++ ) {
                monkeys.forEach( Monkey::inspect );
            }
            return monkeys.stream()//
                .peek( System.out::println )//
                .map( Monkey::getBusiness )//
                .sorted( Comparator.reverseOrder() )//
                .limit( 2 )//
                .reduce( 1L, ( a, b ) -> a * b );
        }

        @Override
        Long part2( Stream<String> input ) {
            //TODO solved it but
            return null;
        }


        static class Monkey {
            BigInteger MAGIC = BigInteger.valueOf( 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 );

            BigInteger lowerAnxiety( BigInteger currentAnxiety ) {
                return currentAnxiety.mod( MAGIC );
            }

            Stack<BigInteger> items = new Stack<>();

            Function<BigInteger, BigInteger> monkeyFingers;

            BigInteger monkeyTest;

            int trueMonkey;

            int falseMonkey;

            long business = 0;

            List<Monkey> monkeys;

            public Long getBusiness() {
                return business;
            }

            void inspect() {
                while ( !items.isEmpty() ) {
                    BigInteger newItemAnxietyLevel = lowerAnxiety( monkeyFingers.apply( items.pop() ) );
                    if ( newItemAnxietyLevel.mod( monkeyTest ).equals( BigInteger.ZERO ) ) {
                        monkeys.get( trueMonkey ).items.push( newItemAnxietyLevel );
                    }
                    else {
                        monkeys.get( falseMonkey ).items.push( newItemAnxietyLevel );
                    }
                    business++;
                }
            }

            public String toString() {
                return String.format( "Monkey[items=%s,div=%s, true=%s,false=%s, busy=%s]", items, monkeyTest, trueMonkey, falseMonkey, business );
            }

            static Monkey parseFromStrings( List<String> monkeyString, List<Monkey> monkeys ) {
                Monkey monkey = new Monkey();
                Matcher itemMatcher = Pattern.compile( "(\\d+)" ).matcher( monkeyString.get( 1 ) );
                while ( itemMatcher.find() ) {
                    monkey.items.push( BigInteger.valueOf( Long.parseLong( itemMatcher.group( 1 ) ) ) );
                }
                Matcher operationMatcher = Pattern.compile( "Operation: new = old ([\\*\\+]) (\\d+|old)" ).matcher( monkeyString.get( 2 ) );
                if ( operationMatcher.matches() ) {
                    boolean sum = operationMatcher.group( 1 ).equals( "+" );
                    String operand = operationMatcher.group( 2 );
                    if ( !operand.equals( "old" ) ) {
                        BigInteger op = BigInteger.valueOf( Long.parseLong( operand ) );
                        monkey.monkeyFingers = item -> sum ? item.add( op ) : item.multiply( op );
                    }
                    else {
                        monkey.monkeyFingers = item -> item.multiply( item );
                    }
                }
                Matcher testMatcher = Pattern.compile( "Test: divisible by (\\d+)" ).matcher( monkeyString.get( 3 ) );
                if ( testMatcher.matches() ) {
                    monkey.monkeyTest = BigInteger.valueOf( Long.parseLong( testMatcher.group( 1 ) ) );
                }
                Pattern pattern = Pattern.compile( "If (true|false): throw to monkey (\\d+)" );
                Matcher trueMonkeyMatcher = pattern.matcher( monkeyString.get( 4 ) );
                Matcher falseMonkeyMatcher = pattern.matcher( monkeyString.get( 5 ) );
                if ( trueMonkeyMatcher.matches() ) {
                    monkey.trueMonkey = Integer.parseInt( trueMonkeyMatcher.group( 2 ) );
                }
                if ( falseMonkeyMatcher.matches() ) {
                    monkey.falseMonkey = Integer.parseInt( falseMonkeyMatcher.group( 2 ) );
                }
                monkey.monkeys = monkeys;
                return monkey;
            }
        }
    }

    //-------------- DAY 12 paths into the hills -------------------

    static class Day12 extends Day<Integer> {

        Day12() {
            super( 12, "Hill Climbing Algorithm" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            return null;
        }

        @Override
        Integer part2( Stream<String> input ) {
            HILL = new Matrix( input.map( line -> line.chars().map( c -> c - 96 ).toArray() ).toArray( int[][]::new ) );
            GOALS.addAll( HILL.findValues( ( (int) 'a' ) - 96, false ) );
            Coordinates startPos = HILL.findValues( ( (int) 'E' ) - 96, true ).get( 0 );

            Map<Coordinates, Path> openList = new HashMap<>();
            Map<Coordinates, Path> closedList = new HashMap<>();

            Path start = new Path( startPos.x, startPos.y, null, "o" );
            openList.put( start.pos, start );

            List<Path> candidates = new ArrayList<>();
            while ( !openList.isEmpty() ) {

                Path path = openList.values().stream().min( Comparator.comparing( Path::getFnord ) ).get();
                openList.remove( path.pos );

                List<Path> children = Stream.of( Direction.north, Direction.south, Direction.east, Direction.west )//
                    .map( path::move )//
                    .filter( Objects::nonNull )//
                    .filter( Path::isLegal ) //
                    .toList();

                children.forEach( child -> {
                    if ( hasReachedGoal( child, GOALS ) ) {
                        candidates.add( child );
                        return;
                    }
                    if ( ( !openList.containsKey( child.pos ) || openList.get( child.pos ).fnord > child.fnord ) &&
                        ( !closedList.containsKey( child.pos ) || closedList.get( child.pos ).fnord > child.fnord ) ) {
                        openList.put( child.pos, child );
                    }
                } );
                closedList.put( path.pos, path );
            }

            return candidates.stream().map( path -> path.steps ).min( Integer::compareTo ).get();
        }

        static Matrix HILL;

        static List<Coordinates> GOALS = new ArrayList<>();


        static boolean hasReachedGoal( Path path, List<Coordinates> goals ) {
            return goals.contains( path.pos );
        }

        static class Path {
            Path parent;

            Coordinates pos;

            String move;

            int height;

            int cost;

            int fnord;

            int steps;


            Path( int x, int y, Path parent, String move ) {
                this.pos = new Coordinates( x, y );
                this.move = move;
                this.parent = parent;
                this.height = parent == null ? 0 : HILL.matrix[x][y];
                this.cost = 1 + ( parent == null ? 0 : parent.cost );
                this.fnord = cost + lineOfSight();
                this.steps = parent == null ? 0 : parent.steps + 1;
            }

            int lineOfSight() {
                return GOALS.stream().map( g -> Math.abs( pos.x - g.x ) + Math.abs( pos.y - g.y ) ).min( Integer::compareTo ).get();
            }

            boolean isLegal() {
                return parent.height - height <= 1;
            }

            public String toString() {
                return parent + " -> " + cost + "[" + pos.x + ":" + pos.y + "]";
            }

            public int getFnord() {
                return fnord;
            }

            Path move( Direction move ) {
                try {
                    return switch ( move ) {
                        case west -> new Path( this.pos.x, this.pos.y - 1, this, "<" );
                        case east -> new Path( this.pos.x, this.pos.y + 1, this, ">" );
                        case north -> new Path( this.pos.x - 1, this.pos.y, this, "^" );
                        case south -> new Path( this.pos.x + 1, this.pos.y, this, "v" );
                        default -> null;
                    };
                }
                catch ( ArrayIndexOutOfBoundsException e ) {
                    return null;
                }
            }

            String prettyPrint() {
                Map<Coordinates, Path> positions = new HashMap<>();
                Path current = this;
                while ( current != null ) {
                    positions.put( current.pos, current );
                    current = current.parent;
                }
                StringBuilder builder = new StringBuilder();
                for ( int x = 0; x < HILL.matrix.length; x++ ) {
                    for ( int y = 0; y < HILL.matrix[0].length; y++ ) {
                        Coordinates currentPos = new Coordinates( x, y );
                        if ( GOALS.contains( currentPos ) ) {
                            builder.append( "X" );
                        }
                        else if ( positions.containsKey( currentPos ) ) {
                            builder.append( positions.get( currentPos ).move );
                        }
                        else {
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

    }

    static class Day13 extends Day<Integer> {

        Day13() {
            super( 13, "Distress Signal" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            List<String> lines = input.filter( Predicate.not( String::isBlank ) ).toList();
            List<Packet> packets = new ArrayList<>();
            for ( int idx = 0; idx < lines.size(); idx = idx + 2 ) {
                packets.add( new Packet( lines.get( idx ), lines.get( idx + 1 ) ) );
            }
            return packets.stream().filter( Packet::isOrdered ).map( Packet::getPosition ).reduce( 0, Integer::sum );
        }

        @Override
        Integer part2( Stream<String> input ) {
            PacketContent dividerA = new PacketContent( "[[2]]" );
            PacketContent dividerB = new PacketContent( "[[6]]" );
            List<PacketContent> packets = new ArrayList<>( input.filter( Predicate.not( String::isBlank ) ).map( PacketContent::new ).toList() );
            packets.add( dividerB );
            packets.add( dividerA );
            List<PacketContent> packetContents = packets.stream().sorted().peek( System.out::println ).toList();
            return ( packetContents.indexOf( dividerA ) + 1 ) * ( packetContents.indexOf( dividerB ) + 1 );
        }


        static AtomicInteger PACKET_ID = new AtomicInteger( 1 );

        static class Packet {
            PacketContent left;

            PacketContent right;

            int position = PACKET_ID.getAndIncrement();

            Packet( String left, String right ) {
                this.left = new PacketContent( left );
                this.right = new PacketContent( right );
            }

            boolean isOrdered() {
                return left.compareTo( right ) <= 0;
            }

            public int getPosition() {
                return position;
            }

            @Override
            public String toString() {
                return position + ":" + left.toString() + " | " + right.toString();
            }
        }

        static class PacketContent
            implements Comparable<PacketContent> {
            Integer value = null;

            List<PacketContent> contents = new ArrayList<>();

            PacketContent( String packetString ) {
                Matcher packetMatcher = Pattern.compile( "\\[(.*)\\]" ).matcher( packetString );
                Matcher numberMatcher = Pattern.compile( "(\\d+)" ).matcher( packetString );
                if ( packetString.equals( "[]" ) ) {
                    //do nothing
                }
                else if ( packetMatcher.matches() ) {
                    String[] children = packetMatcher.group( 1 ).split( "," );
                    int depth = 0;
                    StringBuilder childPacketString = new StringBuilder();
                    for ( String child : children ) {
                        depth = depth + child.chars().map( c -> c == '[' ? 1 : ( c == ']' ? -1 : 0 ) ).reduce( 0, Integer::sum );
                        childPacketString.append( ( childPacketString.length() == 0 ) ? "" : "," ).append( child );
                        if ( depth == 0 ) {
                            contents.add( new PacketContent( childPacketString.toString() ) );
                            childPacketString = new StringBuilder();
                        }
                    }
                }
                else if ( numberMatcher.find() ) {
                    value = Integer.parseInt( numberMatcher.group( 1 ) );
                }
            }

            @Override
            public int compareTo( PacketContent o ) {
                int result = 0;
                if ( o == null ) {
                    throw new IllegalStateException( "Oops" );
                }
                else if ( value != null && o.value != null ) {
                    result = value.compareTo( o.value );
                }
                else if ( value == null && o.value == null ) {
                    result = this.compareContents( o.contents );
                }
                else if ( value == null ) {
                    result = this.compareContents( List.of( o ) );
                }
                else {
                    result = -o.compareContents( List.of( this ) );
                }
                return result;
            }

            private int compareContents( List<PacketContent> otherContents ) {
                LinkedList<PacketContent> otherStack = new LinkedList<>( otherContents );
                LinkedList<PacketContent> stack = new LinkedList<>( this.contents );
                while ( !otherStack.isEmpty() && !stack.isEmpty() ) {
                    PacketContent content = stack.pop();
                    PacketContent otherContent = otherStack.pop();
                    int result = content.compareTo( otherContent );
                    if ( result != 0 ) {
                        return result;
                    }
                }
                return Integer.compare( stack.size(), otherStack.size() );
            }

            @Override
            public String toString() {
                return value == null ? contents.toString() : value.toString();
            }
        }

    }


    static class Day14 extends Day<Integer> {
        static final int SOURCE = 3;
        static final int SAND = 2;
        static final int WALL = 1;
        static final int WIDEN = 10000; //several attempts
        Day14() {
            super( 14, "Regolith Reservoir" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            List<List<Coordinates>> walls = new ArrayList<>(
                input.map( line -> Arrays.stream( line.split( "->" ) ).map( coord -> new Coordinates( coord, ",", WALL ) ).toList() ).toList() );
            walls.add( List.of( new Coordinates( 500, 0, SOURCE ) ) );
            int xMin = walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).min( Integer::compareTo ).get();
            int xMax = walls.stream().flatMap( Collection::stream ).map( Coordinates::getX ).max( Integer::compareTo ).get();
            int yMin = walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).min( Integer::compareTo ).get();
            int yMax = walls.stream().flatMap( Collection::stream ).map( Coordinates::getY ).max( Integer::compareTo ).get() + 2;
            System.out.println( walls );
            Matrix cave = new Matrix( ( xMax - xMin ) + WIDEN, yMax - yMin + 1, xMin - ( WIDEN / 2 ), yMin );
            cave.setAllValuesRow( yMax, WALL ); //add floor

            for ( List<Coordinates> wall : walls ) {
                Coordinates previous = null; //each wall is separate
                for ( Coordinates next : wall ) {
                    cave.setValue( next );
                    if ( previous != null ) {
                        Direction dir = previous.lookingTowards( next );
                        while ( !next.equals( previous ) ) {
                            previous = previous.moveTo( dir, WALL );
                            cave.setValue( previous );
                        }
                    }
                    previous = next;
                }
            }
            while ( true ) {
                try {
                    //begin a new grain
                    Coordinates last = null;
                    Coordinates currentGrain = move( new Coordinates( 500, 0, SOURCE ), cave );
                    while ( currentGrain != null ) {
                        last = currentGrain;
                        currentGrain = move( currentGrain, cave );
                    }
                    if ( last == null ) {
                        break;
                    }
                    cave.setValue( last );
                }
                catch ( ArrayIndexOutOfBoundsException e ) { //cheating but who cares
                    break;
                }
            }
            return cave.findValues( SAND, false ).size() + 1;
        }

        @Override
        Integer part2( Stream<String> input ) {
            return null;
        }

        Coordinates move( Coordinates previous, Matrix cave ) {
            return Stream.of( Direction.south, Direction.southwest, Direction.southeast ).map( d -> previous.moveTo( d, SAND ) ).filter(
                cave::isEmpty ).findFirst().orElse( null );
        }
    }



    static class Day15 extends Day<Integer> {
        record SensorBeacon(Coordinates sensor, Coordinates beacon) {
        }

        static int MAX_DIM = 4000000;
        Day15() {
            super( 15, "Beacon Exclusion Zone" );
        }

        @Override
        Integer part1( Stream<String> input ) {
            long time = System.currentTimeMillis();
            int size = collectCoveredIntervals( parse( input ), 2000000, true ).stream().map( Interval::length ).reduce( 0, Integer::sum );
            System.out.println( "This took : " + ( System.currentTimeMillis() - time ) + "ms" );
            return size;
        }

        @Override
        Integer part2( Stream<String> input ) {
            long time = System.currentTimeMillis();
            List<SensorBeacon> data = parse( input );
            Coordinates distressBeacon = null;
            for ( int y = 0; y <= MAX_DIM; y++ ) {
                List<Interval> coveredCoordinates = collectCoveredIntervals( data, y, false );
                if ( coveredCoordinates.size() > 1 ) {
                    distressBeacon = new Coordinates( coveredCoordinates.get( 0 ).upper + 1, y );
                    break;
                }
            }
            return BigInteger.valueOf( distressBeacon.x ).multiply( BigInteger.valueOf( 4000000 ) ).add( BigInteger.valueOf( distressBeacon.y ) ).intValue();
        }

        List<SensorBeacon> parse( Stream<String> input ) {
            Pattern compile = Pattern.compile( "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)" );

            List<SensorBeacon> data = input.map( line -> {
                Matcher m = compile.matcher( line );
                return m.matches() ? new SensorBeacon( new Coordinates( new String[]{m.group( 1 ), m.group( 2 )}, 3 ),
                                                       new Coordinates( new String[]{m.group( 3 ), m.group( 4 )}, 4 ) ) : null;
            } ).filter( Objects::nonNull ).toList();
            return data;
        }

        List<Interval> collectCoveredIntervals( List<SensorBeacon> sensorBeacons, int y, boolean noMaxDim ) {
            LinkedList<Interval> intervals = new LinkedList<>( sensorBeacons.stream()//
                                                                   .map( sb -> findCoveredInterval( sb.sensor, sb.beacon, y, noMaxDim ) )//
                                                                   .filter( Objects::nonNull ).sorted().toList() );
            List<Interval> result = new ArrayList<>();
            if ( intervals.isEmpty() ) {
                return result;
            }
            Interval current = intervals.pop();
            while ( !intervals.isEmpty() ) {
                Interval next = intervals.pop();
                if ( current.overlap( next ) ) {
                    current = current.union( next );
                }
                else {
                    result.add( current );
                    current = next;
                }
            }
            result.add( current );
            return result;
        }

        Interval findCoveredInterval( Coordinates sensor, Coordinates beacon, int yToCheck, boolean noMaxDim ) {
            int refDistance = sensor.manhattanDistance( new Coordinates( sensor.x, yToCheck ) );
            int beaconDist = sensor.manhattanDistance( beacon );
            if ( refDistance < beaconDist ) {
                int radius = beaconDist - refDistance;
                int lower = sensor.x - radius;
                int upper = sensor.x + radius;
                return new Interval( noMaxDim ? lower : Math.max( 0, lower ), noMaxDim ? upper : Math.min( MAX_DIM, upper ) );
            }
            return null;
        }
    }

    static class Day16 extends Day<Integer> {

        static String START = "AA";
        static AtomicInteger ID = new AtomicInteger(0);
        static int TIME =  30;
        static Map<String,Valve> VALVES;

        private Integer[][] pathMatrix;

        private AdventOfCode2022.Day16.Valve[] valveLine;

        private List<Valve> workingValves;

        Day16() {super(16,"Proboscidea Volcanium");}

        @Override
        Integer part1( Stream<String> input ) {
            init(input);
            List<Path<Valve>> paths= new ArrayList<>(List.of(new Path<Valve>( null, VALVES.get(START),0,0 )));
            List<Path<Valve>> results = getPaths( paths, this::getLegalChildPaths );
            return results.stream().map(Path::getTotalHeuristic).max(Integer::compareTo).orElse( 0 );
        }

        @Override
        Integer part2( Stream<String> input ) {
            //no need to init again?
            List<Path<Valve>> paths= new ArrayList<>(List.of(new Path<Valve>( null, VALVES.get(START),4,0 )));
            Map<Path<Valve>, Set<Integer>> results = getPaths( paths, this::getLegalChildPaths2 ).stream().collect(
                Collectors.toMap( Function.identity(), Path::getNodeIdsWithoutStart ) );
            Set<Integer> unions = new HashSet<>();

            results.keySet().parallelStream().forEach( path -> {
                results.keySet().parallelStream().forEach ( compare -> {
                    if ( results.get(path).stream().noneMatch(a -> results.get( compare ).contains( a ) ) ) {
                        unions.add( path.getTotalHeuristic() + compare.getTotalHeuristic() );
                    }
                });
            });
            return unions.stream().max( Integer::compareTo ).orElse( -1 );
        }


        <T> List<T> getPaths( List<T> paths ,Function<T,List<T>> getChildPaths) {
            List<T> results = new ArrayList<>();
            while(!paths.isEmpty()){
                List<T> candidates = new ArrayList<>();
                for(T path : paths ){
                    List<T> childPaths = getChildPaths.apply( path );
                    if(childPaths.isEmpty()){
                        results.add(path);
                    } else {
                        candidates.addAll(childPaths);
                    }
                }
                paths = candidates;
            }
            return results;
        }

        boolean isLegal(Path<Valve> path,Valve v){
            return path.totalCost + cost(path,v)<30 && !path.contains(v);
        }

        boolean isLegal2(Path<Valve> path,Valve v){
            return path.length()<= workingValves.size()/2 && path.totalCost + cost(path,v)<30 && !path.contains(v);
        }

        //testdata 1651
        List<Path<Valve>> getLegalChildPaths(Path<Valve> parent){
            //+1: turning on a valve is +1
            return workingValves.stream().filter(v -> isLegal( parent,v ))
                .map(valve -> {
                    int cost = cost(parent,valve);
                    return new Path<>( parent, valve, cost, ( TIME - ( parent.totalCost + cost ) ) * valve.flowRate );
                } ).toList();
        }

        List<Path<Valve>> getLegalChildPaths2(Path<Valve> parent){
            //+1: turning on a valve is +1
            return workingValves.stream().filter(v -> isLegal2( parent,v ))
                .map(valve -> {
                    int cost = cost(parent,valve);
                    return new Path<>( parent, valve, cost, ( TIME - ( parent.totalCost + cost ) ) * valve.flowRate );
                } ).toList();
        }


        int cost(Path<Valve> parent,Valve valve){
            return pathMatrix[parent.node.id][valve.id] +1;
        }

        void init(Stream<String> input){
            VALVES =input.map( Valve::new ).collect(Collectors.toMap( Valve::getName, v -> v ));
            valveLine = VALVES.values().stream().sorted( Comparator.comparing( v -> v.id )).toList().toArray( new Valve[VALVES.size()]);
            workingValves = VALVES.values().stream().filter( Valve::hasFlow ).toList();
            pathMatrix = new Integer[VALVES.size()][VALVES.size()];
            VALVES.keySet().stream().map(VALVES::get).forEach( valve -> {
                Integer[] line = pathMatrix[valve.id];
                line[valve.id]=0;
                AtomicInteger steps = new AtomicInteger(0);
                while( Arrays.stream( line ).anyMatch( Objects::isNull )){
                    steps.incrementAndGet();
                    IntStream.range( 0, line.length ).filter( i -> line[i]!=null && line[i] == steps.get()-1 ).forEach( i -> {
                        for(Valve child : valveLine[i].getChildren() ){
                            if(line[child.id]==null) {
                                line[child.id] = steps.get();
                            }
                        }                                                                            }
                    );
                }
            });
        }

        String printMatrix(Valve[] valveLine,Integer[][] matrix){
            StringBuilder builder = new StringBuilder();
            int size = valveLine.length;
            builder.append("    ");
            for(int i =0 ;i<size;i++){
               builder.append( valveLine[i].name ).append( "  " );
            }
            builder.append("\n");
            for(int i =0 ;i<size;i++){
                builder.append( valveLine[i].name ).append( "  " );
                for(int j =0;j<size;j++){
                    builder.append(" ").append( matrix[i][j] ).append( "  " );
                }
                builder.append("\n");
            }
            return builder.toString();
        }



        static class Valve implements PathNode<Valve>, Comparable<Valve>{
            static Pattern pattern = Pattern.compile( "Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.*)" );
            String name;
            int flowRate;
            Map<String,Integer> tunnelsTo;
            int id;
            Valve(String line){
                id = ID.getAndIncrement();
                Matcher matcher = pattern.matcher( line );
                if(matcher.matches()){
                    this.name = matcher.group(1);
                    this.flowRate = Integer.parseInt( matcher.group(2));
                    this.tunnelsTo = Arrays.stream( matcher.group(3).split( "," ) ).map( String::trim ).collect( Collectors.toMap(Function.identity(), v -> 1));
                } else {
                    throw new IllegalStateException("no valve: "+line);
                }
            }

            @Override
            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public int compareTo(Valve o ) {
                return Integer.compare( flowRate,o.flowRate );
            }

            @Override
            public boolean equals( Object o ) {
                if ( this == o ) {
                    return true;
                }
                if ( !( o instanceof Valve valve ) ) {
                    return false;
                }
                return getName().equals( valve.getName() );
            }

            @Override
            public int hashCode() {
                return Objects.hash( getName() );
            }

            @Override
            public String toString() {
                return getName()+"(" + flowRate+")";
            }

            @Override
            public int getValue() {
                return flowRate;
            }

            @Override
            public List<Valve> getChildren() {
                return tunnelsTo.keySet().stream().map( VALVES::get ).toList();
            }

            public boolean hasFlow() {
                return flowRate>0;
            }

        }

    }


    public static void main( String[] args )
       {

        List<Day<?>> daysOfAdvent = List.of(new Day1(), new Day2(), new Day3(), new Day4(), new Day5(), new Day6(),
                                            new Day7(), new Day8(), new Day9(), new Day10(), new Day11(), new Day12(), new Day13(),
                                            new Day14(), new Day15());

        Day<?> today = new Day16();
        today.run( false );


        }

    }


