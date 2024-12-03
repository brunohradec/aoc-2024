package me.bhradec.aoc2024.day3.part2;

public class InputOpResult {
    private boolean opFound;
    private int opSubstringLength;
    private int opResult;

    public InputOpResult(boolean opFound, int opSubstringLength, int opResult) {
        this.opFound = opFound;
        this.opSubstringLength = opSubstringLength;
        this.opResult = opResult;
    }

    public boolean isOpFound() {
        return opFound;
    }

    public void setOpFound(boolean opFound) {
        this.opFound = opFound;
    }

    public int getOpSubstringLength() {
        return opSubstringLength;
    }

    public void setOpSubstringLength(int opSubstringLength) {
        this.opSubstringLength = opSubstringLength;
    }

    public int getOpResult() {
        return opResult;
    }

    public void setOpResult(int opResult) {
        this.opResult = opResult;
    }
}
