package me.bhradec.aoc2024.day9.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private long findResultFromInput(String path) throws IOException {
        List<String> diskMap = getDiskMapFromInput(path);
        // log.debug("Disk map: {}", diskMap);
        List<String> defragedDiskMap = defragDiskMap(diskMap);
        // log.debug("Defragmented disk map: {}", defragedDiskMap);
        return hash(defragedDiskMap);
    }

    private List<String> getDiskMapFromInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            List<String> diskMap = new ArrayList<>(Arrays.stream(line.split("")).toList());
            List<String> diskMapWithEmptySpace = new ArrayList<>();

            int fid = 0;

            for (int i = 0; i < diskMap.size(); i++) {
                if (i % 2 != 0) {
                    for (int j = 0; j < Integer.parseInt(diskMap.get(i)); j++) {
                        diskMapWithEmptySpace.add(".");
                    }
                } else {
                    for (int j = 0; j < Integer.parseInt(diskMap.get(i)); j++) {
                        diskMapWithEmptySpace.add(Integer.toString(fid));
                    }
                    fid++;
                }
            }

            return diskMapWithEmptySpace;
        }
    }

    private List<String> defragDiskMap(List<String> diskMap) {
        List<String> diskMapCopy = new ArrayList<>(diskMap);

        int i = 0;
        int j = diskMapCopy.size() - 1;

        while (i < diskMapCopy.size() && j >= 0 && i < j) {
            // log.debug("i: {}", i);
            // log.debug("j: {}", j);

            // Forward find next empty space for filling
            while (i < diskMapCopy.size() - 1 && !diskMapCopy.get(i).equals(".")) {
                i++;
            }

            // Backwards find first non-empty space for moving
            while (j >= 0 && diskMapCopy.get(j).equals(".")) {
                j--;
            }

            if (diskMapCopy.get(i).equals(".") && !diskMapCopy.get(j).equals(".")) {
                diskMapCopy.set(i, diskMapCopy.get(j));
                diskMapCopy.set(j, ".");
                i++;
                j--;
            }

            // log.debug("iteration result: {}", diskMapCopy);
        }

        return diskMapCopy;
    }

    private long hash(List<String> diskMap) {
        long sum = 0;

        for (int i = 0; i < diskMap.size(); i++) {
            if (!diskMap.get(i).equals(".")) {
                sum += Long.parseLong(diskMap.get(i)) * i;
            }
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
