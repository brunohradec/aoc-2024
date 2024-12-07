package me.bhradec.aoc2024.day7.part2;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private Long findResultFromInput(String path) throws IOException {
        Long sum = 0L;

        Map<Long, List<Long>> equations = parseInput(path);

        for (Map.Entry<Long, List<Long>> equation : equations.entrySet()) {
            if (isEquationSolvable(
                    equation.getKey(),
                    equation.getValue(),
                    equation.getValue().getFirst(),
                    0)) {

                sum += equation.getKey();
            }
        }

        return sum;
    }

    private Map<Long, List<Long>> parseInput(String path) throws IOException {
        Map<Long, List<Long>> equations = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                Long result = Long.parseLong(line.split(":")[0]);
                List<Long> operands = Arrays.stream(line.split(":")[1].trim().split(" "))
                        .map(Long::parseLong)
                        .toList();

                equations.put(result, operands);

                line = reader.readLine();
            }
        }

        return equations;
    }

    private boolean isEquationSolvable(
            Long result,
            List<Long> operands,
            Long partialResult,
            Integer index) {

        // log.debug("-------------------------------------");
        // log.debug("Result: {}", result);
        // log.debug("Operands: {}", operands);
        // log.debug("Partial result: {}", partialResult);
        // log.debug("Index: {}", index);

        if (index == operands.size() - 1 && partialResult.equals(result)) {
            // log.debug("Result found");
            return true;
        }
        else if (index == operands.size() - 1 && !partialResult.equals(result)) {
            // log.debug("No result on this path");
            return false;
        }

        return isEquationSolvable(result, operands, partialResult * operands.get(index + 1), index + 1)
                || isEquationSolvable(result, operands, partialResult + operands.get(index + 1), index + 1)
                || isEquationSolvable(result, operands, Long.parseLong(partialResult.toString() + operands.get(index + 1).toString()), index + 1);
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option inputPathOption = Option.builder("i")
                .longOpt("input")
                .argName("INPUT_PATH")
                .desc("Path to the input file")
                .hasArg()
                .required(true)
                .build();

        options.addOption(inputPathOption);

        CommandLineParser commandLineParser = new DefaultParser();

        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException exception) {
            log.error("Error parsing arguments", exception);
            return;
        }

        String inputPath = commandLine.getOptionValue(inputPathOption);

        Solution solution = new Solution();

        try {
            log.info("Result: {}", solution.findResultFromInput(inputPath));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
