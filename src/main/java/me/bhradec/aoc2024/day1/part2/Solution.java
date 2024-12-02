package me.bhradec.aoc2024.day1.part2;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private final List<Integer> leftList = new ArrayList<>();
    private final Map<Integer, Integer> rightListfrequency = new HashMap<>();

    private void parseInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                Integer leftNum = Integer.valueOf(line.split("\\s+")[0]);
                Integer rightNum = Integer.valueOf(line.split("\\s+")[1]);

                leftList.add(leftNum);

                if (rightListfrequency.get(rightNum) != null) {
                    rightListfrequency.put(rightNum, rightListfrequency.get(rightNum) + 1);
                } else {
                    rightListfrequency.put(rightNum, 1);
                }

                line = reader.readLine();
            }
        }
    }

    private long getSimilarityScore() {
        long similarityScore = 0;

        for (int leftNum : leftList) {
            similarityScore += (long) leftNum * rightListfrequency.getOrDefault(leftNum, 0);
        }

        return similarityScore;
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
            solution.parseInput(inputPath);
        } catch (IOException exception) {
            log.error("Could not read input file", exception);
            return;
        }

        log.info("Result: {}", solution.getSimilarityScore());
    }
}
