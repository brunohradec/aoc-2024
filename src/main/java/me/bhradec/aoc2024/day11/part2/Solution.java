package me.bhradec.aoc2024.day11.part2;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private long blinkAndCountStonesFromInput(String path, int numberOfBlinks) throws IOException {
        List<Long> stones = parseStonesFromInput(path);
        log.debug("Stones: {}", stones);

        Map<Long, Long> countCache = new HashMap<>();

        /* Set the initial counter for every element to 1. If there are non-unique elements,
         * counter gets incremented by 1 for each non-unique instance. */
        for (Long stone : stones) {
            countCache.merge(stone, 1L, Long::sum);
        }

        for (int i = 0; i < numberOfBlinks; i++) {
            Map<Long, Long> tempCountCache = new HashMap<>();
            for (Long cachedStone : countCache.keySet()) {
                for (Long nextStone : nextStone(cachedStone)) {
                    /* For each stone that appeared once (1x) in a previous blink
                     * that created one new stone after a blink,
                     * for instance 1 changing to 2024,
                     * counter is kept as 1 as total number of stones didn't change by that stone,
                     * so 1x1 results in 1x2024. */

                    /* For each stone that appeared once (1x) in a previous blink
                     * that created 2 new stones after a blink,
                     * for instance 2020 that created 20 and 20,
                     * counter is first set to 1 for the first 20, and then incremented by 1 for the second 20,
                     * so 1x2020 results in 2x20. */

                    /* For each stone that appeared multiple times - for example twice (2x) in a previous blink
                     * where each of the stones created 2 new stones after a blink,
                     * for instance 2020 that created 20 and 20, and other 2020 that created 20 and 20,
                     * counter is first set to 2 for the first 20, and then incremented by 2 for the second 20,
                     * so 2x2020 results in 4x20. */

                    /* For each stone that appeared multiple times - for example twice (2x) in a previous blink
                     * where each of the stones created 1 new stone after a blink,
                     * for instance 1 that created 2024 and other 1 that also created 2024,
                     * counter is set to 2 for the 2024 created
                     * so 2x1 results in 2x2024. */
                    tempCountCache.merge(nextStone, countCache.get(cachedStone), Long::sum);
                }
            }
            countCache = tempCountCache;
        }

        return countCache.values().stream().reduce(0L, Long::sum);
    }

    private List<Long> parseStonesFromInput(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            return Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
        }
    }

    private List<Long> nextStone(Long stone) {
        if (stone.equals(0L)) {
            return List.of(1L);
        }

        String stoneStr = stone.toString();
        int strLength = stoneStr.length();

        if (strLength % 2 == 0) {
            return List.of(
                    Long.parseLong(stoneStr.substring(0, strLength / 2)),
                    Long.parseLong(stoneStr.substring(strLength / 2))
            );
        }

        return List.of(stone * 2024);
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
            log.info("Result: {}", solution.blinkAndCountStonesFromInput(inputPath, 75));
        } catch (IOException exception) {
            log.error("Could not parse input");
        }
    }
}
