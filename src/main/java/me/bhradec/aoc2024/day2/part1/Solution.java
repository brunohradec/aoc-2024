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

    private boolean isReportSafe(List<Integer> report) {
        List<Integer> ascReport = new ArrayList<>(report);
        ascReport.sort(Comparator.naturalOrder());

        List<Integer> descReport = new ArrayList<>(report);
        descReport.sort(Comparator.reverseOrder());

        if (!report.equals(ascReport) && !report.equals(descReport)) return false;

        for (int i = 0; i < report.size() - 1; i++) {
            int diff = Math.abs(report.get(i) - report.get(i + 1));
            if (diff < MIN_DIFF || diff > MAX_DIFF) return false;
        }

        return true;
    }

    private long countSafeReportsFromInput(String path) throws IOException {
        long safeReportCnt = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                List<Integer> report = Arrays.stream(line.split(" ")).map(Integer::parseInt).toList();
                if (isReportSafe(report)) safeReportCnt++;
                line = reader.readLine();
            }
        }

        return safeReportCnt;
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
            long safeReportCnt = solution.countSafeReportsFromInput(inputPath);
            log.info("Safe reports: {}", safeReportCnt);
        } catch (IOException exception) {
            log.error("Could not count safe reports", exception);
        }
    }
}
