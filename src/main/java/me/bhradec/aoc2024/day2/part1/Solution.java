package me.bhradec.aoc2024.day2.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private static final int MIN_DIFF = 1;
    private static final int MAX_DIFF = 3;

    private boolean isLevelSafe(List<Integer> level) {
        log.debug("Read level: {}", level);

        List<Integer> ascendingLevel = new ArrayList<>(level);
        ascendingLevel.sort(Comparator.naturalOrder());

        List<Integer> descendingLevel = new ArrayList<>(level);
        descendingLevel.sort(Comparator.reverseOrder());

        if (!level.equals(ascendingLevel) && !level.equals(descendingLevel)) {
            log.debug("Level not ascending or descending");
            return false;
        }

        for (int i = 0; i < level.size() - 1; i++) {
            int diff = Math.abs(level.get(i) - level.get(i + 1));

            if (diff < MIN_DIFF || diff > MAX_DIFF) {
                log.debug("Level elements ({} and {}) unsafe", level.get(i), level.get(i + 1));
                return false;
            }
        }

        return true;
    }

    private long countSafeFromInput(String path) throws IOException {
        long safeLevelCnt = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                List<Integer> level = Arrays.stream(line.split(" ")).map(Integer::parseInt).toList();
                if (isLevelSafe(level)) safeLevelCnt++;
                line = reader.readLine();
            }
        }

        return safeLevelCnt;
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
            long safeLevelCnt = solution.countSafeFromInput(inputPath);
            log.info("Safe levels: {}", safeLevelCnt);
        } catch (IOException exception) {
            log.error("Could not count safe levels", exception);
        }
    }
}
