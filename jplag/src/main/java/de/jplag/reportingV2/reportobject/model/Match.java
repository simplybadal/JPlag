package de.jplag.reportingV2.reportobject.model;

public class Match {

    private final String first_file_name;
    private final String second_file_name;
    private final int start_in_first;
    private final int end_in_first;
    private final int start_in_second;
    private final int end_in_second;
    private final int tokens;

    public Match(String first_file_name, String second_file_name, int start_in_first, int end_in_first, int start_in_second, int end_in_second,
            int tokens) {
        this.first_file_name = first_file_name;
        this.second_file_name = second_file_name;
        this.start_in_first = start_in_first;
        this.end_in_first = end_in_first;
        this.start_in_second = start_in_second;
        this.end_in_second = end_in_second;
        this.tokens = tokens;
    }

    public String getFirst_file_name() {
        return first_file_name;
    }

    public String getSecond_file_name() {
        return second_file_name;
    }

    public int getStart_in_first() {
        return start_in_first;
    }

    public int getEnd_in_first() {
        return end_in_first;
    }

    public int getStart_in_second() {
        return start_in_second;
    }

    public int getEnd_in_second() {
        return end_in_second;
    }

    public int getTokens() {
        return tokens;
    }
}
