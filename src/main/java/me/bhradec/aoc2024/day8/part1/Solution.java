package me.bhradec.aoc2024.day8.part1;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private final int MAX_WIDTH = 50;
    private final int MAX_HEIGHT = 50;

    private int findResultFromInput(String path) throws IOException {
        String[][] map = parseMapFromInput(path, MAX_HEIGHT, MAX_WIDTH);
        Map<String, List<Antenna>> antennaLocations = getAntennaLocations(map, MAX_HEIGHT, MAX_WIDTH);
        return countAntinodes(antennaLocations, MAX_HEIGHT, MAX_WIDTH);
    }

    private String[][] parseMapFromInput(String path, int height, int width) throws IOException {
        String[][] arr = new String[height][width];

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

        return arr;
    }

    private Map<String, List<Antenna>> getAntennaLocations(String[][] map, int height, int width) {
        Map<String, List<Antenna>> antennas = new HashMap<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j].equals(".")) {
                    if (!antennas.containsKey(map[i][j])) {
                        antennas.put(
                                map[i][j],
                                new ArrayList<>(List.of(new Antenna(map[i][j], new Location(j, i))))
                        );
                    } else {
                        antennas.get(map[i][j]).add(new Antenna(map[i][j], new Location(j, i)));
                    }

                }
            }
        }

        return antennas;
    }

    private int countAntinodes(Map<String, List<Antenna>> antennas, int height, int width) {
        Set<Location> antinodeLocations = new HashSet<>();
        for (Map.Entry<String, List<Antenna>> antennaGroup : antennas.entrySet()) {
            for (int i = 0; i < antennaGroup.getValue().size(); i++) {
                for (int j = i; j < antennaGroup.getValue().size(); j++) {
                    Location firstLocation = antennaGroup.getValue().get(i).getLocation();
                    Location secondLocation = antennaGroup.getValue().get(j).getLocation();

                    if (firstLocation.equals(secondLocation)) continue;

                    log.debug(
                            "Checking antennas with frequency: {} ({}, {}) and ({}, {})",
                            antennaGroup.getKey(),
                            firstLocation.getX(),
                            firstLocation.getY(),
                            secondLocation.getX(),
                            secondLocation.getY()
                    );

                    Location antinodeLocation1 = getAntinodeLocation(firstLocation, secondLocation);
                    Location antinodeLocation2 = getAntinodeLocation(secondLocation, firstLocation);

                    log.debug("Antinode location 1: ({}, {})", antinodeLocation1.getY(), antinodeLocation1.getY());
                    log.debug("Antinode location 2: ({}, {})", antinodeLocation2.getY(), antinodeLocation2.getY());

                    if (isInRange(antinodeLocation1.getX(), antinodeLocation1.getY(), width, height)) {
                        log.debug("Antinode 1 in range");
                        antinodeLocations.add(new Location(antinodeLocation1.getX(), antinodeLocation1.getY()));
                    } else {
                        log.debug("Antinode 1 not in range");
                    }

                    if (isInRange(antinodeLocation2.getX(), antinodeLocation2.getY(), width, height)) {
                        log.debug("Antinode 2 in range");
                        antinodeLocations.add(new Location(antinodeLocation2.getX(), antinodeLocation2.getY()));
                    } else {
                        log.debug("Antinode 2 not in range");
                    }
                }
            }
        }

        return antinodeLocations.size();
    }

    private Location getAntinodeLocation(Location first, Location second) {
        return new Location(
                first.getX() - (second.getX() - first.getX()),
                first.getY() - (second.getY() - first.getY())
        );
    }

    private boolean isInRange(int x, int y, int width, int height) {
        return x < width && x >= 0 && y < height && y >= 0;
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
