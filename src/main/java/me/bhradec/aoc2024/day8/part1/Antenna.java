package me.bhradec.aoc2024.day8.part1;

import java.util.Objects;

public class Antenna {
    private String frequency;
    private Location location;

    public Antenna(String frequency, Location location) {
        this.frequency = frequency;
        this.location = location;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Antenna antenna)) return false;
        return Objects.equals(frequency, antenna.frequency) && Objects.equals(location, antenna.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency, location);
    }
}
