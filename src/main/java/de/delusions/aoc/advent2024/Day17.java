package de.delusions.aoc.advent2024;

import de.delusions.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 extends Day<String> {

    private static final Logger LOG = LoggerFactory.getLogger(Day17.class);

    public Day17() {

        super("Chronospatial Computer", "4,6,3,5,6,3,5,2,1,0", "117440","4,0,4,7,1,2,7,1,6", "");
    }

    Pattern REGEX = Pattern.compile("(\\d+)");

    @Override
    public String part0(Stream<String> input) {
        Computer seven_of_bit = Computer.create(REGEX.matcher(input.collect(Collectors.joining())).results().map(m -> Integer.parseInt(m.group(1))).toList());
        while (seven_of_bit.isRunning()) {
            seven_of_bit.executeNextInstruction();
        }
        return printNumbers(seven_of_bit.output);
    }

//0,3,5,4,3,0 -> a = a / 8


// b = (a%8) xor 1 (das können ja nur zahlen von 0...7 sein)
// c = a / (2 ^ b)
// a = a / 8 ->

//    2,4,1,1,7,5,0,3,1,4,4,5,5,5,3,0 -> 16 outputs d.h. a > Math.pow(8,16) -> **281,474,976,710,656**
//    b = a % 8   (0,1,2,3,4,5,6,7)
//    b = b xor 1 (1,0,3,2,5,4,7,0)
//    c = a / (2 hoch b)
//    a = a / 8
//    b = b xor 4  (
//    b = b xor c
//    out b
//    jump to start

    @Override
    public String part1(Stream<String> input) {
        List<Integer> numbers = REGEX.matcher(input.collect(Collectors.joining())).results().map(m -> Integer.parseInt(m.group(1))).toList();
        List<Long> targetNums = numbers.stream().skip(3).map(i -> (long)i).toList();
        String target = printNumbers(targetNums);
        Computer seven_of_bit = Computer.create(numbers);
        long count = (long) Math.pow(8,targetNums.size()-1);
        while(!target.equals(printNumbers(seven_of_bit.output))){
            seven_of_bit.reset(count);
            while (seven_of_bit.isRunning()) {
                seven_of_bit.executeNextInstruction();
            }
            count++;
//            if(count % 1000000 == 0){
//                LOG.info("Count: {} Out={}",count, printNumbers(seven_of_bit.output));
//            }
        }
        return ""+(count-1);

    }

    String printNumbers (List<Long> numbers){
        return numbers.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    record Computer(AtomicLong a, AtomicLong b, AtomicLong c, AtomicInteger programCounter, List<Instruction> program, List<Long> output) {

        static Computer create(List<Integer> numbers) {
            AtomicLong a = new AtomicLong(numbers.get(0));
            AtomicLong b = new AtomicLong(numbers.get(1));
            AtomicLong c = new AtomicLong(numbers.get(2));
            List<Instruction> program = numbers.stream().skip(3).map(i -> Instruction.values()[i]).toList();
            return new Computer(a, b, c, new AtomicInteger(0), program,new ArrayList<>());
        }
        void moveProgramPointer(){
            programCounter.set(programCounter.get() + 2);
        }

        boolean isRunning(){
            return programCounter.get() < program.size();
        }

        int getLiteralOperand(){
            return this.program.get(programCounter.get()+1).getOpcode();
        }

        void reset(long a){
            this.a.set(a);
            this.b.set(0);
            this.c.set(0);
            this.programCounter.set(0);
            this.output.clear();
        }

        //operand is the next instruction + 1

        long getComboperand() {
            long operand = this.program.get(programCounter.get()+1).getOpcode();

            if (operand <4) {
                return operand;
            } else if (operand == 4) {
                return a.get();
            } else if (operand == 5) {
                return b.get();
            } else if (operand == 6) {
                return c.get();
            } else {
                throw new IllegalArgumentException("Invalid operand: " + operand);
            }
        }
        public Instruction executeNextInstruction() {
            Instruction instruction = program.get(programCounter.get());
            instruction.execute(this);
            if(instruction != Instruction.jnz){
                moveProgramPointer();
            }
            return instruction;
        }

    }


    enum Instruction {

        // The numerator is the value in the A register. The denominator is found by raising 2 to the power of the instruction's combo operand. (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then written to the A register.
        // Performs division of A register by 2 raised to the power of the combo operand, truncating the result, and writes it to register A.
        adv(0) {
            @Override
            void execute(Computer computer) {
                long operand = computer.getComboperand();
                int pow = (int) Math.pow(2, operand);
                computer.a.set(computer.a.get() / pow);
            }
        },

        // Calculates the bitwise XOR of register B and the literal operand, then stores the result in register B.
        bxl(1) {
            @Override
            void execute(Computer computer) {
                computer.b.set(computer.b.get() ^ computer.getLiteralOperand());
            }
        },

        // Calculates the value of the combo operand modulo 8, then writes that value to register B.
        bst(2) {
            @Override
            void execute(Computer computer) {
                computer.b.set(computer.getComboperand() % 8);
            }
        },

        // Jumps to the instruction pointer specified by the literal operand if the A register is not zero; otherwise, does nothing.
        jnz(3) {
            @Override
            void execute(Computer computer) {
                if (computer.a.get() != 0) {
                    computer.programCounter.set(computer.getLiteralOperand());
                } else {
                    computer.programCounter().set(computer.program().size());
                }
            }
        },

        // Calculates the bitwise XOR of register B and register C, then stores the result in register B. Ignores the operand.
        bxc(4) {
            @Override
            void execute(Computer computer) {
                computer.b.set(computer.b.get() ^ computer.c.get());
            }
        },

        // Outputs the value of the combo operand modulo 8.
        out(5) {
            @Override
            void execute(Computer computer) {
                computer.output.add(computer.getComboperand() % 8);
            }
        },

        // Operates like `adv`, but writes the result to register B instead of register A.
        bdv(6) {
            @Override
            void execute(Computer computer) {
                long operand = computer.getComboperand();
                int pow = (int) Math.pow(2, operand);
                computer.b.set(computer.a.get() / pow);
            }
        },
        // Operates like `adv`, but writes the result to register C instead of register A.
        cdv(7) {
            @Override
            void execute(Computer computer) {
                long operand = computer.getComboperand();
                int pow = (int) Math.pow(2, operand);
                computer.c.set(computer.a.get() / pow);
            }
        };

        private final int opcode;

        Instruction(int opcode) {
            this.opcode = opcode;
        }

        public int getOpcode() {
            return opcode;
        }

        abstract void execute(Computer computer);
    }
}
