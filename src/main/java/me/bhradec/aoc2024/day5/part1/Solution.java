package me.bhradec.aoc2024.day5.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private Map<Integer, List<Integer>> orders = new HashMap<>();
    private List<List<Integer>> pageGroups = new ArrayList<>();
    private List<List<Integer>> correctPageGroups = new ArrayList<>();

    private int findResultFromInput(String path) throws IOException {
        parseInput(path);
        findCorrectPageGroups();
        return sumMiddleElements();
    }

    private void parseInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            log.debug("Reading orders");

            while (!line.trim().isEmpty()) {
                Integer first = Integer.parseInt(line.split("\\|")[0]);
                Integer second = Integer.parseInt(line.split("\\|")[1]);

                if (!orders.containsKey(first)) {
                    orders.put(first, new ArrayList<>(List.of(second)));
                } else {
                    orders.get(first).add(second);
                }

                line = reader.readLine();
            }

            log.debug("Read orders: {}", orders);

            line = reader.readLine();

            log.debug("Reading page groups");

            while (line != null) {
                pageGroups.add(Arrays.stream(line.split(",")).map(Integer::parseInt).toList());

                line = reader.readLine();
            }

            log.debug("Read groups: {}", pageGroups);
        }
    }

    private void findCorrectPageGroups() {
        log.debug("Finding correct page groups");

        for (List<Integer> pageGroup : pageGroups) {
            boolean isCorrect = true;

            for (int i = pageGroup.size() - 1; i >= 0; i--) {
                for (int j = i; j >= 0; j--) {
                    if (orders.containsKey(pageGroup.get(i)) && orders.get(pageGroup.get(i)).contains(pageGroup.get(j))) {
                        isCorrect = false;
                        break;
                    }
                }
            }

            if (isCorrect) {
                correctPageGroups.add(pageGroup);
            }
        }

        log.debug("Correct page groups: {}", correctPageGroups);
    }

    private int sumMiddleElements() {
        int sum = 0;

        for (List<Integer> pageGroup : correctPageGroups) {
            sum += pageGroup.get(pageGroup.size() / 2);
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
            log.info("Result: {}", solution.findResultFromInput(inputPath));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
