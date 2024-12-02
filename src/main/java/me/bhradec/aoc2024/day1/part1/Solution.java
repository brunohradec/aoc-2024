package me.bhradec.aoc2024.day1.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private List<Integer> leftList = new ArrayList<>();
    private List<Integer> rightList = new ArrayList<>();

    private void parseInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                Integer leftNum = Integer.valueOf(line.split("\\s+")[0]);
                Integer rightNum = Integer.valueOf(line.split("\\s+")[1]);

                insertSorted(leftNum, leftList);
                insertSorted(rightNum, rightList);

                line = reader.readLine();
            }
        }
    }

    private void insertSorted(Integer value, List<Integer> list) {
        if (list.isEmpty()) {
            list.add(value);
            return;
        }

        int indexToInsert = Collections.binarySearch(list, value);
        if (indexToInsert < 0) indexToInsert = -(indexToInsert) - 1;
        list.add(indexToInsert, value);
    }

    private long getDiffSum() {
        long sum = 0;

        for (int i = 0; i < leftList.size(); i++) {
            sum += Math.abs(leftList.get(i) - rightList.get(i));
        }

        return sum;
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

        log.info("Result: {}", solution.getDiffSum());
    }
}
