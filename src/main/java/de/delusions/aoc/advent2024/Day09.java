package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day09 extends Day<String> {
    private static final Logger LOG = LoggerFactory.getLogger(Day09.class);

    private static final int EMPTY = -1;

    public Day09() {
        super("Disk Fragmenter", "1928", "2858", "6432869891895", "6467290479134");
    }


    /**
     * Represents a block with an identifier, a measure of empty space, and an array of block IDs.
     *
     * Provides functionality for moving blocks, checking if the block is a file,
     * determining if another block fits within it, and creating new blocks with
     * specified properties.
     */
    record Block(int id, AtomicInteger empty,int[] blocks,int start) {

        void move(Block fileBlock) {
            int start = blocks.length-empty.getAndAdd(-fileBlock.blocks.length);
            fileBlock.empty.set(fileBlock.blocks.length);
            for (int i = 0; i < fileBlock.blocks.length; i++) {
                blocks[start+i] = fileBlock.id();
                fileBlock.blocks[i] = EMPTY;
            }
        }

        boolean isFile(){
            return id!=EMPTY;
        }

        boolean fits(Block block) {
            return id == EMPTY && empty.get() >= block.blocks().length;
        }

        static Block create(int id, int size,int start) {
            int[] blocks = new int[size];
            Arrays.fill(blocks, id);
            return new Block(id, new AtomicInteger(id==EMPTY ? size : 0), blocks,start);
        }

    }

    @Override
    public String part0(Stream<String> input) {
        List<Block> blockList = readBlockList(input);
        int[] blocks = blockList.stream().map(Block::blocks).flatMapToInt(Arrays::stream).toArray();

        LOG.debug("Part0: Empty blocks {}",Arrays.stream(blocks).filter(n -> n == EMPTY).count());

        // Move non-empty blocks forward in the array, filling empty spots from the back
        for (int forward = 0; forward < blocks.length; forward++) {
            if (blocks[forward] == EMPTY) {
                for (int backward = blocks.length - 1; backward > forward; backward--) {
                    if (blocks[backward] != EMPTY) {
                        blocks[forward] = blocks[backward]; // Fill forward empty slot
                        blocks[backward] = EMPTY; // Mark the backward slot as empty
                        break;
                    }
                }
            }
        }
        return calculateCheckSum(blocks);
    }

    @Override
    public String part1(Stream<String> input) {
        List<Block> blockList = readBlockList(input);

        // Move each file block to the first available fitting empty block that occurs earlier in the list
        blockList.reversed().stream()
                .filter(Block::isFile)
                .forEach(file ->
                    blockList.stream()
                            .filter(b -> b.fits(file) && b.start < file.start)
                            .findFirst()
                            .ifPresent( b -> b.move(file) ));

        int[] blocks = blockList.stream().flatMapToInt(b -> Arrays.stream(b.blocks())).toArray();

        LOG.debug("Part1: Empty blocks {}",Arrays.stream(blocks).filter(n -> n == EMPTY).count());

        return calculateCheckSum(blocks);
    }

    /**
     * Processes the provided stream of strings into a list of `Block` objects.
     *
     * This method converts the input into a character stream.
     * Each character is converted to a numeric value representing the size of a block.
     * It toggles an `AtomicBoolean` to determine if a block is empty and uses an
     * `AtomicInteger` to assign an incremental ID to each non-empty block. Another
     * `AtomicInteger` is used to track the current starting point, which gets updated
     * with the block size after each block is processed.
     *
     * @param input a stream of strings representing the input data (one line in this case)
     * @return a list of `Block` objects created from the input data
     */
    private static List<Block> readBlockList(Stream<String> input) {
        AtomicBoolean empty = new AtomicBoolean(false);
        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger currentStart = new AtomicInteger(0);

        return input
                .collect(Collectors.joining())
                .chars()
                .map(character -> character - 48)
                .mapToObj(size -> Block.create(empty.getAndSet(!empty.get()) ? EMPTY : id.getAndIncrement(),size,currentStart.getAndAdd(size)))
                .toList();
    }

    /**
     * Calculates a checksum for the given array of block integers.
     *
     * This method iterates over the provided array of integers, `blocks`. For each
     * block, it checks if the block is empty by comparing it to the constant `EMPTY`.
     * If the block is not empty, it multiplies the block's value by its index and
     * adds the result to a running total stored in a `BigInteger`. BigInteger protects
     * from Long overflows, which I expected might happen today.
     *
     * @param blocks an array of integers representing the block values
     * @return a string representation of the calculated checksum
     */
    private static String calculateCheckSum(int[] blocks) {
        BigInteger sum = BigInteger.ZERO;
        for(int i = 0; i< blocks.length; i++){
            sum = sum.add(BigInteger.valueOf(blocks[i]==EMPTY ? 0 : (long) blocks[i] *i));
        }
        return sum.toString();
    }

    // --- the following code is not used, but it helped me find the issue with files moving back
    // instead of forward as I was able to write unit tests for these methods much easier
    // than my functional stuff above:

    record FileTuple(int id, int size) {}
    static FileTuple pickLastFile(int[] blocks){
        int endIdx = -1;
        int startIdx = -1;
        int id = -1;
        for(int i = blocks.length-1; i>=0; i--){
            if(id<0 && blocks[i]!=EMPTY){
                endIdx = i;
                id = blocks[i];
            }
            if(id>0 && blocks[i]!=id){
                startIdx = i;
                break;
            }
        }
        return new FileTuple(blocks[endIdx],endIdx-startIdx);
    }

    static int findEmptyIdx(int size,int[] blocks){
        int startIdx = -1;
        for(int i = 0; i<blocks.length; i++){
            if(startIdx<0 && blocks[i]==EMPTY){
                startIdx = i;
            } else if (startIdx>=0 && blocks[i]!=EMPTY && startIdx+size<=i){
                return startIdx;
            } else {
                startIdx = -1;
            }
        }
        return -1;
    }

}
