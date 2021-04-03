package com.trustly.apigithub.dto;

public class GitHubDto {

    private String extension;
    private Long count = 1L;
    private Long lines;
    private Long Bytes;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getLines() {
        return lines;
    }

    public void setLines(Long lines) {
        this.lines = lines;
    }

    public Long getBytes() {
        return Bytes;
    }

    public void setBytes(Long bytes) {
        Bytes = bytes;
    }
}
