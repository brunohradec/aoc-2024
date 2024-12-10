package me.bhradec.aoc2024.day10.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private final int MAX_WIDTH = 54;
    private final int MAX_HEIGHT = 54;

    private long findResultFromInput(String path) throws IOException {
        int[][] map = parseMapFromInput(path, MAX_WIDTH, MAX_HEIGHT);

        for (int i = 0; i < MAX_HEIGHT; i++) {
            System.out.println(Arrays.toString(map[i]));
        }

        return sumTrails(map, MAX_WIDTH, MAX_HEIGHT);
    }

    private int[][] parseMapFromInput(String path, int width, int height) throws IOException {
        int[][] arr = new int[height][width];

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            int i = 0;
            while (line != null) {
                String[] splitLine = line.split("");

                for (int j = 0; j < splitLine.length; j++) {
                    arr[i][j] = Integer.parseInt(splitLine[j]);
                }

                line = reader.readLine();
                i++;
            }
        }

        return arr;
    }

    private int sumTrails(int[][] map, int width, int height) {
        int result = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] == 0) {
                    result += findTrails(map, j, i, width, height, 0).size();
                }
            }
        }

        return result;
    }

    private Set<Coordinate> findTrails(
            int[][] map,
            int x,
            int y,
            int width,
            int height,
            int wantedHeight) {

        if (isOut(x, y, width, height)) {
            log.debug("Rejecting coordinate ({}, {}) as it's out of bounds", x, y);
            return new HashSet<>();
        }

        if (map[y][x] != wantedHeight) {
            log.debug("Rejecting coordinate ({}, {}) with height {} ({})", x, y, map[y][x], wantedHeight);
            return new HashSet<>();
        }

        log.debug("Accepting coordinate ({}, {}) with height {} ({})", x, y, map[y][x], wantedHeight);

        if (map[y][x] == 9) {
            log.debug("Found end of trail at coordinate ({}, {}) with height {}", x, y, map[y][x]);
            HashSet<Coordinate> trailEnd = new HashSet<>();
            trailEnd.add(new Coordinate(x, y));
            return trailEnd;
        }

        Set<Coordinate> resultSet = new HashSet<>();

        resultSet.addAll(findTrails(map, x - 1, y, width, height, wantedHeight + 1));
        resultSet.addAll(findTrails(map, x, y - 1, width, height, wantedHeight + 1));
        resultSet.addAll(findTrails(map, x + 1, y, width, height, wantedHeight + 1));
        resultSet.addAll(findTrails(map, x, y + 1, width, height, wantedHeight + 1));

        return resultSet;
    }

    private boolean isOut(int x, int y, int width, int height) {
        return x >= width || x < 0 || y >= height || y < 0;
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
