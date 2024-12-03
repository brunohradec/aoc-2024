package me.bhradec.aoc2024.day3.part2;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    private boolean mulOpEnabled = true;

    private int executeInputOps(final String path) throws IOException {
        int result = 0;

        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            byte[] buff = new byte[3 + 1 + 3 + 1 + 3 + 1];

            int fileOffset = 0;
            int readChars;

            do {
                raf.seek(fileOffset);
                readChars = raf.read(buff);

                String buffString = new String(buff);

                log.debug("Read buffer: {}", buffString);

                InputOpResult inputOpResult = executeSubstringOps(buffString);

                if (inputOpResult.isOpFound()) {
                    fileOffset += inputOpResult.getOpSubstringLength();
                    result += inputOpResult.getOpResult();
                } else {
                    fileOffset++;
                }
            } while (readChars != -1);
        }

        return result;
    }

    private InputOpResult executeSubstringOps(final String str) {
        Pattern mulOpPattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
        Pattern doOpPattern = Pattern.compile("do\\(\\)");
        Pattern dontOpPattern = Pattern.compile("don't\\(\\)");

        Matcher mulOpMatcher = mulOpPattern.matcher(str);
        Matcher doOpMatcher = doOpPattern.matcher(str);
        Matcher dontOpMatcher = dontOpPattern.matcher(str);

        if (doOpMatcher.find()) {
            String opSubstring = doOpMatcher.group();
            log.debug("do op substring found: {}", opSubstring);
            this.mulOpEnabled = true;

            return new InputOpResult(
                    true,
                    opSubstring.length(),
                    0
            );
        }

        if (dontOpMatcher.find()) {
            String opSubstring = dontOpMatcher.group();
            log.debug("don't op substring found: {}", opSubstring);
            this.mulOpEnabled = false;

            return new InputOpResult(
                    true,
                    opSubstring.length(),
                    0
            );
        }

        if (mulOpMatcher.find()) {
            String opSubstring = mulOpMatcher.group();
            log.debug("mul op substring found: {}", opSubstring);
            return executeMulOp(opSubstring);
        }

        log.debug("Op substring not found");

        return new InputOpResult(
                false,
                0,
                0
        );
    }


    private InputOpResult executeMulOp(final String opSubstring) {
        int result = 1;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(opSubstring);

        while (matcher.find()) {
            result *= Integer.parseInt(matcher.group());
        }

        log.debug("mul op execution result: {}", result);

        return new InputOpResult(
                true,
                opSubstring.length(),
                mulOpEnabled ? result : 0
        );
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
            int result = solution.executeInputOps(inputPath);
            log.info("Result: {}", result);
        } catch (IOException exception) {
            log.error("Could not parse input", exception);
        }
    }
}
