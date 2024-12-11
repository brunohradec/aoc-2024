package me.bhradec.aoc2024.day11.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private long blinkAndCountStonesFromInput(String path, int numberOfBlinks) throws IOException {
        List<Long> stones = parseStonesFromInput(path);
        log.debug("Stones: {}", stones);

        List<Long> result = new ArrayList<>(stones);

        for (int i = 0; i < numberOfBlinks; i++) {
            result = blink(result);
            log.debug("After blink {}: {}", i + 1, result);
        }

        return result.size();
    }

    private List<Long> parseStonesFromInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            return Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
        }
    }

    private List<Long> blink(List<Long> stones) {
        return stones
                .stream()
                .flatMap(stone -> nextStone(stone).stream())
                .toList();
    }

    private List<Long> nextStone(Long stone) {
        if (stone.equals(0L)) {
            return List.of(1L);
        }

        String stoneStr = stone.toString();
        int strLength = stoneStr.length();

        if (strLength % 2 == 0) {
            return List.of(
                    Long.parseLong(stoneStr.substring(0, strLength / 2)),
                    Long.parseLong(stoneStr.substring(strLength / 2))
            );
        }

        return List.of(stone * 2024);
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
            log.info("Result: {}", solution.blinkAndCountStonesFromInput(inputPath, 25));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
