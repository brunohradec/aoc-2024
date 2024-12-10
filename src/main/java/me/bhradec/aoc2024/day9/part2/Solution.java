package me.bhradec.aoc2024.day9.part2;

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
        List<DiskBlock> diskMap = getDiskMapFromInput(path);
        List<DiskBlock> defragedDiskMap = defragDiskMap(diskMap);

        return hash(defragedDiskMap);
    }

    private List<DiskBlock> getDiskMapFromInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            List<String> diskMap = new ArrayList<>(Arrays.stream(line.split("")).toList());
            List<DiskBlock> diskBlockMap = new ArrayList<>();

            int fid = 0;

            for (int i = 0; i < diskMap.size(); i++) {
                if (i % 2 != 0) {
                    List<String> blockContent = new ArrayList<>();

                    for (int j = 0; j < Integer.parseInt(diskMap.get(i)); j++) {
                        blockContent.add(".");
                    }

                    diskBlockMap.add(new DiskBlock(".", blockContent));
                } else {
                    List<String> blockContent = new ArrayList<>();

                    for (int j = 0; j < Integer.parseInt(diskMap.get(i)); j++) {
                        blockContent.add(Integer.toString(fid));
                    }

                    diskBlockMap.add(new DiskBlock(Integer.toString(fid), blockContent));

                    fid++;
                }
            }

            return diskBlockMap;
        }
    }

    private List<DiskBlock> defragDiskMap(List<DiskBlock> diskMap) {
        List<DiskBlock> diskMapCopy = new ArrayList<>(diskMap);

        for (int i = diskMapCopy.size() - 1; i >= 0; i--) {
            DiskBlock currentBlockFromBack = diskMapCopy.get(i);

            // Is the block from the back a non-empty file block?
            if (!currentBlockFromBack.getFid().equals(".") && !currentBlockFromBack.getContent().isEmpty()) {
                for (int j = 0; j < diskMapCopy.size(); j++) {
                    DiskBlock currentBlockFromFront = diskMapCopy.get(j);

                    // Is the block from front an empty block that can fit the file block from the back?
                    if (currentBlockFromFront.getFid().equals(".") && currentBlockFromFront.getContent().size() >= currentBlockFromBack.getContent().size() && j < i) {
                        List<String> emptyBlockContent = currentBlockFromFront.getContent();
                        List<String> fileBlockContent = currentBlockFromBack.getContent();

                        // Insert file into the place opf the empty block
                        diskMapCopy.set(j, new DiskBlock(
                                currentBlockFromBack.getFid(),
                                new ArrayList<>(fileBlockContent)
                        ));

                        // Insert remaining empty space if there is any left

                        int sizeDiff = emptyBlockContent.size() - fileBlockContent.size();

                        if (sizeDiff > 0) {
                            ArrayList<String> remainingEmptySpace = new ArrayList<>();

                            for (int n = 0; n < sizeDiff; n++) {
                                remainingEmptySpace.add(".");
                            }

                            diskMapCopy.add(j + 1, new DiskBlock(".", remainingEmptySpace));
                        }

                        // Replace file block with empty space
                        currentBlockFromBack.setFid(".");
                        currentBlockFromBack.setContent(currentBlockFromBack.getContent().stream().map(c -> ".").toList());

                        break;
                    }
                }
            }
        }

        return diskMapCopy;
    }

    private long hash(List<DiskBlock> diskMap) {
        long sum = 0;

        List<String> flattenedDiskMap = diskMap
                .stream()
                .flatMap(b -> b.getContent().stream())
                .toList();

        for (int i = 0; i < flattenedDiskMap.size(); i++) {
            if (!flattenedDiskMap.get(i).equals(".")) {
                sum += Long.parseLong(flattenedDiskMap.get(i)) * i;
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
