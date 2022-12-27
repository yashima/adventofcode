This is my java code for AdventOfCode 2022
https://adventofcode.com/2022

Initially this was just a small programming exercise
but as puzzles got more complex I needed more structure.

First it was all one big scratch file in IntelliJ and
the early days have a lot of mixed up solutions or missing
pieces from part 1 mostly.

The next step was to make it into a project with
different classes for each day but by that time there
was a lot of chaos that could not be easily cleaned up.

To try the code just copy a Day specific class and
execute either part1() or part2() where they are implemented.
Both methods expect a Stream<String> as input.
The code for reading the input from the files lives
in the parent class adjust the PATH as necessary there.

I wrote some utility classes that I was able to recycle in some places.
Those live in the util package if they are needed.

Sidenote: I tend to write javadocs as an exercise to get
clarity so when there are javadocs above methods it probably means
I struggled a lot to solve that puzzle