package me.bhradec.aoc2024.day8.part2;

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
        return countAntinodes(antennaLocations, MAX_HEIGHT, MAX_WIDTH, map);
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

    private int countAntinodes(Map<String, List<Antenna>> antennas, int height, int width, String[][] map) {
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

                    log.debug("First direction");

                    if (distanceFromOrigin(firstLocation) > distanceFromOrigin(secondLocation)) {

                    }

                    // For next harmonic formula to work, first considered location must be closer to origin then second
                    Location firstLocationHelper = distanceFromOrigin(firstLocation) < distanceFromOrigin(secondLocation)
                            ? firstLocation
                            : secondLocation;

                    Location secondLocationHelper = distanceFromOrigin(secondLocation) > distanceFromOrigin(firstLocation)
                            ? secondLocation
                            : firstLocation;

                    while (true) {
                        log.debug("First location: {}", firstLocationHelper);
                        log.debug("Second location: {}", secondLocationHelper);

                        Location nextHarmonicLocation = new Location(
                                secondLocationHelper.getX() + (secondLocationHelper.getX() - firstLocationHelper.getX()),
                                secondLocationHelper.getY() + (secondLocationHelper.getY() - firstLocationHelper.getY())
                        );

                        log.debug("Next harmonic: {}", nextHarmonicLocation);

                        if (isInRange(nextHarmonicLocation, width, height)) {
                            log.debug("Harmonic is in range. Continuing direction.");
                            antinodeLocations.add(nextHarmonicLocation);
                            firstLocationHelper = secondLocationHelper;
                            secondLocationHelper = nextHarmonicLocation;
                        } else {
                            log.debug("Harmonic is out of range. Stopping direction.");
                            break;
                        }
                    }

                    log.debug("Second direction");

                    // For next harmonic formula to work, first considered location must be closer to origin then second
                    firstLocationHelper = distanceFromOrigin(firstLocation) < distanceFromOrigin(secondLocation)
                            ? firstLocation
                            : secondLocation;

                    secondLocationHelper = distanceFromOrigin(secondLocation) > distanceFromOrigin(firstLocation)
                            ? secondLocation
                            : firstLocation;

                    while (true) {
                        log.debug("First location: {}", firstLocationHelper);
                        log.debug("Second location: {}", secondLocationHelper);

                        Location nextHarmonicLocation = new Location(
                                firstLocationHelper.getX() - (secondLocationHelper.getX() - firstLocationHelper.getX()),
                                firstLocationHelper.getY() - (secondLocationHelper.getY() - firstLocationHelper.getY())
                        );

                        log.debug("Next harmonic: {}", nextHarmonicLocation);

                        if (isInRange(nextHarmonicLocation, width, height)) {
                            log.debug("Harmonic is in range. Continuing direction.");
                            antinodeLocations.add(nextHarmonicLocation);
                            secondLocationHelper = firstLocationHelper;
                            firstLocationHelper = nextHarmonicLocation;
                        } else {
                            log.debug("Harmonic is out of range. Stopping direction.");
                            break;
                        }
                    }

                }
            }
        }

        for (Map.Entry<String, List<Antenna>> antennaGroup : antennas.entrySet()) {
            if (antennaGroup.getValue().size() > 1) {
                for (Antenna antenna : antennaGroup.getValue()) {
                    antinodeLocations.add(antenna.getLocation());
                }
            }
        }

        System.out.println("Initial map");
        for (String[] row : map) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println();

        for (Location location : antinodeLocations) {
            map[location.getY()][location.getX()] = "#";
        }

        System.out.println("With antinodes");
        for (String[] row : map) {
            System.out.println(Arrays.toString(row));
        }

        return antinodeLocations.size();
    }

    public double distanceFromOrigin(Location location) {
        return Math.sqrt((location.getY()) * (location.getY()) + (location.getX()) * (location.getX()));
    }

    private boolean isInRange(Location location, int width, int height) {
        return location.getX() < width && location.getX() >= 0 && location.getY() < height && location.getY() >= 0;
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
