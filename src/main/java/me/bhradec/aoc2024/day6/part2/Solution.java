package me.bhradec.aoc2024.day6.part2;

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

    private final String INITIAL_OBSTACLE = "#";
    private final String ADDED_OBSTACLE = "O";

    private final List<String> obstacles = List.of(
            INITIAL_OBSTACLE,
            ADDED_OBSTACLE
    );

    private int findResultFromInput(String path) throws IOException {
        String[][] space = parseInput(path, MAX_HEIGHT, MAX_WIDTH);
        Position startPosition = findStartPosition(space);

        int loopCount = 0;

        /* Get the positions that the guard visits with the initial obstacles (initial guard path).
         * Since initial obstacles don't change and only new ones are added, there's no use
         * to adding obstacles outside the initial guard path since the guard will never
         * get to such obstacles. The initial guard position is not included to not get
         * overwritten by an added obstacle. */
        List<Position> initialGuardPath = getGuardPath(startPosition, space);

        for (Position position : initialGuardPath) {
            String initialValue = space[position.getY()][position.getX()];

            /* Mutating the initial 2D array is a bit faster than making a
             * copy each time, especially as the 2D array can be quite large. */
            space[position.getY()][position.getX()] = ADDED_OBSTACLE;

            if (checkLoop(startPosition, space)) {
                loopCount++;
            }

            /* As array wasn't copied, after placing the added obstacle and
             * checking for loops, the array is returned to the initial state. */
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

    /**
     * Method used for getting all the positions that guard visits on a
     * given map.
     *
     * @param startPosition The initial position of the guard
     * @param space The map with obstacles and the guard
     *
     * @return A list of visited positions on a map (the guard path)
     */
    private List<Position> getGuardPath(Position startPosition, String[][] space) {
        List<Position> visitedPositions = new ArrayList<>();

        int x = startPosition.getX();
        int y = startPosition.getY();

        Direction direction = getGuardDirection(space[y][x]);

        while (true) {
            Position nextPosition = moveByOneInDirection(new Position(x, y), direction);

            if (!isOut(nextPosition, MAX_HEIGHT, MAX_WIDTH)) {
                if (obstacles.contains(space[nextPosition.getY()][nextPosition.getX()])) {
                    direction = turnGuardRight(direction);
                } else {
                    x = nextPosition.getX();
                    y = nextPosition.getY();

                    /* The start position is not included in the list of visited positions */
                    if (!visitedPositions.contains(new Position(x, y))
                            && !(x == startPosition.getX() && y == startPosition.getY())) {
                        visitedPositions.add(new Position(x, y));
                    }
                }
            } else {
                break;
            }
        }

        return visitedPositions;
    }

    /**
     * Method used for checking if a guard will end up in a loop.
     *
     * @param startPosition The initial position of the guard
     * @param space The map with obstacles and the guard
     *
     * @return True if guard ends up in a loop, false if not.
     */
    private boolean checkLoop(Position startPosition, String[][] space) {
        /* This list contains a log of the positions the guard visited and the
        * direction the guard was facing. The direction is important for determining
        * if the guard is moving in a loop. */
        List<Movement> movementLog = new ArrayList<>();

        int x = startPosition.getX();
        int y = startPosition.getY();

        Direction direction = getGuardDirection(space[y][x]);

        movementLog.add(new Movement(x, y, direction));

        while (true) {
            Position nextPosition = moveByOneInDirection(new Position(x, y), direction);

            if (!isOut(nextPosition, MAX_HEIGHT, MAX_WIDTH)) {
                if (obstacles.contains(space[nextPosition.getY()][nextPosition.getX()])) {
                    direction = turnGuardRight(direction);
                } else {
                    x = nextPosition.getX();
                    y = nextPosition.getY();

                    Movement movement = new Movement(x, y, direction);

                    /* If the guard was already at this position moving at the
                     * same direction, the guard is moving in a loop. */
                    if (movementLog.contains(movement)) return true;
                    else movementLog.add(movement);
                }
            } else {
                return false;
            }
        }
    }

    private Direction getGuardDirection(String guard) {
        return switch (guard) {
            case "^" -> Direction.UP;
            case "v" -> Direction.DOWN;
            case "<" -> Direction.LEFT;
            case ">" -> Direction.RIGHT;
            default -> throw new RuntimeException("Position does not match any known state");
        };
    }

    private Direction turnGuardRight(Direction oldDirection) {
        return switch (oldDirection) {
            case UP -> Direction.RIGHT;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
            case RIGHT -> Direction.DOWN;
        };
    }

    private Position moveByOneInDirection(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.getX(), position.getY() - 1);
            case DOWN -> new Position(position.getX(), position.getY() + 1);
            case LEFT -> new Position(position.getX() - 1, position.getY());
            case RIGHT -> new Position(position.getX() + 1, position.getY());
        };
    }

    private boolean isOut(Position position, int height, int width) {
        return position.getX() >= width
                || position.getX() < 0
                || position.getY() >= height
                || position.getY() < 0;
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
