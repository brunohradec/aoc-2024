package me.bhradec.aoc2024.day4.part2;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private static final int ROWS = 140;
    private static final int COLS = 140;

    private static final String SEARCH_TEXT = "XMAS";

    private final List<Pattern> patterns = new ArrayList<>(){{
        add(Pattern.compile("M.M.A.S.S"));
        add(Pattern.compile("S.S.A.M.M"));
        add(Pattern.compile("M.S.A.M.S"));
        add(Pattern.compile("S.M.A.S.M"));
    }};

    private String[][] arr = new String[ROWS][COLS];

    private int countFromInput(String path) throws IOException {
        read2DArrFromInput(path);
        return countOccurrences();
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

    private int countOccurrences() {
        int result = 0;

        for (int i = 0; i <= ROWS - 3; i++) {
            for (int j = 0; j <= COLS - 3; j++) {

                StringBuilder flatSubArrayBuilder = new StringBuilder();

                for (int x = i; x < i + 3; x++) {
                    for (int y = j; y < j + 3; y++) {
                        flatSubArrayBuilder.append(arr[x][y]);
                    }
                }

                for (Pattern pattern : patterns) {
                    if (pattern.matcher(flatSubArrayBuilder.toString()).find()) {
                        result += 1;
                    }
                }
            }
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
            log.info("Result: {}", solution.countFromInput(inputPath));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
