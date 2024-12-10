package me.bhradec.aoc2024.day9.part2;

import java.util.List;
import java.util.Objects;

public class DiskBlock {
    String fid;
    List<String> content;

    public DiskBlock(String fid, List<String> content) {
        this.fid = fid;
        this.content = content;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DiskBlock{" +
                "fid='" + fid + '\'' +
                ", content=" + content +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DiskBlock diskBlock)) return false;
        return Objects.equals(fid, diskBlock.fid) && Objects.equals(content, diskBlock.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fid, content);
    }
}
