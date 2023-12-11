# adventofcode2023

My attempts at solving Advent of Code 2023 Puzzles

* Day 1 "Trebuchet" involved recognizing digits in the input--for part two some written out in words.
  It was a bit evil as the numbers overlapped so a simple search and replace would fail.
  The secondary problem was that there might only be a single digit on a line.
* Day 2 "Cube Conundrum" had bags filled with a number of colored cubes, the input consisted of descriptions of games with the bag and
  one had to count the possible ones. Just check if one of the color numbers was higher than the actual bag. The trick was more
  how to read the input effectively because not all colors were present in all moves. For part 2 one had to find the
  maximum # of stones per color that would be needed to make all games possible.
* Day 3 "Great Ratios". First appearance of a Matrix input was on the early side. It had digits that had to be made into numbers and
  then find the ones that were next to "parts" and for part two find all the "gear" (*) parts that were adjacent to 2 numbers. For both parts
  the tricky thing was to resolve the numbers from the adjacent digits. It helps that I already had a matrix class from the previous year.
* Day 4 "Scratchcards": A bunch of numbers counted as winning, for part 1 simply compare the sets of numbers. Part 2 however involved some tricky
  processing of amount of winning numbers to add additional cards further down the line
* Day 5 "If You Give A Seed A Fertilizer". This was a tricky one. Part one involved just processing some starting numbers through a series of matches.
  For part 2 though the input got reinterpreted not to numbers but to ranges that contained huge amounts of numbers. So it was necessary to calculate
  the boundaries of the ranges instead of every single number. Since there were multiple options for each matching, it might be that a range could
  be split into up to three parts for the next step of the processing. My Interval class from last year came in quite useful. I wrote a unit test
  for the first time for an Advent of Code puzzle.
* Day 6 "Wait For It" a race game that involved some simple maths with the quadratic formula--because I am bad at simple maths I brute forced
  half of it. I should go back and improve my solution
* Day 7 "Camel Cards" another game, this time poker adjacent. I solved this mostly with object oriented programming. The trick was to figure out how
  to efficiently determine what kind of hand you were holding. A map with counts of card types was very useful. Then write a Comparator to help with
  sorting. Part 2 made Jacks into Jokers. So jokers could improve a hand and it was easy to find a mapping which hands can be improved in what way.
  Easy to overlook however: Jacks value went way down to 0 and changed the sorting. Very much fun to program this one.
* Day 8 "Haunted Wasteland" the most tricky so far. For part 1 one had to simply follow a command sequence of left/right choices through a directed
  graph which was simple enough to just need a Map. Part 2 made it an optimization problem by demanding to synchronize several paths. The trick was to
  look
  at the input and play around with it to find that it was cyclic with a cycle that was easily detectable. Needed the smallest common multiple (scm)
  Also involved prime factoring unless you
  had a hunch after studying the input.
* Day 9 "Mirage Maintenance" a first recursion that was quite thoroughly described in the puzzle description and so could be easily implemented.  
  Helped me discover some new Java21 features.
* Day 10 "Pipe Maze" another matrix input this time moving along a path. Once again I mixed up X and Y. Part 1 was easily solved once the directions
  were fixed. Just count the steps (and divide by 2). Part 2 was greatly helped by visualising the loop with ASCII characters. Then it is possible to
  iterate row by row and simply check if we inside or outside the loop by comparing the character set associated with clockwise or counterclockwise
  movement which is determined by the starting facing. No flood fill needed.
* Day 11 "Cosmic Expansion" and more 2D riddles. This one was pretty easy as my matrix could instantly give me all the stars and I already had a
  Manhattan distance function in place. Figuring out where empty space was and how to add it to the distance was the only challenge here and realizing
  that I had to subtract one from the expansion rate or that for part 1 the expansion rate was actually 2 not 1--because 1 row gets replaced by 2 or
  by 100 but that first one is already there so -1. 