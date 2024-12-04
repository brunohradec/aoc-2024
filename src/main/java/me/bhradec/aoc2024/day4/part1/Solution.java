package me.bhradec.aoc2024.day4.part1;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private static final int ROWS = 140;
    private static final int COLS = 140;

    private static final String SEARCH_TEXT = "XMAS";

    private String[][] arr = new String[ROWS][COLS];

    private int countWordsFromInput(String path) throws IOException {
        read2DArrFromInput(path);
        return countHorizontal() + countVertical() + countDiagonal();
    }

    private void read2DArrFromInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            int i = 0;
            while (line != null) {
                String[] splitLine = line.split("");
                for (int j = 0; j < splitLine.length; j++) {
                    arr[i][j] = splitLine[j];
                }

                line = reader.readLine();
                i++;
            }
        }
    }

    private int countHorizontal() {
        int result = 0;

        for (int i = 0; i < ROWS; i++) {
            String line = String.join("", arr[i]);
            String reversedLine = (new StringBuilder(line)).reverse().toString();

            int occurrences = StringUtils.countMatches(line, SEARCH_TEXT);
            int reversedOccurrences = StringUtils.countMatches(reversedLine, SEARCH_TEXT);

            log.debug("HORIZONTAL - read line: {}, occurrences: {}", line, occurrences);
            log.debug("HORIZONTAL - reversed line: {}, occurrences: {}", reversedLine, reversedOccurrences);

            result += occurrences + reversedOccurrences;
        }

        return result;
    }

    private int countVertical() {
        int result = 0;
        StringBuilder lineBuilder = new StringBuilder();

        for (int j = 0; j < COLS; j++) {
            for (int i = 0; i < ROWS; i++) {
                lineBuilder.append(arr[i][j]);
            }

            String line = lineBuilder.toString();
            String reversedLine = lineBuilder.reverse().toString();

            int occurrences = StringUtils.countMatches(line, SEARCH_TEXT);
            int reversedOccurrences = StringUtils.countMatches(reversedLine, SEARCH_TEXT);

            log.debug("VERTICAL - read line: {}, occurrences: {}", line, occurrences);
            log.debug("VERTICAL - reversed line: {}, occurrences: {}", reversedLine, reversedOccurrences);

            result += occurrences + reversedOccurrences;

            lineBuilder = new StringBuilder();
        }

        return result;
    }

    private int countDiagonal() {
        int result = 0;
        StringBuilder lineBuilder = new StringBuilder();

        for (int d = 0; d <= ROWS + COLS - 2; d++) {
            for (int j = 0; j <= d; j++) {
                int i = d - j;
                if (i < ROWS && j < COLS) {
                    lineBuilder.append(arr[i][j]);
                }
            }

            String line = lineBuilder.toString();
            String reversedLine = lineBuilder.reverse().toString();

            int occurrences = StringUtils.countMatches(line, SEARCH_TEXT);
            int reversedOccurrences = StringUtils.countMatches(reversedLine, SEARCH_TEXT);

            log.debug("RIGHT DIAGONALS  - read line: {}, occurrences: {}", line, occurrences);
            log.debug("RIGHT DIAGONALS - reversed line: {}, occurrences: {}", reversedLine, reversedOccurrences);

            result += occurrences + reversedOccurrences;

            lineBuilder = new StringBuilder();
        }

        for (int d = 0; d <= ROWS + COLS - 2; d++) {
            for (int j = 0; j <= d; j++) {
                int i = d - j;
                int mirr = ROWS - i;
                if (mirr >= 0 && mirr < ROWS && j < COLS) {
                    lineBuilder.append(arr[mirr][j]);
                }
            }

            String line = lineBuilder.toString();
            String reversedLine = lineBuilder.reverse().toString();

            int occurrences = StringUtils.countMatches(line, SEARCH_TEXT);
            int reversedOccurrences = StringUtils.countMatches(reversedLine, SEARCH_TEXT);

            log.debug("LEFT DIAGONALS  - read line: {}, occurrences: {}", line, occurrences);
            log.debug("LEFT DIAGONALS - reversed line: {}, occurrences: {}", reversedLine, reversedOccurrences);

            result += occurrences + reversedOccurrences;

            lineBuilder = new StringBuilder();
        }

        return result;
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
            log.info("Result: {}", solution.countWordsFromInput(inputPath));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
