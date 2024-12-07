package me.bhradec.aoc2024.day6.part2_faster;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private final int MAX_WIDTH = 130;
    private final int MAX_HEIGHT = 130;

    private int findResultFromInput(String path) throws IOException {
        String[][] space = parseInput(path, MAX_HEIGHT, MAX_WIDTH);
        Position startPosition = findStartPosition(space);

        int loopCount = 0;

        List<Position> guardTrail = getGuardTrail(startPosition, space);

        for (Position position : guardTrail) {
            String initialValue = space[position.getY()][position.getX()];

            space[position.getY()][position.getX()] = "O";

            if (checkLoop(startPosition, space)) {
                // System.out.println("Loop detected:");
                // printSpace(spaceWithObstacle);
                loopCount++;
            }
            space[position.getY()][position.getX()] = initialValue;
        }

        return loopCount;
    }

    private String[][] parseInput(String path, int height, int width) throws IOException {
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

    private Position findStartPosition(String[][] space) {
        for (int i = 0; i < space.length; i++) {
            for (int j = 0; j < space[i].length; j++) {
                if (space[i][j].equals("^")) {
                    return new Position(j, i);
                }
            }
        }

        throw new RuntimeException("Start position not found");
    }

    private List<Position> getGuardTrail(Position startPosition, String[][] space) {
        List<Position> visitedPositions = new ArrayList<>();

        int i = startPosition.getY();
        int j = startPosition.getX();

        int iNext = i;
        int jNext = j;

        Direction direction = switch (space[i][j]) {
            case "^" -> Direction.UP;
            case "v" -> Direction.DOWN;
            case "<" -> Direction.LEFT;
            case ">" -> Direction.RIGHT;
            default -> throw new RuntimeException("Position does not match any known state");
        };

        while (true) {
            if (direction == Direction.UP) {
                iNext = i - 1;
                jNext = j;
            }

            if (direction == Direction.DOWN) {
                iNext = i + 1;
                jNext = j;
            }

            if (direction == Direction.LEFT) {
                iNext = i;
                jNext = j - 1;
            }

            if (direction == Direction.RIGHT) {
                iNext = i;
                jNext = j + 1;
            }

            if (!isOut(new Position(jNext, iNext), MAX_HEIGHT, MAX_WIDTH)) {
                if (space[iNext][jNext].equals("#") || space[iNext][jNext].equals("O")) {
                    direction = switch (direction) {
                        case UP -> Direction.RIGHT;
                        case DOWN -> Direction.LEFT;
                        case LEFT -> Direction.UP;
                        case RIGHT -> Direction.DOWN;
                    };

                } else {
                    i = iNext;
                    j = jNext;

                    if (!visitedPositions.contains(new Position(j, i)) && !(i == startPosition.getY() && j == startPosition.getX())) {
                        visitedPositions.add(new Position(j, i));
                    }
                }
            } else {
                break;
            }
        }

        return visitedPositions;
    }

    private boolean checkLoop(Position startPosition, String[][] space) {
        int i = startPosition.getY();
        int j = startPosition.getX();

        int iNext = i;
        int jNext = j;

        List<Movement> movementLog = new ArrayList<>();

        Direction direction = switch (space[i][j]) {
            case "^" -> Direction.UP;
            case "v" -> Direction.DOWN;
            case "<" -> Direction.LEFT;
            case ">" -> Direction.RIGHT;
            default -> throw new RuntimeException("Position does not match any known state");
        };

        movementLog.add(new Movement(j, i, direction));

        while (true) {
            if (direction == Direction.UP) {
                iNext = i - 1;
                jNext = j;
            }

            if (direction == Direction.DOWN) {
                iNext = i + 1;
                jNext = j;
            }

            if (direction == Direction.LEFT) {
                iNext = i;
                jNext = j - 1;
            }

            if (direction == Direction.RIGHT) {
                iNext = i;
                jNext = j + 1;
            }

            if (!isOut(new Position(jNext, iNext), MAX_HEIGHT, MAX_WIDTH)) {
                if (space[iNext][jNext].equals("#") || space[iNext][jNext].equals("O")) {
                    direction = switch (direction) {
                        case UP -> Direction.RIGHT;
                        case DOWN -> Direction.LEFT;
                        case LEFT -> Direction.UP;
                        case RIGHT -> Direction.DOWN;
                    };

                    // log.debug("Turn: {}", direction);
                } else {
                    i = iNext;
                    j = jNext;

                    Movement movement = new Movement(j, i, direction);

                    if (movementLog.contains(movement)) {
                        return true;
                    } else {
                        movementLog.add(movement);
                    }
                }
            } else {
                return false;
            }
        }
    }

    private boolean isOut(Position position, int height, int width) {
        return position.getX() >= width || position.getX() < 0 || position.getY() >= height || position.getY() < 0;
    }

    private void printSpace(String[][] space) {
        System.out.println();
        for (String[] row : space) {
            System.out.println(Arrays.toString(row));
        }
    }

    private int countObstacles(String[][] space) {
        int count = 0;

        for (String[] row : space) {
            for (String col : row) {
                if (col.equals("#") || col.equals("O")) {
                    count++;
                }
            }
        }

        return count;
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
